package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Application;
import model.ApplicationHistoryViewModel;
import model.ApplicationStatus;
import model.Job;
import model.TA;
import service.ApplicationService;
import service.JobService;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import controller.SearchBarController;

import java.util.Comparator;
import java.util.List;

public class TAApplicationHistoryController {
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label activeApplicationsLabel;
    @FXML
    private Label acceptedLabel;
    @FXML
    private Label rejectedLabel;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<String> departmentFilter;
    @FXML
    private TableView<ApplicationHistoryViewModel> applicationsTable;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> jobTitleColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> departmentColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> appliedAtColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> statusColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> reviewTimeColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> matchScoreColumn;
    @FXML
    private Label detailTitleLabel;
    @FXML
    private Label detailStatusLabel;
    @FXML
    private Label detailAppliedAtLabel;
    @FXML
    private Label detailReviewTimeLabel;
    @FXML
    private Label detailMatchScoreLabel;
    @FXML
    private TextArea detailCommentArea;
    @FXML
    private Button withdrawButton;

    private TA user;
    private Stage stage;
    private KeyboardShortcutService shortcutService;

    @FXML
    private void initialize() {
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        appliedAtColumn.setCellValueFactory(new PropertyValueFactory<>("appliedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reviewTimeColumn.setCellValueFactory(new PropertyValueFactory<>("reviewTime"));
        matchScoreColumn.setCellValueFactory(new PropertyValueFactory<>("matchScore"));

        jobTitleColumn.setCellFactory(col -> createAlignedTextCell());
        departmentColumn.setCellFactory(col -> createAlignedTextCell());
        appliedAtColumn.setCellFactory(col -> createAlignedTextCell());
        statusColumn.setCellFactory(col -> createAlignedTextCell());
        reviewTimeColumn.setCellFactory(col -> createAlignedTextCell());
        matchScoreColumn.setCellFactory(col -> createAlignedTextCell());

        applicationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        statusFilter.getItems().addAll("ALL", "PENDING", "SCREENED", "ACCEPTED", "REJECTED", "WITHDRAWN");
        statusFilter.setValue("ALL");
        departmentFilter.getItems().add("ALL");
        departmentFilter.setValue("ALL");

        applicationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showDetails(newValue));
    }

    private TableCell<ApplicationHistoryViewModel, String> createAlignedTextCell() {
        return new TableCell<ApplicationHistoryViewModel, String>() {
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

    public void setUser(TA user) {
        this.user = user;
        refreshApplications();
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
            // Create list of TA features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("Job Requirements", () -> handleJobRequirements()));
            features.add(new SearchBarController.Feature("My Applications", () -> handleApplicationManagement()));
            features.add(new SearchBarController.Feature("Update Profile", () -> handleUpdateProfile()));
            features.add(new SearchBarController.Feature("Upload Resume", () -> handleUploadResume()));
            features.add(new SearchBarController.Feature("Personal Center", () -> handlePersonalCenter()));

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

    @FXML
    private void handleJobRequirements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.TA);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("JobList", () -> {
                try {
                    FXMLLoader historyLoader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
                    Parent historyRoot = historyLoader.load();
                    TAApplicationHistoryController historyController = historyLoader.getController();
                    historyController.setUser(user);
                    historyController.setStage(stage);
                    
                    Scene scene = new Scene(historyRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application History");
                    
                    // Force layout update to ensure components resize properly
                    historyRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application history page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load job list page: " + e.getMessage());
        }
    }

    @FXML
    private void handleApplicationManagement() {
        // Already on this page, do nothing
    }

    private void handleUpdateProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAProfileEdit", () -> {
                try {
                    FXMLLoader historyLoader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
                    Parent historyRoot = historyLoader.load();
                    TAApplicationHistoryController historyController = historyLoader.getController();
                    historyController.setUser(user);
                    historyController.setStage(stage);
                    
                    Scene scene = new Scene(historyRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application History");
                    
                    // Force layout update to ensure components resize properly
                    historyRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application history page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Update Profile");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile edit page: " + e.getMessage());
        }
    }

    private void handleUploadResume() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
            Parent root = loader.load();
            TAUploadResumeController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAUploadResume", () -> {
                try {
                    FXMLLoader historyLoader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
                    Parent historyRoot = historyLoader.load();
                    TAApplicationHistoryController historyController = historyLoader.getController();
                    historyController.setUser(user);
                    historyController.setStage(stage);
                    
                    Scene scene = new Scene(historyRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application History");
                    
                    // Force layout update to ensure components resize properly
                    historyRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application history page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load upload resume page: " + e.getMessage());
        }
    }

    @FXML
    private void handlePersonalCenter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
            Parent root = loader.load();
            TAProfileViewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAProfileView", () -> {
                try {
                    FXMLLoader historyLoader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
                    Parent historyRoot = historyLoader.load();
                    TAApplicationHistoryController historyController = historyLoader.getController();
                    historyController.setUser(user);
                    historyController.setStage(stage);
                    
                    Scene scene = new Scene(historyRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application History");
                    
                    // Force layout update to ensure components resize properly
                    historyRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application history page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load personal center page: " + e.getMessage());
        }
    }

    private void refreshApplications() {
        if (user == null) {
            return;
        }

        List<Application> applications = ApplicationService.getApplicationsByTA(user.getId());
        applications.sort(Comparator.comparing(Application::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed());

        departmentFilter.getItems().setAll("ALL");

        ObservableList<ApplicationHistoryViewModel> rows = FXCollections.observableArrayList();
        int activeCount = 0;
        int acceptedCount = 0;
        int rejectedCount = 0;

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            String jobTitle = job != null ? job.getTitle() : "Unknown Job";
            String department = job != null ? job.getDepartment() : "-";
            if (job != null && job.getDepartment() != null && !departmentFilter.getItems().contains(job.getDepartment())) {
                departmentFilter.getItems().add(job.getDepartment());
            }
            boolean withdrawable = isWithdrawable(app, job);
            if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                activeCount++;
            }
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                acceptedCount++;
            }
            if (app.getStatus() == ApplicationStatus.REJECTED) {
                rejectedCount++;
            }

            rows.add(new ApplicationHistoryViewModel(
                    app.getId(),
                    jobTitle,
                    department,
                    formatDateTime(app.getCreatedAt()),
                    app.getStatus() != null ? app.getStatus().name() : "-",
                    formatDateTime(app.getReviewTime()),
                    safeText(app.getReviewComment()),
                    String.format("%.2f%%", app.getMatchScore()),
                    withdrawable
            ));
        }

        applicationsTable.setItems(filterRows(rows));
        totalApplicationsLabel.setText(String.valueOf(applications.size()));
        activeApplicationsLabel.setText(String.valueOf(activeCount));
        acceptedLabel.setText(String.valueOf(acceptedCount));
        rejectedLabel.setText(String.valueOf(rejectedCount));

        if (!rows.isEmpty()) {
            applicationsTable.getSelectionModel().selectFirst();
        } else {
            showDetails(null);
        }
    }

    @FXML
    private void handleFilterChange() {
        refreshApplications();
    }

    private ObservableList<ApplicationHistoryViewModel> filterRows(ObservableList<ApplicationHistoryViewModel> rows) {
        String status = statusFilter.getValue();
        String department = departmentFilter.getValue();
        ObservableList<ApplicationHistoryViewModel> filtered = FXCollections.observableArrayList();
        for (ApplicationHistoryViewModel row : rows) {
            boolean matchesStatus = status == null || "ALL".equals(status) || status.equals(row.getStatus());
            boolean matchesDepartment = department == null || "ALL".equals(department) || department.equals(row.getDepartment());
            if (matchesStatus && matchesDepartment) {
                filtered.add(row);
            }
        }
        return filtered;
    }

    private void showDetails(ApplicationHistoryViewModel selected) {
        if (selected == null) {
            detailTitleLabel.setText("No application selected");
            detailStatusLabel.setText("-");
            detailAppliedAtLabel.setText("-");
            detailReviewTimeLabel.setText("-");
            detailMatchScoreLabel.setText("-");
            detailCommentArea.setText("Select one application to view details.");
            withdrawButton.setDisable(true);
            return;
        }

        detailTitleLabel.setText(selected.getJobTitle());
        detailStatusLabel.setText(selected.getStatus());
        detailAppliedAtLabel.setText(selected.getAppliedAt());
        detailReviewTimeLabel.setText(selected.getReviewTime());
        detailMatchScoreLabel.setText(selected.getMatchScore());
        detailCommentArea.setText(selected.getReviewComment());
        withdrawButton.setDisable(!selected.isWithdrawable());
    }

    private boolean isWithdrawable(Application app, Job job) {
        if (app == null || job == null) {
            return false;
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return !ApplicationService.isDeadlinePassed(job.getDeadline());
    }

    private String formatDateTime(String value) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return "-";
        }

        String normalized = value.replace('T', ' ').trim();
        int dotIndex = normalized.indexOf('.');
        if (dotIndex > 0) {
            normalized = normalized.substring(0, dotIndex);
        }
        return normalized;
    }

    private String safeText(String value) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return "No feedback yet";
        }
        return value;
    }

    @FXML
    private void handleWithdrawApplication() {
        ApplicationHistoryViewModel selected = applicationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an application record first.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Withdrawal");
        confirmAlert.setHeaderText("Withdraw: " + selected.getJobTitle());
        confirmAlert.setContentText("This application will be marked as WITHDRAWN and can only be withdrawn before review. Continue?");
        confirmAlert.initModality(Modality.APPLICATION_MODAL);

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean success = ApplicationService.withdrawApplication(selected.getApplicationId(), user.getId());
        if (!success) {
            showAlert(Alert.AlertType.WARNING, "Cannot Withdraw", "Only applications that are still PENDING and before the deadline can be withdrawn.");
            refreshApplications();
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Withdrawn", "This application has been withdrawn. The status is now WITHDRAWN.");
        refreshApplications();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Error", "Logout failed: " + e.getMessage());
        }
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
