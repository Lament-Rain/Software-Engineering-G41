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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MOMyJobsController {
    @FXML private Label pageTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TableView<JobRowViewModel> jobsTable;
    @FXML private Label resultCountLabel;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private TextField workTimeField;
    @FXML private TextField recruitNumField;
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

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        departmentComboBox.getItems().addAll("Computer Science School", "Science School", "School of Foreign Languages", "School of Humanities", "School of Economics and Management", "School of Engineering");
        setupTable();
        jobsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showSelectedJob(n));
        jobsTable.setOnMouseClicked(this::handleTableDoubleClick);
    }

    public void setUser(MO user) {
        this.user = user;
        pageTitleLabel.setText("My Jobs");
        subtitleLabel.setText("You can only edit or withdraw jobs that you created.");
        loadJobs();
    }

    public void setStage(Stage stage) { this.stage = stage; }

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

    private void setupTable() {
        TableColumn<JobRowViewModel, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(220);
        TableColumn<JobRowViewModel, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(130);
        TableColumn<JobRowViewModel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);
        TableColumn<JobRowViewModel, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineCol.setPrefWidth(140);
        TableColumn<JobRowViewModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        TableColumn<JobRowViewModel, Integer> appCol = new TableColumn<>("Applications");
        appCol.setCellValueFactory(new PropertyValueFactory<>("applicationCount"));
        appCol.setPrefWidth(90);
        jobsTable.getColumns().setAll(titleCol, departmentCol, typeCol, deadlineCol, statusCol, appCol);
        jobsTable.setItems(rows);
    }

    private void loadJobs() {
        rows.clear();
        if (user == null) return;
        List<Job> jobs = JobService.getAllJobsByMO(user.getId());
        for (Job job : jobs) {
            rows.add(new JobRowViewModel(job.getId(), safeText(job.getTitle()), safeText(job.getDepartment()),
                    job.getType() == null ? "-" : job.getType().name(), formatDisplayDate(job.getDeadline()),
                    formatStatus(job.getStatus()), ApplicationService.getApplicationsByJob(job.getId()).size(), job));
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
        workTimeField.setText(safeEditText(selectedJob.getWorkTime()));
        recruitNumField.setText(selectedJob.getRecruitNum() <= 0 ? "" : String.valueOf(selectedJob.getRecruitNum()));
        deadlineField.setText(safeEditText(selectedJob.getDeadline()));
        descriptionArea.setText(safeEditText(selectedJob.getDescription()));
        skillsArea.setText(selectedJob.getSkills() == null ? "" : String.join(", ", selectedJob.getSkills()));
        saveButton.setDisable(false);
        withdrawButton.setDisable(selectedJob.getStatus() == model.JobStatus.CLOSED);
    }

    private void clearForm() {
        titleField.clear(); typeComboBox.setValue(null); departmentComboBox.setValue(null); workTimeField.clear();
        recruitNumField.clear(); deadlineField.clear(); descriptionArea.clear(); skillsArea.clear();
    }

    @FXML
    private void handleSaveChanges() {
        if (selectedJob == null) { showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select a job first."); return; }
        String title = text(titleField.getText());
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String workTime = text(workTimeField.getText());
        String recruitNumStr = text(recruitNumField.getText());
        String deadline = text(deadlineField.getText());
        String description = text(descriptionArea.getText());
        String skills = text(skillsArea.getText());
        if (title.isEmpty() || type == null || department == null || workTime.isEmpty() || recruitNumStr.isEmpty() || deadline.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Notice", "Please complete the job information before saving."); return; }
        int recruitNum;
        try { recruitNum = Integer.parseInt(recruitNumStr); if (recruitNum <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showAlert(Alert.AlertType.WARNING, "Notice", "The number of openings must be a number greater than 0."); return; }
        selectedJob.setTitle(title); selectedJob.setType(JobType.valueOf(type)); selectedJob.setDepartment(department);
        selectedJob.setWorkTime(workTime); selectedJob.setRecruitNum(recruitNum); selectedJob.setDeadline(deadline);
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

    @FXML
    private void handleOpenJobBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open job board: " + e.getMessage());
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
        private final int applicationCount;
        private final Job job;

        public JobRowViewModel(String jobId, String title, String department, String type, String deadline, String status, int applicationCount, Job job) {
            this.jobId = jobId;
            this.title = title;
            this.department = department;
            this.type = type;
            this.deadline = deadline;
            this.status = status;
            this.applicationCount = applicationCount;
            this.job = job;
        }

        public String getJobId() { return jobId; }
        public String getTitle() { return title; }
        public String getDepartment() { return department; }
        public String getType() { return type; }
        public String getDeadline() { return deadline; }
        public String getStatus() { return status; }
        public int getApplicationCount() { return applicationCount; }
        public Job getJob() { return job; }
    }
}
