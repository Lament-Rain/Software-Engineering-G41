package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Admin;
import model.Job;
import model.JobType;
import model.MO;
import model.UserRole;
import service.FormValidationService;
import service.JobService;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import service.ToastService;
import controller.SearchBarController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MOCreateJobController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private CheckBox monCheckBox;
    @FXML
    private TextField monHoursField;
    @FXML
    private CheckBox tueCheckBox;
    @FXML
    private TextField tueHoursField;
    @FXML
    private CheckBox wedCheckBox;
    @FXML
    private TextField wedHoursField;
    @FXML
    private CheckBox thuCheckBox;
    @FXML
    private TextField thuHoursField;
    @FXML
    private CheckBox friCheckBox;
    @FXML
    private TextField friHoursField;
    @FXML
    private CheckBox satCheckBox;
    @FXML
    private TextField satHoursField;
    @FXML
    private CheckBox sunCheckBox;
    @FXML
    private TextField sunHoursField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField recruitNumField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private Label errorMessage;

    @FXML
    private Label titleError;
    @FXML
    private Label typeError;
    @FXML
    private Label departmentError;
    @FXML
    private Label workTimeError;
    @FXML
    private Label recruitNumError;
    @FXML
    private Label deadlineError;
    @FXML
    private Label startDateError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label skillsError;
    @FXML
    private Button submitButton;

    private MO moUser;
    private Admin adminUser;
    private UserRole userRole;
    private Stage stage;
    private KeyboardShortcutService shortcutService;
    private static final DateTimeFormatter UI_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public void setUser(MO user) {
        this.moUser = user;
        this.userRole = UserRole.MO;
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName());
        }
    }

    public void setAdminUser(Admin admin) {
        this.adminUser = admin;
        this.userRole = UserRole.ADMIN;
        if (admin != null) {
            welcomeLabel.setText("Welcome, Admin " + admin.getUsername());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        departmentComboBox.getItems().addAll(
                "Computer Science School",
                "Science School",
                "School of Foreign Languages",
                "School of Humanities",
                "School of Economics and Management",
                "School of Engineering"
        );

        setupFieldValidation();
    }

    private void setupFieldValidation() {
        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateTitleField();
        });

        setupScheduleControls();

        recruitNumField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateRecruitNumField();
        });

        startDatePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateStartDateField();
        });

        deadlinePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateDeadlineField();
        });

        descriptionArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateDescriptionField();
        });

        skillsArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateSkillsField();
        });
    }

    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+s", this::handleSubmitAction);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleCancelAction);

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
            ToastService.showToast(stage, "Failed to load search bar: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private void handleMyJobs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
            Parent root = loader.load();
            MOMyJobsController controller = loader.getController();
            controller.setUser(moUser);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOMyJobs", () -> {
                try {
                    FXMLLoader createJobLoader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
                    Parent createJobRoot = createJobLoader.load();
                    MOCreateJobController createJobController = createJobLoader.getController();
                    createJobController.setUser(moUser);
                    createJobController.setStage(stage);
                    
                    Scene scene = new Scene(createJobRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Create Job");
                    
                    // Force layout update to ensure components resize properly
                    createJobRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastService.showToast(stage, "Failed to load create job page: " + e.getMessage(), ToastService.ToastType.ERROR);
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load my jobs page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private void handleCreateJob() {
        // Already on this page, do nothing
    }

    @FXML
    private void handleJobBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(moUser, model.UserRole.MO);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load job board: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleReviewApplications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
            Parent root = loader.load();
            MOApplicationReviewController controller = loader.getController();
            controller.setUser(moUser);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOApplicationReview", () -> {
                try {
                    FXMLLoader createJobLoader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
                    Parent createJobRoot = createJobLoader.load();
                    MOCreateJobController createJobController = createJobLoader.getController();
                    createJobController.setUser(moUser);
                    createJobController.setStage(stage);
                    
                    Scene scene = new Scene(createJobRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Create Job");
                    
                    // Force layout update to ensure components resize properly
                    createJobRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastService.showToast(stage, "Failed to load create job page: " + e.getMessage(), ToastService.ToastType.ERROR);
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application Review");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load application review page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handlePersonalCenter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOProfileView.fxml"));
            Parent root = loader.load();
            MOProfileViewController controller = loader.getController();
            controller.setUser(moUser);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - MO Personal Center");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load personal center page: " + e.getMessage(), ToastService.ToastType.ERROR);
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

    private void bindDayToggle(CheckBox dayCheckBox, TextField hoursField) {
        dayCheckBox.selectedProperty().addListener((obs, oldValue, selected) -> {
            hoursField.setDisable(!selected);
            if (!selected) {
                hoursField.clear();
            }
            validateWorkTimeField();
        });
        hoursField.focusedProperty().addListener((obs, oldV, focused) -> {
            if (!focused) {
                validateWorkTimeField();
            }
        });
    }

    private boolean validateStartDateField() {
        LocalDate value = startDatePicker.getValue();
        if (value == null) {
            FormValidationService.showError(startDatePicker, startDateError, "Start date is required");
            return false;
        }
        FormValidationService.clearError(startDatePicker, startDateError);
        return true;
    }

    private Integer computeWeeklyWorkload() {
        Map<CheckBox, TextField> dayPeriodMap = new LinkedHashMap<>();
        dayPeriodMap.put(monCheckBox, monHoursField);
        dayPeriodMap.put(tueCheckBox, tueHoursField);
        dayPeriodMap.put(wedCheckBox, wedHoursField);
        dayPeriodMap.put(thuCheckBox, thuHoursField);
        dayPeriodMap.put(friCheckBox, friHoursField);
        dayPeriodMap.put(satCheckBox, satHoursField);
        dayPeriodMap.put(sunCheckBox, sunHoursField);

        int total = 0;
        boolean anySelected = false;
        for (Map.Entry<CheckBox, TextField> entry : dayPeriodMap.entrySet()) {
            if (!entry.getKey().isSelected()) {
                continue;
            }
            anySelected = true;
            Set<Integer> periods = parsePeriods(entry.getValue().getText());
            if (periods == null || periods.isEmpty()) {
                return null;
            }
            total += periods.size();
        }

        if (!anySelected) {
            return null;
        }
        return total;
    }

    private Set<Integer> parsePeriods(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        Set<Integer> periods = new LinkedHashSet<>();
        String[] parts = raw.split(",");
        for (String part : parts) {
            String value = part.trim();
            if (value.isEmpty() || !value.matches("^\\d+$")) {
                return null;
            }
            int p = Integer.parseInt(value);
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
        Set<Integer> periods = parsePeriods(field.getText());
        if (periods == null || periods.isEmpty()) {
            return;
        }
        sb.append(day)
          .append(":")
          .append(periods.stream().map(String::valueOf).collect(Collectors.joining(",")))
          .append(";");
    }

    private boolean validateTitleField() {
        FormValidationService.ValidationResult result = FormValidationService.validateMinLength(titleField.getText(), 3, "Job title");
        if (!result.isValid()) {
            FormValidationService.showError(titleField, titleError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(titleField, titleError);
            return true;
        }
    }

    private boolean validateTypeField() {
        FormValidationService.ValidationResult result = FormValidationService.validateComboBox(typeComboBox.getValue(), "Job type");
        if (!result.isValid()) {
            FormValidationService.showError(typeComboBox, typeError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(typeComboBox, typeError);
            return true;
        }
    }

    private boolean validateDepartmentField() {
        FormValidationService.ValidationResult result = FormValidationService.validateComboBox(departmentComboBox.getValue(), "Department");
        if (!result.isValid()) {
            FormValidationService.showError(departmentComboBox, departmentError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(departmentComboBox, departmentError);
            return true;
        }
    }

    private boolean validateWorkTimeField() {
        Integer weekly = computeWeeklyWorkload();
        if (weekly == null) {
            FormValidationService.showError(monHoursField, workTimeError, "Select weekdays and enter valid periods (1-14, comma-separated) for each selected day");
            return false;
        }
        FormValidationService.clearError(monHoursField, workTimeError);
        return true;
    }

    private boolean validateRecruitNumField() {
        FormValidationService.ValidationResult result = FormValidationService.validatePositiveInteger(recruitNumField.getText(), "Number of openings");
        if (!result.isValid()) {
            FormValidationService.showError(recruitNumField, recruitNumError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(recruitNumField, recruitNumError);
            return true;
        }
    }

    private boolean validateDeadlineField() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = deadlinePicker.getValue();
        if (endDate == null) {
            FormValidationService.showError(deadlinePicker, deadlineError, "End date is required");
            return false;
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            FormValidationService.showError(deadlinePicker, deadlineError, "End date must be on or after start date");
            return false;
        }
        FormValidationService.clearError(deadlinePicker, deadlineError);
        return true;
    }

    private boolean validateDescriptionField() {
        FormValidationService.ValidationResult result = FormValidationService.validateMinLength(descriptionArea.getText(), 10, "Job description");
        if (!result.isValid()) {
            FormValidationService.showError(descriptionArea, descriptionError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(descriptionArea, descriptionError);
            return true;
        }
    }

    private boolean validateSkillsField() {
        FormValidationService.ValidationResult result = FormValidationService.validateRequired(skillsArea.getText(), "Required skills");
        if (!result.isValid()) {
            FormValidationService.showError(skillsArea, skillsError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(skillsArea, skillsError);
            return true;
        }
    }

    private boolean validateAllFields() {
        boolean valid = true;

        if (!validateTitleField()) valid = false;
        if (!validateTypeField()) valid = false;
        if (!validateDepartmentField()) valid = false;
        if (!validateWorkTimeField()) valid = false;
        if (!validateRecruitNumField()) valid = false;
        if (!validateStartDateField()) valid = false;
        if (!validateDeadlineField()) valid = false;
        if (!validateDescriptionField()) valid = false;
        if (!validateSkillsField()) valid = false;

        return valid;
    }

    private void clearAllErrors() {
        FormValidationService.clearError(titleField, titleError);
        FormValidationService.clearError(typeComboBox, typeError);
        FormValidationService.clearError(departmentComboBox, departmentError);
        FormValidationService.clearError(monHoursField, workTimeError);
        FormValidationService.clearError(recruitNumField, recruitNumError);
        FormValidationService.clearError(startDatePicker, startDateError);
        FormValidationService.clearError(deadlinePicker, deadlineError);
        FormValidationService.clearError(descriptionArea, descriptionError);
        FormValidationService.clearError(skillsArea, skillsError);
        errorMessage.setText("");
    }

    @FXML
    private void handleSubmit() {
        handleSubmitAction();
    }

    private void handleSubmitAction() {
        clearAllErrors();

        if (!validateAllFields()) {
            ToastService.showToast(stage, "Please fix the errors before submitting", ToastService.ToastType.WARNING);
            return;
        }

        String title = titleField.getText();
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String recruitNumStr = recruitNumField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = deadlinePicker.getValue();
        String description = descriptionArea.getText();
        String skills = skillsArea.getText();

        Integer weeklyWorkload = computeWeeklyWorkload();
        if (weeklyWorkload == null) {
            FormValidationService.showError(monHoursField, workTimeError, "Select weekdays and enter valid periods (1-14, comma-separated) for each selected day");
            ToastService.showToast(stage, "Please complete weekly schedule periods", ToastService.ToastType.WARNING);
            return;
        }

        if (startDate == null || endDate == null) {
            ToastService.showToast(stage, "Please select both start date and end date", ToastService.ToastType.WARNING);
            return;
        }

        if (endDate.isBefore(startDate)) {
            FormValidationService.showError(deadlinePicker, deadlineError, "End date must be on or after start date");
            ToastService.showToast(stage, "End date must be on or after start date", ToastService.ToastType.WARNING);
            return;
        }

        submitButton.setDisable(true);
        submitButton.setText("Publishing...");

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(800), event -> {
            try {
                int recruitNum = Integer.parseInt(recruitNumStr);
                List<String> skillsList = java.util.Arrays.asList(skills.split(","));
                String salary = "";
                String location = "";
                String startDateText = startDate.format(UI_DATE_FORMATTER);
                String endDateText = endDate.format(UI_DATE_FORMATTER);
                String extraRequirements = buildScheduleSummary(startDateText, endDateText);
                String weeklyWorkTime = buildPeriodWorkTimeString();

                Job job = null;
                if (userRole == UserRole.MO && moUser != null) {
                    job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, weeklyWorkTime, recruitNum, endDate.toString(), salary, location, extraRequirements, moUser.getId(), "MO", moUser.getName());
                } else if (userRole == UserRole.ADMIN && adminUser != null) {
                    job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, weeklyWorkTime, recruitNum, endDate.toString(), salary, location, extraRequirements, adminUser.getId(), "ADMIN", adminUser.getUsername());
                }

                if (job != null) {
                    ToastService.showToast(stage, "Job published successfully!", ToastService.ToastType.SUCCESS);
                    submitButton.setDisable(false);
                    submitButton.setText("Publish Job");
                    handleHome();
                } else {
                    ToastService.showToast(stage, "Failed to publish job. Please try again.", ToastService.ToastType.ERROR);
                    submitButton.setDisable(false);
                    submitButton.setText("Publish Job");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastService.showToast(stage, "An error occurred: " + e.getMessage(), ToastService.ToastType.ERROR);
                submitButton.setDisable(false);
                submitButton.setText("Publish Job");
            }
        }));
        timeline.play();
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
        sb.append(day).append(":").append(field.getText().trim()).append("h");
    }

    @FXML
    private void handleCancel() {
        handleCancelAction();
    }

    private void handleCancelAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Any unsaved changes will be lost.");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                handleHome();
            }
        });
    }

    @FXML
    private void handleHome() {
        try {
            if (userRole == UserRole.MO && moUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                Parent root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser(moUser);
                controller.setStage(stage);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
            } else if (userRole == UserRole.ADMIN && adminUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser(adminUser);
                controller.setStage(stage);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to return home: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        NavigationHistory.getInstance().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            // Save current window state
            boolean isFullScreen = stage.isFullScreen();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Create new scene without hardcoded size
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Apply saved window state
            stage.setScene(scene);
            if (isFullScreen) {
                stage.setFullScreen(true);
            } else if (currentWidth > 0 && currentHeight > 0) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            } else {
                // Default size if no previous size
                stage.setWidth(800);
                stage.setHeight(600);
            }
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load login page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }
}
