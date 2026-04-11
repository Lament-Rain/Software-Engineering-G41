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

    public void setUser(Object user, UserRole role) {
        this.currentUser = user;
        this.userRole = role;
        initializeView();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Initialize filters
        departmentFilter.getItems().addAll("All", "School of Computer Science", "School of Science", "School of Foreign Languages", "School of Humanities", "School of Economics and Management", "School of Engineering");
        departmentFilter.setValue("All");

        typeFilter.getItems().addAll("All", "MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        typeFilter.setValue("All");

        // Set up table columns
        setupTableColumns();

        searchField.setOnAction(event -> handleSearch());
        departmentFilter.setOnAction(event -> handleSearch());
        typeFilter.setOnAction(event -> handleSearch());
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                handleSearch();
            }
        });

        // Add table double-click event
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

        // Set page title and visible content based on user role
        switch (userRole) {
            case TA:
                pageTitleLabel.setText("Open Positions");
                subtitleLabel.setText("Browse all positions currently available for application");
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
        List<Job> jobs = userRole == UserRole.TA ? JobService.getAvailableJobs() : JobService.getAllJobs();
        ObservableList<JobListViewModel> jobList = FXCollections.observableArrayList();

        for (Job job : jobs) {
            // Get publisher name
            String publisher = job.getPublisherName();
            if (publisher == null || publisher.isEmpty()) {
                if ("ADMIN".equals(job.getPublisherType())) {
                    publisher = "Administrator";
                } else {
                    publisher = "Module Organizer";
                }
            }

            // Format publish date
            String publishDate = formatDisplayDate(job.getCreatedAt());

            // Format deadline
            String deadline = formatDisplayDate(job.getDeadline());

            // Get job requirement summary
            String requirements = job.getDescription();
            if (requirements != null && requirements.length() > 50) {
                requirements = requirements.substring(0, 50) + "...";
            }

            // Get status
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
    }

    @FXML
    private void handleSearch() {
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
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        departmentFilter.setValue("All");
        typeFilter.setValue("All");
        loadJobs();
    }

    @FXML
    private void handleCreateJob() {
        if (userRole == UserRole.TA) {
            showAlert("Permission Denied", "TAs are not allowed to create jobs", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();

            if (currentUser instanceof MO) {
                controller.setUser((MO) currentUser);
            } else if (currentUser instanceof Admin) {
                // Create temporary MO object for Admin or publish as Admin
                controller.setAdminUser((Admin) currentUser);
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Create Job");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load page: " + e.getMessage(), Alert.AlertType.ERROR);
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
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to go back: " + e.getMessage(), Alert.AlertType.ERROR);
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
            stage.setTitle("BUPT International School TA Recruitment System - Job Details");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load job details: " + e.getMessage(), Alert.AlertType.ERROR);
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
        resultCountLabel.setText("Current results: " + count + " positions");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Logout failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Internal class for table display
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
