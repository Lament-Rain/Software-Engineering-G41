package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Job;
import model.JobType;
import model.MO;
import service.ApplicationService;
import service.JobService;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import controller.SearchBarController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class MOMyJobsController {
    @FXML private Label pageTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TableView<JobRowViewModel> jobsTable;
    @FXML private Label resultCountLabel;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private CheckBox monCheckBox;
    @FXML private TextField monHoursField;
    @FXML private CheckBox tueCheckBox;
    @FXML private TextField tueHoursField;
    @FXML private CheckBox wedCheckBox;
    @FXML private TextField wedHoursField;
    @FXML private CheckBox thuCheckBox;
    @FXML private TextField thuHoursField;
    @FXML private CheckBox friCheckBox;
    @FXML private TextField friHoursField;
    @FXML private CheckBox satCheckBox;
    @FXML private TextField satHoursField;
    @FXML private CheckBox sunCheckBox;
    @FXML private TextField sunHoursField;
    @FXML private TextField recruitNumField;
    @FXML private TextField startDateField;
    @FXML private TextField deadlineField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea skillsArea;
    @FXML private Label selectedJobLabel;
    @FXML private Label statusHintLabel;
    @FXML private Button saveButton;
    @FXML private Button withdrawButton;

    private MO user;
    private Stage stage;
    private final ObservableList<JobRowViewModel> rows = FXCollections.observableArrayList();
    private Job selectedJob;
    private KeyboardShortcutService shortcutService;
    private static final DateTimeFormatter UI_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        departmentComboBox.getItems().addAll("Computer Science School", "Science School", "School of Foreign Languages", "School of Humanities", "School of Economics and Management", "School of Engineering");
        setupTable();
        setupScheduleControls();
        jobsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showSelectedJob(n));
        jobsTable.setOnMouseClicked(this::handleTableDoubleClick);
    }

    public void setUser(MO user) {
        this.user = user;
        pageTitleLabel.setText("My Jobs");
        subtitleLabel.setText("You can only edit or withdraw jobs that you created.");
        loadJobs();
    }

    public void setStage(Stage stage) { 
        this.stage = stage;
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleBack);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }
    
    private void handleSearchAction() {
        showSearchBar();
    }

    private void showSearchBar() {
        try {
            // Create list of MO features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("My Jobs", () -> handleMyJobs()));
            features.add(new SearchBarController.Feature("Create Job", () -> handleCreateJob()));
            features.add(new SearchBarController.Feature("Review Applications", () -> handleReviewApplications()));
            features.add(new SearchBarController.Feature("Job Board", () -> handleOpenJobBoard()));
            features.add(new SearchBarController.Feature("Personal Center", this::handlePersonalCenter));

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            searchStage.initOwner(stage);
            searchStage.setTitle("Search Features");

            // Set up controller
            controller.setStage(searchStage);
            controller.setFeatures(features);
            controller.setOnFeatureSelected(featureName -> {
                // Find and execute the selected feature
                for (SearchBarController.Feature feature : features) {
                    if (feature.getName().equals(featureName)) {
                        feature.getAction().run();
                        break;
                    }
                }
            });

            // Create scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            searchStage.setScene(scene);



            // Show search bar
            searchStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load search bar: " + e.getMessage());
        }
    }

    private void handleMyJobs() {
        // Already on this page, do nothing
    }

    private void handleCreateJob() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOCreateJob", () -> {
                try {
                    FXMLLoader myJobsLoader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
                    Parent myJobsRoot = myJobsLoader.load();
                    MOMyJobsController myJobsController = myJobsLoader.getController();
                    myJobsController.setUser(user);
                    myJobsController.setStage(stage);
                    
                    Scene scene = new Scene(myJobsRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
                    
                    // Force layout update to ensure components resize properly
                    myJobsRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load my jobs page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Create Job");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load create job page: " + e.getMessage());
        }
    }

    private void handleReviewApplications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
            Parent root = loader.load();
            MOApplicationReviewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOApplicationReview", () -> {
                try {
                    FXMLLoader myJobsLoader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
                    Parent myJobsRoot = myJobsLoader.load();
                    MOMyJobsController myJobsController = myJobsLoader.getController();
                    myJobsController.setUser(user);
                    myJobsController.setStage(stage);
                    
                    Scene scene = new Scene(myJobsRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
                    
                    // Force layout update to ensure components resize properly
                    myJobsRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load my jobs page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application Review");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application review page: " + e.getMessage());
        }
    }

    @FXML
    private void handlePersonalCenter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOProfileView.fxml"));
            Parent root = loader.load();
            MOProfileViewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - MO Personal Center");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load personal center page: " + e.getMessage());
        }
    }

    public void openJob(String jobId) {
        loadJobs();
        if (jobId == null) return;
        for (JobRowViewModel row : rows) {
            if (jobId.equals(row.getJobId())) {
                jobsTable.getSelectionModel().select(row);
                jobsTable.scrollTo(row);
                showSelectedJob(row);
                break;
            }
        }
    }

    private void setupScheduleControls() {
        bindDayToggle(monCheckBox, monHoursField);
        bindDayToggle(tueCheckBox, tueHoursField);
        bindDayToggle(wedCheckBox, wedHoursField);
        bindDayToggle(thuCheckBox, thuHoursField);
        bindDayToggle(friCheckBox, friHoursField);
        bindDayToggle(satCheckBox, satHoursField);
        bindDayToggle(sunCheckBox, sunHoursField);
    }

    private void bindDayToggle(CheckBox checkBox, TextField hoursField) {
        checkBox.selectedProperty().addListener((obs, oldV, selected) -> {
            hoursField.setDisable(!selected);
            if (!selected) {
                hoursField.clear();
            }
        });
    }

    private int computeWeeklyWorkloadOrMinusOne() {
        int total = 0;
        int selectedDays = 0;

        Set<Integer> monPeriods = parsePeriods(monCheckBox, monHoursField); if (monCheckBox.isSelected()) selectedDays++;
        Set<Integer> tuePeriods = parsePeriods(tueCheckBox, tueHoursField); if (tueCheckBox.isSelected()) selectedDays++;
        Set<Integer> wedPeriods = parsePeriods(wedCheckBox, wedHoursField); if (wedCheckBox.isSelected()) selectedDays++;
        Set<Integer> thuPeriods = parsePeriods(thuCheckBox, thuHoursField); if (thuCheckBox.isSelected()) selectedDays++;
        Set<Integer> friPeriods = parsePeriods(friCheckBox, friHoursField); if (friCheckBox.isSelected()) selectedDays++;
        Set<Integer> satPeriods = parsePeriods(satCheckBox, satHoursField); if (satCheckBox.isSelected()) selectedDays++;
        Set<Integer> sunPeriods = parsePeriods(sunCheckBox, sunHoursField); if (sunCheckBox.isSelected()) selectedDays++;

        if ((monCheckBox.isSelected() && monPeriods == null) ||
            (tueCheckBox.isSelected() && tuePeriods == null) ||
            (wedCheckBox.isSelected() && wedPeriods == null) ||
            (thuCheckBox.isSelected() && thuPeriods == null) ||
            (friCheckBox.isSelected() && friPeriods == null) ||
            (satCheckBox.isSelected() && satPeriods == null) ||
            (sunCheckBox.isSelected() && sunPeriods == null)) {
            return -1;
        }

        total += monPeriods == null ? 0 : monPeriods.size();
        total += tuePeriods == null ? 0 : tuePeriods.size();
        total += wedPeriods == null ? 0 : wedPeriods.size();
        total += thuPeriods == null ? 0 : thuPeriods.size();
        total += friPeriods == null ? 0 : friPeriods.size();
        total += satPeriods == null ? 0 : satPeriods.size();
        total += sunPeriods == null ? 0 : sunPeriods.size();

        if (selectedDays == 0 || total <= 0) {
            return -1;
        }
        return total;
    }

    private Set<Integer> parsePeriods(CheckBox checkBox, TextField field) {
        if (!checkBox.isSelected()) {
            return new LinkedHashSet<>();
        }

        String raw = text(field.getText());
        if (raw.isEmpty()) {
            return null;
        }

        Set<Integer> periods = new LinkedHashSet<>();
        String[] parts = raw.split(",");
        for (String part : parts) {
            String v = part.trim();
            if (!v.matches("^\\d+$")) {
                return null;
            }
            int p = Integer.parseInt(v);
            if (p < 1 || p > 14) {
                return null;
            }
            periods.add(p);
        }
        return periods;
    }

    private String buildPeriodWorkTimeString() {
        StringBuilder sb = new StringBuilder("PERIODS|");
        appendDayPeriods(sb, "Sun", sunCheckBox, sunHoursField);
        appendDayPeriods(sb, "Mon", monCheckBox, monHoursField);
        appendDayPeriods(sb, "Tue", tueCheckBox, tueHoursField);
        appendDayPeriods(sb, "Wed", wedCheckBox, wedHoursField);
        appendDayPeriods(sb, "Thu", thuCheckBox, thuHoursField);
        appendDayPeriods(sb, "Fri", friCheckBox, friHoursField);
        appendDayPeriods(sb, "Sat", satCheckBox, satHoursField);
        if (sb.charAt(sb.length() - 1) == '|') {
            return "PERIODS|";
        }
        if (sb.charAt(sb.length() - 1) == ';') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void appendDayPeriods(StringBuilder sb, String day, CheckBox checkBox, TextField field) {
        if (!checkBox.isSelected()) {
            return;
        }
        Set<Integer> periods = parsePeriods(checkBox, field);
        if (periods == null || periods.isEmpty()) {
            return;
        }
        sb.append(day)
                .append(":")
                .append(periods.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .append(";");
    }

    private void setupTable() {
        TableColumn<JobRowViewModel, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(220);
        titleCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(130);
        departmentCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);
        typeCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineCol.setPrefWidth(140);
        deadlineCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, String> headcountCol = new TableColumn<>("Headcount");
        headcountCol.setCellValueFactory(new PropertyValueFactory<>("headcount"));
        headcountCol.setPrefWidth(110);
        headcountCol.setCellFactory(col -> createAlignedTextCell());
        TableColumn<JobRowViewModel, Integer> appCol = new TableColumn<>("Applications");
        appCol.setCellValueFactory(new PropertyValueFactory<>("applicationCount"));
        appCol.setPrefWidth(90);
        appCol.setCellFactory(col -> createAlignedNumberCell());
        jobsTable.getColumns().setAll(titleCol, departmentCol, typeCol, deadlineCol, statusCol, headcountCol, appCol);
        jobsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        jobsTable.setItems(rows);
    }

    private TableCell<JobRowViewModel, String> createAlignedTextCell() {
        return new TableCell<JobRowViewModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
                }
                setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
        };
    }

    private TableCell<JobRowViewModel, Integer> createAlignedNumberCell() {
        return new TableCell<JobRowViewModel, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.valueOf(item));
                setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
        };
    }

    private void loadJobs() {
        rows.clear();
        if (user == null) return;
        List<Job> jobs = JobService.getAllJobsByMO(user.getId());
        for (Job job : jobs) {
            rows.add(new JobRowViewModel(job.getId(), safeText(job.getTitle()), safeText(job.getDepartment()),
                    job.getType() == null ? "-" : job.getType().name(), formatDisplayDate(job.getDeadline()),
                    formatStatus(job.getStatus()), job.getCurrentNum() + " / " + job.getRecruitNum(), ApplicationService.getApplicationsByJob(job.getId()).size(), job));
        }
        resultCountLabel.setText("Results: " + rows.size() + " jobs");
        if (!rows.isEmpty() && jobsTable.getSelectionModel().getSelectedItem() == null) {
            jobsTable.getSelectionModel().selectFirst();
            showSelectedJob(jobsTable.getSelectionModel().getSelectedItem());
        } else if (rows.isEmpty()) {
            showSelectedJob(null);
        }
    }

    private void showSelectedJob(JobRowViewModel row) {
        if (row == null) {
            selectedJob = null;
            selectedJobLabel.setText("No job selected");
            statusHintLabel.setText("Please select a job from the list on the left.");
            clearForm();
            saveButton.setDisable(true);
            withdrawButton.setDisable(true);
            return;
        }
        selectedJob = row.getJob();
        selectedJobLabel.setText(row.getTitle());
        statusHintLabel.setText("Current status: " + row.getStatus() + ". After saving, the job will return to pending review; withdrawing it will change the status to closed. You can only manage jobs that you created.");
        titleField.setText(safeEditText(selectedJob.getTitle()));
        typeComboBox.setValue(selectedJob.getType() == null ? null : selectedJob.getType().name());
        departmentComboBox.setValue(safeEditText(selectedJob.getDepartment()));
        clearScheduleInputs();
        boolean parsedSchedule = applyScheduleFromJob(selectedJob);
        if (!parsedSchedule) {
            String workloadText = safeEditText(selectedJob.getWorkTime());
            if (!workloadText.isEmpty() && workloadText.matches("^\\d+$")) {
                monCheckBox.setSelected(true);
                monHoursField.setDisable(false);
                monHoursField.setText(workloadText);
            }
            statusHintLabel.setText(statusHintLabel.getText() + " Legacy data detected: schedule details were not stored, so workload is prefilled on Monday. Please confirm and adjust weekdays/periods.");
        }
        recruitNumField.setText(selectedJob.getRecruitNum() <= 0 ? "" : String.valueOf(selectedJob.getRecruitNum()));
        deadlineField.setText(formatDisplayDate(safeEditText(selectedJob.getDeadline())));
        if (!parsedSchedule) {
            startDateField.setText(formatDisplayDate(safeEditText(selectedJob.getCreatedAt())));
        }
        descriptionArea.setText(safeEditText(selectedJob.getDescription()));
        skillsArea.setText(selectedJob.getSkills() == null ? "" : String.join(", ", selectedJob.getSkills()));
        saveButton.setDisable(false);
        withdrawButton.setDisable(selectedJob.getStatus() == model.JobStatus.CLOSED);
    }

    private void clearForm() {
        titleField.clear(); typeComboBox.setValue(null); departmentComboBox.setValue(null); clearScheduleInputs();
        recruitNumField.clear(); startDateField.clear(); deadlineField.clear(); descriptionArea.clear(); skillsArea.clear();
    }

    @FXML
    private void handleSaveChanges() {
        if (selectedJob == null) { showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select a job first."); return; }
        String title = text(titleField.getText());
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String recruitNumStr = text(recruitNumField.getText());
        String startDateText = text(startDateField.getText());
        String deadline = text(deadlineField.getText());
        String description = text(descriptionArea.getText());
        String skills = text(skillsArea.getText());
        if (title.isEmpty() || type == null || department == null || recruitNumStr.isEmpty() || startDateText.isEmpty() || deadline.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Notice", "Please complete the job information before saving."); return; }
        int recruitNum;
        int weeklyWorkload = computeWeeklyWorkloadOrMinusOne();
        if (weeklyWorkload <= 0) {
            showAlert(Alert.AlertType.WARNING, "Notice", "Please select weekdays and enter valid periods (1-14, comma-separated) for each selected day.");
            return;
        }
        try {
            LocalDate startDate = LocalDate.parse(startDateText, UI_DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(deadline, UI_DATE_FORMATTER);
            if (endDate.isBefore(startDate)) {
                showAlert(Alert.AlertType.WARNING, "Notice", "End date must be on or after start date.");
                return;
            }
            deadline = endDate.toString();
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.WARNING, "Notice", "Start/End date must be MM-DD-YYYY.");
            return;
        }
        try { recruitNum = Integer.parseInt(recruitNumStr); if (recruitNum <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showAlert(Alert.AlertType.WARNING, "Notice", "The number of openings must be a number greater than 0."); return; }
        selectedJob.setTitle(title); selectedJob.setType(JobType.valueOf(type)); selectedJob.setDepartment(department);
        selectedJob.setWorkTime(buildPeriodWorkTimeString()); selectedJob.setRecruitNum(recruitNum); selectedJob.setDeadline(deadline);
        selectedJob.setExtraRequirements(buildScheduleSummary(startDateText, text(deadlineField.getText())));
        selectedJob.setDescription(description); selectedJob.setSkills(parseSkills(skills)); selectedJob.setStatus(model.JobStatus.PENDING);
        if (!JobService.updateJobByMO(selectedJob, user.getId())) { showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save the job. Please try again later."); return; }
        showAlert(Alert.AlertType.INFORMATION, "Saved", "The job has been updated and sent back for review.");
        openJob(selectedJob.getId());
    }

    @FXML
    private void handleWithdrawJob() {
        if (selectedJob == null) { showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select a job first."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Withdraw");
        confirm.setHeaderText("Withdraw job: " + selectedJob.getTitle());
        confirm.setContentText("After withdrawing, the job status will change to Closed. Do you want to continue?");
        confirm.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        if (!JobService.closeJob(selectedJob.getId(), user.getId())) { showAlert(Alert.AlertType.ERROR, "Withdraw Failed", "You can only withdraw jobs that you created. Please try again later."); return; }
        showAlert(Alert.AlertType.INFORMATION, "Withdrawn", "The job has been withdrawn and marked as closed.");
        openJob(selectedJob.getId());
    }

    @FXML
    private void handleBack() {
        // If we can go back, do so; otherwise, go to dashboard
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                Parent root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser(user);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Module Organizer Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleOpenJobBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("JobList", () -> {
                try {
                    FXMLLoader myJobsLoader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
                    Parent myJobsRoot = myJobsLoader.load();
                    MOMyJobsController myJobsController = myJobsLoader.getController();
                    myJobsController.setUser(user);
                    myJobsController.setStage(stage);
                    
                    Scene scene = new Scene(myJobsRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
                    
                    // Force layout update to ensure components resize properly
                    myJobsRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load my jobs page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open job board: " + e.getMessage());
        }
    }

    @FXML
    private void handleReviewApplicationsNav() {
        handleReviewApplications();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void handleTableDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            JobRowViewModel row = jobsTable.getSelectionModel().getSelectedItem();
            if (row != null) {
                showSelectedJob(row);
            }
        }
    }

    private void clearScheduleInputs() {
        monCheckBox.setSelected(false); monHoursField.clear(); monHoursField.setDisable(true);
        tueCheckBox.setSelected(false); tueHoursField.clear(); tueHoursField.setDisable(true);
        wedCheckBox.setSelected(false); wedHoursField.clear(); wedHoursField.setDisable(true);
        thuCheckBox.setSelected(false); thuHoursField.clear(); thuHoursField.setDisable(true);
        friCheckBox.setSelected(false); friHoursField.clear(); friHoursField.setDisable(true);
        satCheckBox.setSelected(false); satHoursField.clear(); satHoursField.setDisable(true);
        sunCheckBox.setSelected(false); sunHoursField.clear(); sunHoursField.setDisable(true);
    }

    private boolean applyScheduleFromJob(Job job) {
        String workTime = safeEditText(job.getWorkTime());
        if (workTime.startsWith("PERIODS|")) {
            String payload = workTime.substring("PERIODS|".length());
            boolean hasDay = false;
            String[] dayEntries = payload.split(";");
            for (String entry : dayEntries) {
                if (entry == null || entry.trim().isEmpty() || !entry.contains(":")) {
                    continue;
                }
                String[] kv = entry.split(":", 2);
                String day = kv[0].trim();
                String periods = kv[1].trim();
                if (periods.isEmpty()) {
                    continue;
                }
                setDay(day, periods);
                hasDay = true;
            }

            String extra = safeEditText(job.getExtraRequirements());
            if (extra.startsWith("Schedule[")) {
                String body = extra.substring("Schedule[".length(), extra.endsWith("]") ? extra.length() - 1 : extra.length());
                String[] sections = body.split("; ");
                for (String section : sections) {
                    if (section.startsWith("Start=")) {
                        startDateField.setText(section.substring("Start=".length()).trim());
                    }
                }
            }
            return hasDay;
        }

        String extra = safeEditText(job.getExtraRequirements());
        if (!extra.startsWith("Schedule[")) {
            return false;
        }
        String body = extra.substring("Schedule[".length(), extra.endsWith("]") ? extra.length() - 1 : extra.length());
        String[] sections = body.split("; ");
        if (sections.length < 3) {
            return false;
        }
        boolean hasDay = false;
        String daysPart = sections[0];
        String[] dayItems = daysPart.split(", ");
        for (String item : dayItems) {
            String[] kv = item.split(":");
            if (kv.length != 2 || !kv[1].endsWith("h")) {
                continue;
            }
            String day = kv[0].trim();
            String hours = kv[1].substring(0, kv[1].length() - 1).trim();
            if (!hours.matches("^\\d+$")) {
                continue;
            }
            setDay(day, hours);
            hasDay = true;
        }

        for (String section : sections) {
            if (section.startsWith("Start=")) {
                startDateField.setText(section.substring("Start=".length()).trim());
            }
        }
        return hasDay;
    }

    private void setDay(String day, String periodsOrHours) {
        switch (day) {
            case "Mon": monCheckBox.setSelected(true); monHoursField.setDisable(false); monHoursField.setText(periodsOrHours); break;
            case "Tue": tueCheckBox.setSelected(true); tueHoursField.setDisable(false); tueHoursField.setText(periodsOrHours); break;
            case "Wed": wedCheckBox.setSelected(true); wedHoursField.setDisable(false); wedHoursField.setText(periodsOrHours); break;
            case "Thu": thuCheckBox.setSelected(true); thuHoursField.setDisable(false); thuHoursField.setText(periodsOrHours); break;
            case "Fri": friCheckBox.setSelected(true); friHoursField.setDisable(false); friHoursField.setText(periodsOrHours); break;
            case "Sat": satCheckBox.setSelected(true); satHoursField.setDisable(false); satHoursField.setText(periodsOrHours); break;
            case "Sun": sunCheckBox.setSelected(true); sunHoursField.setDisable(false); sunHoursField.setText(periodsOrHours); break;
            default: break;
        }
    }

    private String buildScheduleSummary(String startDateText, String endDateText) {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule[");
        appendDay(sb, "Mon", monCheckBox, monHoursField);
        appendDay(sb, "Tue", tueCheckBox, tueHoursField);
        appendDay(sb, "Wed", wedCheckBox, wedHoursField);
        appendDay(sb, "Thu", thuCheckBox, thuHoursField);
        appendDay(sb, "Fri", friCheckBox, friHoursField);
        appendDay(sb, "Sat", satCheckBox, satHoursField);
        appendDay(sb, "Sun", sunCheckBox, sunHoursField);
        sb.append("; Start=").append(startDateText).append("; End=").append(endDateText).append("]");
        return sb.toString();
    }

    private void appendDay(StringBuilder sb, String day, CheckBox checkBox, TextField field) {
        if (!checkBox.isSelected()) {
            return;
        }
        if (sb.charAt(sb.length() - 1) != '[') {
            sb.append(", ");
        }
        sb.append(day).append(":").append(field.getText().trim());
    }

    private List<String> parseSkills(String value) {
        List<String> result = new ArrayList<>();
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }

    private String text(String value) { return value == null ? "" : value.trim(); }
    private String safeText(String value) { return value == null || value.isBlank() || "null".equalsIgnoreCase(value) ? "-" : value; }
    private String safeEditText(String value) { return value == null || "null".equalsIgnoreCase(value) ? "" : value; }

    private String formatStatus(model.JobStatus status) {
        if (status == null) return "Unknown";
        switch (status) {
            case DRAFT: return "Draft";
            case PENDING: return "Pending Review";
            case PUBLISHED: return "Published";
            case REJECTED: return "Rejected";
            case CLOSED: return "Closed";
            default: return status.name();
        }
    }

    private String formatDisplayDate(String value) {
        if (value == null) return "-";
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) return "-";
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(trimmed);
            return String.format(Locale.ROOT, "%02d-%02d-%04d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
        } catch (java.time.format.DateTimeParseException ignored) {}
        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(trimmed);
            java.time.LocalDate date = dateTime.toLocalDate();
            return String.format(Locale.ROOT, "%02d-%02d-%04d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
        } catch (java.time.format.DateTimeParseException ignored) {}
        String[] parts = trimmed.split("-");
        if (parts.length >= 3) {
            try {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                String dayPart = parts[2];
                int tIndex = dayPart.indexOf('T');
                if (tIndex >= 0) dayPart = dayPart.substring(0, tIndex);
                int day = Integer.parseInt(dayPart);
                return String.format(Locale.ROOT, "%02d-%02d-%04d", month, day, year);
            } catch (NumberFormatException ignored) {}
        }
        return trimmed;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    public static class JobRowViewModel {
        private final String jobId;
        private final String title;
        private final String department;
        private final String type;
        private final String deadline;
        private final String status;
        private final String headcount;
        private final int applicationCount;
        private final Job job;

        public JobRowViewModel(String jobId, String title, String department, String type, String deadline, String status, String headcount, int applicationCount, Job job) {
            this.jobId = jobId;
            this.title = title;
            this.department = department;
            this.type = type;
            this.deadline = deadline;
            this.status = status;
            this.headcount = headcount;
            this.applicationCount = applicationCount;
            this.job = job;
        }

        public String getJobId() { return jobId; }
        public String getTitle() { return title; }
        public String getDepartment() { return department; }
        public String getType() { return type; }
        public String getDeadline() { return deadline; }
        public String getStatus() { return status; }
        public String getHeadcount() { return headcount; }
        public int getApplicationCount() { return applicationCount; }
        public Job getJob() { return job; }
    }
}
