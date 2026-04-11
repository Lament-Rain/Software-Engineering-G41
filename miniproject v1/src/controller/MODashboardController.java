package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.JobStatus;
import model.JobViewModel;
import model.MO;
import service.ApplicationService;
import service.JobService;

import java.util.List;

public class MODashboardController {
    @FXML
    private Label publishedJobsLabel;
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label acceptedApplicationsLabel;
    @FXML
    private TableView<JobViewModel> jobsTable;

    private MO user;

    public void setUser(MO user) {
        this.user = user;
        initializeDashboard();
    }

    private void initializeDashboard() {
        if (user == null) {
            return;
        }

        try {
            loadStatistics();
            loadRecentJobs();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }

    private void loadStatistics() {
        List<Job> jobsForStats = JobService.getAllJobsByMO(user.getId());

        int publishedJobsCount = jobsForStats.size();
        int totalApplicationsCount = 0;
        int acceptedApplicationsCount = 0;

        for (Job job : jobsForStats) {
            List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
            totalApplicationsCount += applications.size();
            for (Application app : applications) {
                if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                    acceptedApplicationsCount++;
                }
            }
        }

        publishedJobsLabel.setText(String.valueOf(publishedJobsCount));
        totalApplicationsLabel.setText(String.valueOf(totalApplicationsCount));
        acceptedApplicationsLabel.setText(String.valueOf(acceptedApplicationsCount));
    }

    private void loadRecentJobs() {
        ObservableList<JobViewModel> jobs = FXCollections.observableArrayList();
        List<Job> jobList = JobService.getAllJobsByMO(user.getId());

        for (Job job : jobList) {
            int applicationCount = ApplicationService.getApplicationsByJob(job.getId()).size();
            jobs.add(new JobViewModel(
                    job.getId(),
                    safeText(job.getTitle()),
                    safeText(job.getDepartment()),
                    formatDeadline(job.getDeadline()),
                    formatStatus(job.getStatus()),
                    applicationCount
            ));
        }

        jobsTable.setItems(jobs);
        jobsTable.setRowFactory(table -> {
            TableRow<JobViewModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openMyJobsPage(row.getItem().getJobId());
                }
            });
            return row;
        });
        jobsTable.refresh();
    }

    @FXML
    private void handleCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setUser(user);

            Stage stage = getStage(event);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Create TA Position");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load create job page. Please try again later.");
        }
    }

    @FXML
    private void handleViewApplications(ActionEvent event) {
        openApplicationReview(event);
    }

    @FXML
    private void handleExportData(ActionEvent event) {
        if (user == null) {
            return;
        }

        List<Job> jobs = JobService.getAllJobsByMO(user.getId());
        if (jobs.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "No published jobs yet.");
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("Job Title,Job Status,Applicant,Department,Applied At,Status,Match Score,Review Comment\n");

        int exportedRows = 0;
        for (Job job : jobs) {
            List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
            if (applications.isEmpty()) {
                content.append(job.getTitle()).append(",")
                        .append(formatStatus(job.getStatus())).append(",No Applications,-,-,-,-,-\n");
                continue;
            }

            for (Application app : applications) {
                String applicantName = "Unknown Applicant";
                String applicantDepartment = "-";
                model.TA ta = service.UserService.getTAProfile(app.getTaId());
                if (ta != null) {
                    applicantName = ta.getName() != null && !ta.getName().isBlank() ? ta.getName() : ta.getUsername();
                    applicantDepartment = safeText(ta.getDepartment());
                }

                content.append(safeCsv(job.getTitle())).append(",")
                        .append(safeCsv(formatStatus(job.getStatus()))).append(",")
                        .append(safeCsv(applicantName)).append(",")
                        .append(safeCsv(applicantDepartment)).append(",")
                        .append(safeCsv(safeText(app.getCreatedAt()))).append(",")
                        .append(safeCsv(app.getStatus() == null ? "-" : app.getStatus().name())).append(",")
                        .append(app.getMatchScore()).append(",")
                        .append(safeCsv(safeText(app.getReviewComment())))
                        .append("\n");
                exportedRows++;
            }
        }

        String filePath = "src/data/mo_export_" + user.getUsername() + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write(content.toString());
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Exported " + exportedRows + " application records to:\n" + filePath);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Export failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(getStage(event));

            Scene scene = new Scene(root, 800, 600);
            Stage stage = getStage(event);
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page. Please try again later.");
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        initializeDashboard();
    }

    @FXML
    private void handleJobManagement(ActionEvent event) {
        handleViewAllJobs(event);
    }

    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        openApplicationReview(event);
    }

    @FXML
    private void handleViewMyJobs(ActionEvent event) {
        openMyJobsPage(null);
    }

    @FXML
    private void handleViewAllJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);

            Stage stage = getStage(event);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load job list page. Please try again later.");
        }
    }

    private void openApplicationReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
            Parent root = loader.load();
            MOApplicationReviewController controller = loader.getController();
            controller.setUser(user);

            Stage stage = getStage(event);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application Review");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application review page. Please try again later.");
        }
    }

    private void openMyJobsPage(String jobId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
            Parent root = loader.load();
            MOMyJobsController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) jobsTable.getScene().getWindow();
            controller.setStage(stage);
            controller.openJob(jobId);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load My Jobs page. Please try again later.");
        }
    }

    private Stage getStage(ActionEvent event) {
        if (event != null && event.getSource() instanceof Button) {
            return (Stage) ((Button) event.getSource()).getScene().getWindow();
        }
        return (Stage) jobsTable.getScene().getWindow();
    }

    private String formatDeadline(String deadline) {
        return safeText(deadline);
    }

    private String formatStatus(JobStatus status) {
        if (status == null) {
            return "Unknown";
        }
        switch (status) {
            case DRAFT:
                return "Draft";
            case PENDING:
                return "Pending Review";
            case PUBLISHED:
                return "Published";
            case REJECTED:
                return "Rejected";
            case CLOSED:
                return "Closed";
            default:
                return status.name();
        }
    }

    private String safeText(String value) {
        return value == null || value.isBlank() || "null".equalsIgnoreCase(value) ? "-" : value;
    }

    private String safeCsv(String value) {
        String cleaned = safeText(value).replace("\"", "\"\"");
        return "\"" + cleaned + "\"";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
