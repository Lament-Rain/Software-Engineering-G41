package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Job;
import model.JobViewModel;
import model.MO;
import model.Admin;
import model.TA;
import model.UserRole;
import service.JobService;
import service.ApplicationService;
import service.KeyboardShortcutService;
import service.ToastService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Locale;

public class JobListController {

    @FXML
    private Label pageTitleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private TableView<JobListViewModel> jobsTable;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> departmentFilter;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private Label resultCountLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button createJobButton;

    private Object currentUser;
    private UserRole userRole;
    private Stage stage;
    private ObservableList<JobListViewModel> allJobs = FXCollections.observableArrayList();
    private KeyboardShortcutService shortcutService;

    public void setUser(Object user, UserRole role) {
        this.currentUser = user;
        this.userRole = role;
        initializeView();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }

    @FXML
    private void initialize() {
        departmentFilter.getItems().addAll("All", "School of Computer Science", "School of Science", "School of Foreign Languages", "School of Humanities", "School of Economics and Management", "School of Engineering");
        departmentFilter.setValue("All");

        typeFilter.getItems().addAll("All", "MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        typeFilter.setValue("All");

        setupTableColumns();

        searchField.setOnAction(event -> handleSearchAction());
        departmentFilter.setOnAction(event -> handleSearchAction());
        typeFilter.setOnAction(event -> handleSearchAction());
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                handleSearchAction();
            }
        });

        jobsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    JobListViewModel selectedJob = jobsTable.getSelectionModel().getSelectedItem();
                    if (selectedJob != null) {
                        showJobDetail(selectedJob.getJobId());
                    }
                }
            }
        });
    }

    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::showSearchBar);
        shortcutService.registerShortcut("escape", this::handleResetAction);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }

    private void showSearchBar() {
        try {
            // Create list of features based on user role
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            
            if (userRole == UserRole.TA) {
                features.add(new SearchBarController.Feature("Job Requirements", () -> {})); // 已在JobList页面，无需跳转
                features.add(new SearchBarController.Feature("My Applications", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
                        Parent root = loader.load();
                        TAApplicationHistoryController controller = loader.getController();
                        controller.setUser((TA) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Application History");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Update Profile", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
                        Parent root = loader.load();
                        TAProfileEditController controller = loader.getController();
                        controller.setUser((TA) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Update Profile");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Upload Resume", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
                        Parent root = loader.load();
                        TAUploadResumeController controller = loader.getController();
                        controller.setUser((TA) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Personal Center", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
                        Parent root = loader.load();
                        TAProfileViewController controller = loader.getController();
                        controller.setUser((TA) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
            } else if (userRole == UserRole.MO) {
                features.add(new SearchBarController.Feature("Create Job", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
                        Parent root = loader.load();
                        MOCreateJobController controller = loader.getController();
                        controller.setUser((MO) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Create TA Position");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Application Review", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
                        Parent root = loader.load();
                        MOApplicationReviewController controller = loader.getController();
                        controller.setUser((MO) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Application Review");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("My Jobs", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
                        Parent root = loader.load();
                        MOMyJobsController controller = loader.getController();
                        controller.setUser((MO) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Job List", () -> handleBack()));
            } else if (userRole == UserRole.ADMIN) {
                features.add(new SearchBarController.Feature("User Management", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        AdminDashboardController controller = loader.getController();
                        controller.setUser((Admin) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Job Management", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        AdminDashboardController controller = loader.getController();
                        controller.setUser((Admin) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Application Management", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        AdminDashboardController controller = loader.getController();
                        controller.setUser((Admin) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("System Settings", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        AdminDashboardController controller = loader.getController();
                        controller.setUser((Admin) currentUser);
                        controller.setStage(stage);
                        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
                        root.requestLayout();
                        stage.sizeToScene();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                features.add(new SearchBarController.Feature("Job List", () -> handleBack()));
            }

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            searchStage.initOwner(stage);
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
            ToastService.showToast(stage, "Failed to load search bar: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private void setupTableColumns() {
        TableColumn<JobListViewModel, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<JobListViewModel, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> typeCol = new TableColumn<>("Job Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> requirementsCol = new TableColumn<>("Requirements");
        requirementsCol.setCellValueFactory(new PropertyValueFactory<>("requirements"));
        requirementsCol.setPrefWidth(250);

        TableColumn<JobListViewModel, String> publishDateCol = new TableColumn<>("Published Date");
        publishDateCol.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        publishDateCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> publisherCol = new TableColumn<>("Publisher");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setPrefWidth(100);

        TableColumn<JobListViewModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        jobsTable.getColumns().clear();
        jobsTable.getColumns().addAll(titleCol, departmentCol, typeCol, requirementsCol, publishDateCol, deadlineCol, publisherCol, statusCol);
    }

    private void initializeView() {
        if (currentUser == null) return;

        switch (userRole) {
            case TA:
                pageTitleLabel.setText("Job Board");
                subtitleLabel.setText("View every posted position");
                createJobButton.setVisible(false);
                backButton.setText("Back to TA Dashboard");
                break;
            case MO:
                pageTitleLabel.setText("Job Board");
                subtitleLabel.setText("View every posted position");
                createJobButton.setVisible(false);
                createJobButton.setManaged(false);
                backButton.setText("Back to MO Dashboard");
                break;
            case ADMIN:
                pageTitleLabel.setText("Job Board");
                subtitleLabel.setText("Review and manage all positions");
                createJobButton.setVisible(true);
                createJobButton.setText("Create Job");
                backButton.setText("Back to Admin Dashboard");
                break;
        }

        loadJobs();
    }

    private void loadJobs() {
        List<Job> jobs = JobService.getAllJobs();
        ObservableList<JobListViewModel> jobList = FXCollections.observableArrayList();

        for (Job job : jobs) {
            String publisher = job.getPublisherName();
            if (publisher == null || publisher.isEmpty()) {
                if ("ADMIN".equals(job.getPublisherType())) {
                    publisher = "Administrator";
                } else {
                    publisher = "Module Organizer";
                }
            }

            String publishDate = formatDisplayDate(job.getCreatedAt());
            String deadline = formatDisplayDate(job.getDeadline());

            String requirements = job.getDescription();
            if (requirements != null && requirements.length() > 50) {
                requirements = requirements.substring(0, 50) + "...";
            }

            String status = job.getStatus() != null ? job.getStatus().toString() : "UNKNOWN";

            jobList.add(new JobListViewModel(
                job.getId(),
                job.getTitle(),
                job.getDepartment(),
                job.getType() != null ? job.getType().toString() : "",
                requirements,
                publishDate,
                deadline,
                publisher,
                status,
                job
            ));
        }

        allJobs.setAll(jobList);
        jobsTable.setItems(FXCollections.observableArrayList(allJobs));
        updateResultCount(jobList.size());

        ToastService.showToast(stage, "Loaded " + jobList.size() + " job(s)", ToastService.ToastType.INFO);
    }

    @FXML
    private void handleSearch() {
        handleSearchAction();
    }

    private void handleSearchAction() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String deptFilter = departmentFilter.getValue();
        String typeFilterValue = typeFilter.getValue();

        ObservableList<JobListViewModel> filteredList = FXCollections.observableArrayList();

        for (JobListViewModel job : allJobs) {
            String title = job.getTitle() == null ? "" : job.getTitle().toLowerCase();
            String requirements = job.getRequirements() == null ? "" : job.getRequirements().toLowerCase();

            boolean matchesKeyword = keyword.isEmpty() ||
                    title.contains(keyword) ||
                    requirements.contains(keyword);

            boolean matchesDept = deptFilter == null || "All".equals(deptFilter) || deptFilter.equals(job.getDepartment());

            boolean matchesType = typeFilterValue == null || "All".equals(typeFilterValue) || typeFilterValue.equals(job.getType());

            if (matchesKeyword && matchesDept && matchesType) {
                filteredList.add(job);
            }
        }

        jobsTable.setItems(filteredList);
        updateResultCount(filteredList.size());

        if (filteredList.isEmpty()) {
            ToastService.showToast(stage, "No jobs match your search criteria", ToastService.ToastType.INFO);
        } else {
            ToastService.showToast(stage, "Found " + filteredList.size() + " job(s)", ToastService.ToastType.SUCCESS);
        }
    }

    @FXML
    private void handleReset() {
        handleResetAction();
    }

    private void handleResetAction() {
        // Go back to dashboard (home)
        try {
            FXMLLoader loader = null;
            Parent root = null;
            
            if (userRole == UserRole.TA) {
                loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
                root = loader.load();
                TADashboardController controller = loader.getController();
                controller.setUser((TA) currentUser);
                controller.setStage(stage);
            } else if (userRole == UserRole.MO) {
                loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser((MO) currentUser);
                controller.setStage(stage);
            } else if (userRole == UserRole.ADMIN) {
                loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser((Admin) currentUser);
                controller.setStage(stage);
            }

            if (root != null) {
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - " + userRole.name() + " Dashboard");
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to navigate home: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleCreateJob() {
        if (userRole == UserRole.TA) {
            ToastService.showToast(stage, "TAs are not allowed to create jobs", ToastService.ToastType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();

            if (currentUser instanceof MO) {
                controller.setUser((MO) currentUser);
            } else if (currentUser instanceof Admin) {
                controller.setAdminUser((Admin) currentUser);
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - Create Job");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlPath;
            String title;

            switch (userRole) {
                case TA:
                    fxmlPath = "/fxml/TADashboard.fxml";
                    title = "BUPT International School TA Recruitment System - TA Dashboard";
                    FXMLLoader taLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent taRoot = taLoader.load();
                    TADashboardController taController = taLoader.getController();
                    taController.setUser((TA) currentUser);
                    Scene taScene = new Scene(taRoot, stage.getWidth(), stage.getHeight());
                    taScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(taScene);
                    stage.setTitle(title);
                    
                    // Force layout update to ensure components resize properly
                    taRoot.requestLayout();
                    stage.sizeToScene();
                    break;
                case MO:
                    fxmlPath = "/fxml/MODashboard.fxml";
                    title = "BUPT International School TA Recruitment System - MO Dashboard";
                    FXMLLoader moLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent moRoot = moLoader.load();
                    MODashboardController moController = moLoader.getController();
                    moController.setUser((MO) currentUser);
                    Scene moScene = new Scene(moRoot, stage.getWidth(), stage.getHeight());
                    moScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(moScene);
                    stage.setTitle(title);
                    
                    // Force layout update to ensure components resize properly
                    moRoot.requestLayout();
                    stage.sizeToScene();
                    break;
                case ADMIN:
                    fxmlPath = "/fxml/AdminDashboard.fxml";
                    title = "BUPT International School TA Recruitment System - Admin Dashboard";
                    FXMLLoader adminLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent adminRoot = adminLoader.load();
                    AdminDashboardController adminController = adminLoader.getController();
                    adminController.setUser((Admin) currentUser);
                    Scene adminScene = new Scene(adminRoot, stage.getWidth(), stage.getHeight());
                    adminScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(adminScene);
                    stage.setTitle(title);
                    
                    // Force layout update to ensure components resize properly
                    adminRoot.requestLayout();
                    stage.sizeToScene();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to go back: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private void showJobDetail(String jobId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobDetail.fxml"));
            Parent root = loader.load();
            JobDetailController controller = loader.getController();

            controller.setJob(jobId, currentUser, userRole, stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - Job Details");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load job details: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    private String formatDisplayDate(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
            return "";
        }

        try {
            java.time.LocalDate date = java.time.LocalDate.parse(trimmed);
            return String.format(Locale.ROOT, "%02d-%02d-%04d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(trimmed);
            java.time.LocalDate date = dateTime.toLocalDate();
            return String.format(Locale.ROOT, "%02d-%02d-%04d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        String[] parts = trimmed.split("-");
        if (parts.length >= 3) {
            try {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                String dayPart = parts[2];
                int tIndex = dayPart.indexOf('T');
                if (tIndex >= 0) {
                    dayPart = dayPart.substring(0, tIndex);
                }
                int day = Integer.parseInt(dayPart);
                return String.format(Locale.ROOT, "%02d-%02d-%04d", month, day, year);
            } catch (NumberFormatException ignored) {
            }
        }

        return trimmed;
    }

    private void updateResultCount(int count) {
        if (resultCountLabel == null) {
            return;
        }
        resultCountLabel.setText("Results: " + count + " positions");
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
            ToastService.showToast(stage, "Logged out successfully", ToastService.ToastType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Logout failed: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    public static class JobListViewModel {
        private String jobId;
        private String title;
        private String department;
        private String type;
        private String requirements;
        private String publishDate;
        private String deadline;
        private String publisher;
        private String status;
        private Job job;

        public JobListViewModel(String jobId, String title, String department, String type,
                                String requirements, String publishDate, String deadline,
                                String publisher, String status, Job job) {
            this.jobId = jobId;
            this.title = title;
            this.department = department;
            this.type = type;
            this.requirements = requirements;
            this.publishDate = publishDate;
            this.deadline = deadline;
            this.publisher = publisher;
            this.status = status;
            this.job = job;
        }

        public String getJobId() { return jobId; }
        public String getTitle() { return title; }
        public String getDepartment() { return department; }
        public String getType() { return type; }
        public String getRequirements() { return requirements; }
        public String getPublishDate() { return publishDate; }
        public String getDeadline() { return deadline; }
        public String getPublisher() { return publisher; }
        public String getStatus() { return status; }
        public Job getJob() { return job; }
    }
}
