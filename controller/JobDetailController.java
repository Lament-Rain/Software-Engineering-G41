package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;
import model.Application;
import model.Job;
import model.MO;
import model.ProfileStatus;
import model.TA;
import model.UserRole;
import service.ApplicationService;
import service.JobService;
import service.WorkloadService;
import service.AdminConfigService;
import model.AdminConfig;

import java.util.List;

public class JobDetailController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label workTimeLabel;
    @FXML
    private Label recruitNumLabel;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Label salaryLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label publishDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private TextArea extraRequirementsArea;
    @FXML
    private Button applyButton;
    @FXML
    private Button backButton;

    private Job currentJob;
    private Object currentUser;
    private UserRole userRole;
    private Stage stage;

    public void setJob(String jobId, Object user, UserRole role, Stage stage) {
        this.currentUser = user;
        this.userRole = role;
        this.stage = stage;
        this.currentJob = JobService.getJobById(jobId);

        if (currentJob != null) {
            loadJobDetails();
        }
    }

    private void loadJobDetails() {
        titleLabel.setText(currentJob.getTitle());
        departmentLabel.setText(currentJob.getDepartment());
        typeLabel.setText(currentJob.getType() != null ? currentJob.getType().toString() : "");
        workTimeLabel.setText(currentJob.getWorkTime());
        recruitNumLabel.setText(String.valueOf(currentJob.getRecruitNum()));
        deadlineLabel.setText(currentJob.getDeadline());
        salaryLabel.setText(currentJob.getSalary() != null && !currentJob.getSalary().isEmpty() ? currentJob.getSalary() : "Not specified");
        locationLabel.setText(currentJob.getLocation() != null && !currentJob.getLocation().isEmpty() ? currentJob.getLocation() : "Not specified");

        String publisher = currentJob.getPublisherName();
        if (publisher == null || publisher.isEmpty()) {
            if ("ADMIN".equals(currentJob.getPublisherType())) {
                publisher = "Administrator";
            } else {
                publisher = "Module Organizer";
            }
        }
        publisherLabel.setText(publisher);

        String publishDate = "";
        if (currentJob.getCreatedAt() != null && currentJob.getCreatedAt().length() >= 10) {
            publishDate = currentJob.getCreatedAt().substring(0, 10);
        }
        publishDateLabel.setText(publishDate);

        statusLabel.setText(currentJob.getStatus() != null ? currentJob.getStatus().toString() : "UNKNOWN");

        descriptionArea.setText(currentJob.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);

        List<String> skills = currentJob.getSkills();
        if (skills != null && !skills.isEmpty()) {
            skillsArea.setText(String.join("\n", skills));
        } else {
            skillsArea.setText("No special skills required");
        }
        skillsArea.setEditable(false);
        skillsArea.setWrapText(true);

        String extraReq = currentJob.getExtraRequirements();
        extraRequirementsArea.setText(extraReq != null && !extraReq.isEmpty() ? extraReq : "No additional requirements");
        extraRequirementsArea.setEditable(false);
        extraRequirementsArea.setWrapText(true);

        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        switch (userRole) {
            case TA:
                applyButton.setVisible(true);
                applyButton.setDisable(false);
                applyButton.setText("Apply");
                break;
            case MO:
            case ADMIN:
                applyButton.setVisible(false);
                break;
        }
    }

    @FXML
    private void handleApply() {
        if (userRole != UserRole.TA || !(currentUser instanceof TA)) {
            showAlert("Error", "Only TAs can apply for jobs", Alert.AlertType.ERROR);
            return;
        }

        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != ProfileStatus.APPROVED) {
            showAlert("Notice", "Your profile has not been approved yet, so you cannot apply for jobs", Alert.AlertType.INFORMATION);
            return;
        }

        if (ApplicationService.isDeadlinePassed(currentJob.getDeadline())) {
            showAlert("Notice", "This job is closed because the deadline has passed.", Alert.AlertType.WARNING);
            return;
        }

        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        Application latestApplication = applications.stream()
                .filter(app -> app.getJobId().equals(currentJob.getId()))
                .max(java.util.Comparator.comparing(Application::getCreatedAt, java.util.Comparator.nullsLast(String::compareTo)))
                .orElse(null);
        if (latestApplication != null && latestApplication.getStatus() != model.ApplicationStatus.WITHDRAWN
                && latestApplication.getStatus() != model.ApplicationStatus.REJECTED) {
            showAlert("Notice", "You already have an active application for this job.", Alert.AlertType.WARNING);
            return;
        }

        AdminConfig config = AdminConfigService.loadConfig();
        int workload = WorkloadService.getCurrentWorkload(ta);
        int jobWorkload = parseWorkHours(currentJob.getWorkTime());
        if (workload + jobWorkload > config.getMaxThreshold()) {
            showAlert("Notice", "Application not allowed: current workload (" + workload + "h) + this job (" + jobWorkload + "h) exceeds the maximum (" + config.getMaxThreshold() + "h).", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Application");
        confirmAlert.setHeaderText("Apply for: " + currentJob.getTitle());
        confirmAlert.setContentText("Are you sure you want to apply for this position?");

        TextArea coverLetterArea = new TextArea();
        coverLetterArea.setPromptText("Enter your cover letter (optional)...");
        coverLetterArea.setPrefRowCount(5);
        coverLetterArea.setWrapText(true);

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.setSpacing(10);
        dialogPaneContent.getChildren().addAll(
            new Label("Cover Letter:"),
            coverLetterArea
        );

        confirmAlert.getDialogPane().setExpandableContent(dialogPaneContent);
        confirmAlert.getDialogPane().setExpanded(true);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String coverLetter = coverLetterArea.getText();
                if (coverLetter == null || coverLetter.trim().isEmpty()) {
                    coverLetter = "I am very interested in this position and would appreciate the opportunity to join.";
                }

                Application application = ApplicationService.submitApplication(ta.getId(), currentJob.getId(), coverLetter);
                if (application != null) {
                    showAlert("Application Submitted", "Your application has been submitted! Match score: " + String.format("%.2f%%", application.getMatchScore()), Alert.AlertType.INFORMATION);
                    applyButton.setText("Applied");
                    applyButton.setDisable(true);
                } else {
                    showAlert("Application Failed", "Submission failed. You may already have an active application for this job, or the deadline may have passed.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            String boardFxml = "/fxml/JobList.fxml";
            if (userRole == UserRole.TA) {
                boardFxml = "/fxml/TAJobBoard.fxml";
            } else if (userRole == UserRole.MO) {
                boardFxml = "/fxml/MOJobBoard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(boardFxml));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(currentUser, userRole);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);

            root.requestLayout();
            stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - Job Directory");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to go back: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
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
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
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
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Logout failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int parseWorkHours(String workTime) {
        if (workTime == null || workTime.isBlank()) {
            return 0;
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(workTime);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
