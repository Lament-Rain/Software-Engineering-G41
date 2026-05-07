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
import model.UserRole;
import service.ApplicationService;
import service.JobService;
import service.KeyboardShortcutService;
import service.ToastService;
import service.NavigationHistory;
import controller.SearchBarController;
import controller.SearchBarController.Feature;

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
    private Stage stage;
    private KeyboardShortcutService shortcutService;
    
    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleHomeAction);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }

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

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOCreateJob", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MODashboardController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard, please try again later.");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
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
        // Clear navigation history on logout
        NavigationHistory.getInstance().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            Stage stage = getStage(event);
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
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page. Please try again later.");
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        // If we can go back, do so; otherwise, stay on the dashboard
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            initializeDashboard();
        }
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

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("JobList", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MODashboardController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard, please try again later.");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
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

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOApplicationReview", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MODashboardController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard, please try again later.");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
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

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOMyJobs", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MODashboardController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard, please try again later.");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
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
    
    private void handleHomeAction() {
        // If we can go back, do so; otherwise, stay on the dashboard
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            // Refresh current dashboard
            initializeDashboard();
        }
    }
    
    private void handleSearchAction() {
        showSearchBar();
    }

    private void showSearchBar() {
        try {
            // Create list of MO features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("Create Job", () -> handleCreateJob(new ActionEvent())));
            features.add(new SearchBarController.Feature("Application Review", () -> openApplicationReview(new ActionEvent())));
            features.add(new SearchBarController.Feature("My Jobs", () -> handleViewMyJobs(new ActionEvent())));
            features.add(new SearchBarController.Feature("Job List", () -> handleViewAllJobs(new ActionEvent())));

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            searchStage.initOwner((Stage) jobsTable.getScene().getWindow());
            searchStage.setTitle("Search Features");
            // Set fixed size and disable maximize button
            searchStage.setResizable(false);
            searchStage.setWidth(600);
            searchStage.setHeight(450);

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
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load search bar. Please try again later.");
        }
    }
}
