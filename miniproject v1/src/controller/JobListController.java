package controller;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Admin;
import model.Job;
import model.MO;
import model.TA;
import model.UserRole;
import service.JobService;

import java.util.List;

public class JobListController {
    @FXML private Label pageTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TableView<JobListViewModel> jobsTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilter;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button backButton;
    @FXML private Button createJobButton;

    private Object currentUser;
    private UserRole userRole;
    private Stage stage;
    private final ObservableList<JobListViewModel> allJobs = FXCollections.observableArrayList();

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
        departmentFilter.getItems().addAll("全部", "计算机学院", "理学院", "外语学院", "人文学院", "经济管理学院", "工学院");
        departmentFilter.setValue("全部");
        typeFilter.getItems().addAll("全部", "MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        typeFilter.setValue("全部");
        statusFilter.getItems().addAll("全部", "PUBLISHED", "PENDING", "REJECTED", "CLOSED", "DRAFT");
        statusFilter.setValue("全部");
        setupTableColumns();
        jobsTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                JobListViewModel selectedJob = jobsTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    showJobDetail(selectedJob.getJobId());
                }
            }
        });
    }

    private void setupTableColumns() {
        TableColumn<JobListViewModel, String> titleCol = new TableColumn<>("职位名称");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getTitle())));
        titleCol.setPrefWidth(180);
        TableColumn<JobListViewModel, String> departmentCol = new TableColumn<>("院系");
        departmentCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getDepartment())));
        departmentCol.setPrefWidth(110);
        TableColumn<JobListViewModel, String> typeCol = new TableColumn<>("职位类型");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getType())));
        typeCol.setPrefWidth(120);
        TableColumn<JobListViewModel, String> requirementsCol = new TableColumn<>("职位需求");
        requirementsCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getRequirements())));
        requirementsCol.setPrefWidth(260);
        TableColumn<JobListViewModel, String> publishDateCol = new TableColumn<>("发布日期");
        publishDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getPublishDate())));
        publishDateCol.setPrefWidth(110);
        TableColumn<JobListViewModel, String> deadlineCol = new TableColumn<>("截止日期");
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getDeadline())));
        deadlineCol.setPrefWidth(110);
        TableColumn<JobListViewModel, String> publisherCol = new TableColumn<>("发布者");
        publisherCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getPublisher())));
        publisherCol.setPrefWidth(100);
        TableColumn<JobListViewModel, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(safe(cellData.getValue().getStatus())));
        statusCol.setPrefWidth(90);
        jobsTable.getColumns().setAll(titleCol, departmentCol, typeCol, requirementsCol, publishDateCol, deadlineCol, publisherCol, statusCol);
        jobsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        jobsTable.setFixedCellSize(46);
        jobsTable.setPlaceholder(new Label("暂无职位数据"));
    }

    private void initializeView() {
        if (currentUser == null) return;
        switch (userRole) {
            case TA:
                pageTitleLabel.setText("职位需求");
                subtitleLabel.setText("查看所有可申请职位，并按关键词、院系、类型快速筛选");
                createJobButton.setVisible(false);
                backButton.setText("返回TA控制台");
                statusFilter.setValue("PUBLISHED");
                statusFilter.setDisable(true);
                break;
            case MO:
                pageTitleLabel.setText("职位需求");
                subtitleLabel.setText("查看你发布的职位，并筛选待审核、驳回或已发布状态");
                createJobButton.setVisible(true);
                backButton.setText("返回MO控制台");
                statusFilter.setDisable(false);
                statusFilter.setValue("全部");
                break;
            case ADMIN:
                pageTitleLabel.setText("职位需求");
                subtitleLabel.setText("查看和管理所有职位，支持全量搜索与状态筛选");
                createJobButton.setVisible(true);
                backButton.setText("返回管理员控制台");
                statusFilter.setDisable(false);
                statusFilter.setValue("全部");
                break;
        }
        loadJobs();
    }

    private void loadJobs() {
        List<Job> jobs = userRole == UserRole.TA ? JobService.getAvailableJobs() : JobService.getAllJobs();
        allJobs.clear();
        for (Job job : jobs) {
            if (userRole == UserRole.MO && currentUser instanceof MO) {
                String moId = ((MO) currentUser).getId();
                if (job.getMoId() == null || !job.getMoId().equals(moId)) {
                    continue;
                }
            }
            String publisher = job.getPublisherName();
            if (publisher == null || publisher.isEmpty()) {
                publisher = "ADMIN".equals(job.getPublisherType()) ? "管理员" : "模块组织者";
            }
            String requirements = job.getDescription() == null ? "" : job.getDescription();
            if (requirements.length() > 50) {
                requirements = requirements.substring(0, 50) + "...";
            }
            allJobs.add(new JobListViewModel(
                job.getId(),
                job.getTitle(),
                job.getDepartment(),
                job.getType() == null ? "" : job.getType().toString(),
                requirements,
                shortDate(job.getCreatedAt()),
                shortDate(job.getDeadline()),
                publisher,
                job.getStatus() == null ? "UNKNOWN" : job.getStatus().toString(),
                job
            ));
        }
        applyFilters();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        departmentFilter.setValue("全部");
        typeFilter.setValue("全部");
        statusFilter.setValue(userRole == UserRole.TA ? "PUBLISHED" : "全部");
        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String deptFilter = departmentFilter.getValue();
        String typeFilterValue = typeFilter.getValue();
        String statusFilterValue = statusFilter.getValue();
        ObservableList<JobListViewModel> filteredList = FXCollections.observableArrayList();
        for (JobListViewModel job : allJobs) {
            boolean matchesKeyword = keyword.isEmpty()
                    || job.getTitle().toLowerCase().contains(keyword)
                    || job.getRequirements().toLowerCase().contains(keyword)
                    || job.getPublisher().toLowerCase().contains(keyword);
            boolean matchesDept = deptFilter == null || "全部".equals(deptFilter) || deptFilter.equals(job.getDepartment());
            boolean matchesType = typeFilterValue == null || "全部".equals(typeFilterValue) || typeFilterValue.equals(job.getType());
            boolean matchesStatus = statusFilterValue == null || "全部".equals(statusFilterValue) || statusFilterValue.equals(job.getStatus());
            if (matchesKeyword && matchesDept && matchesType && matchesStatus) {
                filteredList.add(job);
            }
        }
        jobsTable.setItems(filteredList);
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

    private String shortDate(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.length() >= 10 ? value.substring(0, 10) : value;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
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
