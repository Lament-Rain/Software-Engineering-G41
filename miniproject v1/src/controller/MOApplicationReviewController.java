package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ApplicantReviewViewModel;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.MO;
import model.MOJobReviewViewModel;
import model.TA;
import service.ApplicationService;
import service.JobService;
import service.UserService;

import java.util.List;
import java.util.Optional;

public class MOApplicationReviewController {
    @FXML
    private ComboBox<MOJobReviewViewModel> jobSelector;
    @FXML
    private Label applicantCountLabel;
    @FXML
    private Label acceptedCountLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private TableView<ApplicantReviewViewModel> applicantsTable;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> nameColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> departmentColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> submittedAtColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> statusColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> matchScoreColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> resumeStatusColumn;
    @FXML
    private Label selectedJobLabel;
    @FXML
    private Label applicantNameLabel;
    @FXML
    private Label applicantStatusLabel;
    @FXML
    private Label applicantResumeLabel;
    @FXML
    private TextArea experienceArea;
    @FXML
    private TextArea coverLetterArea;
    @FXML
    private TextArea feedbackArea;
    @FXML
    private Button screenButton;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;

    private MO user;
    private Stage stage;
    private Job selectedJob;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        matchScoreColumn.setCellValueFactory(new PropertyValueFactory<>("matchScore"));
        resumeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("resumeStatus"));

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showApplicantDetails(newVal));
        jobSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedJob = JobService.getJobById(newVal.getJobId());
                loadApplicants();
            }
        });
    }

    public void setUser(MO user) {
        this.user = user;
        loadJobs();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loadJobs() {
        if (user == null) {
            return;
        }

        ObservableList<MOJobReviewViewModel> items = FXCollections.observableArrayList();
        for (Job job : JobService.getAllJobsByMO(user.getId())) {
            int count = ApplicationService.getApplicationsByJob(job.getId()).size();
            items.add(new MOJobReviewViewModel(
                    job.getId(),
                    job.getTitle(),
                    job.getDepartment(),
                    formatDate(job.getDeadline()),
                    String.valueOf(count)
            ));
        }
        jobSelector.setItems(items);
        if (!items.isEmpty()) {
            jobSelector.getSelectionModel().selectFirst();
        }
    }

    private void loadApplicants() {
        if (selectedJob == null) {
            return;
        }

        List<Application> applications = ApplicationService.getApplicationsByJob(selectedJob.getId());
        ObservableList<ApplicantReviewViewModel> rows = FXCollections.observableArrayList();

        int accepted = 0;
        int pending = 0;
        for (Application app : applications) {
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta == null) {
                continue;
            }
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                accepted++;
            }
            if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                pending++;
            }

            rows.add(new ApplicantReviewViewModel(
                    app.getId(),
                    ta.getId(),
                    ta.getName() != null && !ta.getName().isEmpty() ? ta.getName() : ta.getUsername(),
                    ta.getDepartment() != null ? ta.getDepartment() : "-",
                    formatDate(app.getCreatedAt()),
                    app.getStatus() != null ? app.getStatus().name() : "-",
                    String.format("%.2f%%", app.getMatchScore()),
                    ta.getResumePath() != null && !ta.getResumePath().isEmpty() ? "Uploaded" : "Not uploaded",
                    ta.getExperience() != null ? ta.getExperience() : "",
                    app.getCoverLetter() != null ? app.getCoverLetter() : "",
                    app.getReviewComment() != null ? app.getReviewComment() : ""
            ));
        }

        applicantsTable.setItems(rows);
        selectedJobLabel.setText(selectedJob.getTitle());
        applicantCountLabel.setText(String.valueOf(rows.size()));
        acceptedCountLabel.setText(String.valueOf(accepted));
        pendingCountLabel.setText(String.valueOf(pending));

        if (!rows.isEmpty()) {
            applicantsTable.getSelectionModel().selectFirst();
        } else {
            showApplicantDetails(null);
        }
    }

    private void showApplicantDetails(ApplicantReviewViewModel applicant) {
        if (applicant == null) {
            applicantNameLabel.setText("No applicant selected");
            applicantStatusLabel.setText("-");
            applicantResumeLabel.setText("-");
            experienceArea.setText("");
            coverLetterArea.setText("");
            feedbackArea.clear();
            screenButton.setDisable(true);
            acceptButton.setDisable(true);
            rejectButton.setDisable(true);
            return;
        }

        applicantNameLabel.setText(applicant.getName());
        applicantStatusLabel.setText(applicant.getStatus());
        applicantResumeLabel.setText(applicant.getResumeStatus());
        experienceArea.setText(applicant.getExperience());
        coverLetterArea.setText(applicant.getCoverLetter());
        feedbackArea.setText(applicant.getReviewComment());

        boolean finalStatus = "ACCEPTED".equals(applicant.getStatus()) || "REJECTED".equals(applicant.getStatus()) || "WITHDRAWN".equals(applicant.getStatus());
        screenButton.setDisable(finalStatus);
        acceptButton.setDisable(finalStatus);
        rejectButton.setDisable(finalStatus);
    }

    @FXML
    private void handleScreenApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        String feedback = feedbackArea.getText() != null ? feedbackArea.getText().trim() : "";
        if (feedback.isEmpty()) {
            feedback = "Shortlisted by MO";
        }

        boolean success = ApplicationService.screenApplication(selected.getApplicationId(), ApplicationStatus.SCREENED, feedback, user.getId());
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Action Failed", "Unable to mark this application as SCREENED.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Screened", "This application has been marked as SCREENED.");
        loadApplicants();
    }

    @FXML
    private void handleAcceptApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        boolean success = ApplicationService.acceptApplication(selected.getApplicationId(), user.getId());
        if (!success) {
            showAlert(Alert.AlertType.WARNING, "Acceptance Failed", "The position may have reached its acceptance limit, or the application status does not allow acceptance.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Accepted", "This application has been updated to ACCEPTED.");
        loadApplicants();
    }

    @FXML
    private void handleRejectApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        String feedback = feedbackArea.getText() != null ? feedbackArea.getText().trim() : "";
        if (feedback.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reject Application");
            dialog.setHeaderText("Enter rejection reason");
            dialog.setContentText("Reason:");
            Optional<String> input = dialog.showAndWait();
            if (!input.isPresent() || input.get().trim().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Please enter a rejection reason before continuing.");
                return;
            }
            feedback = input.get().trim();
        }

        boolean success = ApplicationService.rejectApplication(selected.getApplicationId(), feedback, user.getId());
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Rejection Failed", "This application cannot be rejected.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Rejected", "This application has been updated to REJECTED.");
        loadApplicants();
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
            stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back: " + e.getMessage());
        }
    }

    private String formatDate(String value) {
        if (value == null || value.isEmpty()) {
            return "-";
        }
        return value.replace('T', ' ');
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
