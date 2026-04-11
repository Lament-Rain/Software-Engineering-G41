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
        // 初始化筛选器
        departmentFilter.getItems().addAll("全部", "计算机学院", "理学院", "外语学院", "人文学院", "经济管理学院", "工学院");
        departmentFilter.setValue("全部");

        typeFilter.getItems().addAll("全部", "MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        typeFilter.setValue("全部");

        // 设置表格列
        setupTableColumns();

        searchField.setOnAction(event -> handleSearch());
        departmentFilter.setOnAction(event -> handleSearch());
        typeFilter.setOnAction(event -> handleSearch());
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                handleSearch();
            }
        });

        // 添加表格双击事件
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
        TableColumn<JobListViewModel, String> titleCol = new TableColumn<>("职位名称");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<JobListViewModel, String> departmentCol = new TableColumn<>("院系");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> typeCol = new TableColumn<>("职位类型");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> requirementsCol = new TableColumn<>("职位需求");
        requirementsCol.setCellValueFactory(new PropertyValueFactory<>("requirements"));
        requirementsCol.setPrefWidth(250);

        TableColumn<JobListViewModel, String> publishDateCol = new TableColumn<>("发布日期");
        publishDateCol.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        publishDateCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> deadlineCol = new TableColumn<>("截止日期");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineCol.setPrefWidth(120);

        TableColumn<JobListViewModel, String> publisherCol = new TableColumn<>("发布者");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setPrefWidth(100);

        TableColumn<JobListViewModel, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        jobsTable.getColumns().clear();
        jobsTable.getColumns().addAll(titleCol, departmentCol, typeCol, requirementsCol, publishDateCol, deadlineCol, publisherCol, statusCol);
    }

    private void initializeView() {
        if (currentUser == null) return;

        // 根据用户角色设置页面标题和显示内容
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
                createJobButton.setVisible(true);
                createJobButton.setText("Create Job");
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
            // 获取发布者名称
            String publisher = job.getPublisherName();
            if (publisher == null || publisher.isEmpty()) {
                if ("ADMIN".equals(job.getPublisherType())) {
                    publisher = "管理员";
                } else {
                    publisher = "模块组织者";
                }
            }

            // 格式化发布日期
            String publishDate = formatDisplayDate(job.getCreatedAt());

            // 格式化截止日期
            String deadline = formatDisplayDate(job.getDeadline());

            // 获取职位需求摘要
            String requirements = job.getDescription();
            if (requirements != null && requirements.length() > 50) {
                requirements = requirements.substring(0, 50) + "...";
            }

            // 获取状态
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

            boolean matchesDept = deptFilter == null || "全部".equals(deptFilter) || deptFilter.equals(job.getDepartment());

            boolean matchesType = typeFilterValue == null || "全部".equals(typeFilterValue) || typeFilterValue.equals(job.getType());

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
        departmentFilter.setValue("全部");
        typeFilter.setValue("全部");
        loadJobs();
    }

    @FXML
    private void handleCreateJob() {
        if (userRole == UserRole.TA) {
            showAlert("权限不足", "TA没有权限发布职位", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();

            if (currentUser instanceof MO) {
                controller.setUser((MO) currentUser);
            } else if (currentUser instanceof Admin) {
                // 为Admin创建临时MO对象或使用Admin发布
                controller.setAdminUser((Admin) currentUser);
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 发布职位");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "页面加载失败: " + e.getMessage(), Alert.AlertType.ERROR);
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
                    title = "BUPT国际学校TA招聘系统 - TA控制台";
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
                    title = "BUPT国际学校TA招聘系统 - 模块组织者控制台";
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
                    title = "BUPT国际学校TA招聘系统 - 管理员控制台";
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
            showAlert("错误", "返回失败: " + e.getMessage(), Alert.AlertType.ERROR);
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
            stage.setTitle("BUPT国际学校TA招聘系统 - 职位详情");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "加载职位详情失败: " + e.getMessage(), Alert.AlertType.ERROR);
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
        resultCountLabel.setText("当前结果：" + count + " 个职位");
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
            stage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "退出失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // 内部类用于表格显示
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
