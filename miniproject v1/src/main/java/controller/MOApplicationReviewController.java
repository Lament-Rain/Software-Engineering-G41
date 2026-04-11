package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.User;
import model.TA;
import service.DataStorage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MOApplicationReviewController {

    @FXML
    private ComboBox<String> jobSelector;
    @FXML
    private Label applicantCountLabel;
    @FXML
    private Label acceptedCountLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private TableView<ObservableList<String>> applicantsTable;
    @FXML
    private TableColumn<ObservableList<String>, String> nameColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> departmentColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> submittedAtColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> statusColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> matchScoreColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> resumeStatusColumn;
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
    
    private model.MO currentUser;

    @FXML
    public void initialize() {
        initTableColumns();

        jobSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (currentUser != null) {
                loadApplications();
            }
            if (newVal != null && !newVal.equals("全部职位")) {
                selectedJobLabel.setText(newVal);
                System.out.println("已选择职位：" + newVal);
            } else {
                selectedJobLabel.setText("全部职位");
            }
        });

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // 这里只显示基本信息，因为详细信息需要从原始数据获取
                applicantNameLabel.setText(newVal.get(0));
                applicantStatusLabel.setText(newVal.get(3));
                applicantResumeLabel.setText(newVal.get(5));
                experienceArea.setText("点击申请表查看详细信息");
                coverLetterArea.setText("点击申请表查看详细信息");
                System.out.println("已选择申请人：" + newVal.get(0));
            } else {
                clearApplicantDetail();
            }
        });
    }
    
    public void setUser(model.MO user) {
        this.currentUser = user;
        if (user != null) {
            initJobSelector();
            loadApplications();
        }
    }
    
    private void initJobSelector() {
        ObservableList<String> jobList = FXCollections.observableArrayList();
        jobList.add("全部职位");
        
        // 加载当前MO发布的所有职位
        java.util.List<Job> allJobs = service.DataStorage.getJobs();
        for (Job job : allJobs) {
            if (currentUser != null && currentUser.getId().equals(job.getMoId())) {
                jobList.add(job.getTitle());
            }
        }
        
        jobSelector.setItems(jobList);
        jobSelector.setValue("全部职位");
        selectedJobLabel.setText("全部职位");
    }
    
    private void updateStatistics(java.util.List<Application> applications) {
        int total = applications.size();
        int accepted = 0;
        int pending = 0;
        
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                accepted++;
            } else if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                pending++;
            }
        }
        
        applicantCountLabel.setText(String.valueOf(total));
        acceptedCountLabel.setText(String.valueOf(accepted));
        pendingCountLabel.setText(String.valueOf(pending));
    }
    
    private void loadApplications() {
        String selectedJobTitle = jobSelector.getValue();
        java.util.List<Job> allJobs = service.DataStorage.getJobs();
        java.util.List<Application> allApplications = service.DataStorage.getApplications();
        
        // 过滤出当前MO的申请
        java.util.List<Application> myApplications = new java.util.ArrayList<>();
        
        for (Application app : allApplications) {
            // 找到申请对应的职位
            for (Job job : allJobs) {
                if (job.getId().equals(app.getJobId())) {
                    // 检查这个职位是否属于当前MO
                    if (currentUser != null && currentUser.getId().equals(job.getMoId())) {
                        // 如果选择了特定职位，过滤
                        if ("全部职位".equals(selectedJobTitle) || job.getTitle().equals(selectedJobTitle)) {
                            myApplications.add(app);
                        }
                    }
                    break;
                }
            }
        }
        
        // 更新统计
        updateStatistics(myApplications);
        
        // 加载表格数据
        ObservableList<ObservableList<String>> applicationList = FXCollections.observableArrayList();
        
        for (Application app : myApplications) {
            // 找到TA信息
            String taName = "Unknown";
            String taDepartment = "";
            model.TA ta = null;
            java.util.List<User> users = service.DataStorage.getUsers();
            for (User u : users) {
                if (u.getId().equals(app.getTaId()) && u instanceof model.TA) {
                    ta = (model.TA) u;
                    taName = ta.getName() != null ? ta.getName() : ta.getUsername();
                    taDepartment = ta.getDepartment() != null ? ta.getDepartment() : "";
                    break;
                }
            }
            
            String statusStr = getStatusChinese(app.getStatus());
            String resumeStr = (ta != null && ta.getResumePath() != null && !ta.getResumePath().isEmpty()) 
                    ? "已上传" : "未上传";
            String scoreStr = app.getMatchScore() > 0 ? String.format("%.0f", app.getMatchScore()) : "-";
            
            applicationList.add(FXCollections.observableArrayList(
                    taName,
                    taDepartment,
                    app.getCreatedAt() != null ? app.getCreatedAt() : "-",
                    statusStr,
                    scoreStr,
                    resumeStr
            ));
        }
        
        if (applicationList.isEmpty()) {
            applicationList.add(FXCollections.observableArrayList("暂无申请", "", "", "", "", ""));
        }
        
        applicantsTable.setItems(applicationList);
    }
    
    private String getStatusChinese(ApplicationStatus status) {
        if (status == null) return "待审核";
        switch (status) {
            case PENDING: return "待审核";
            case SCREENED: return "已筛选";
            case ACCEPTED: return "已录用";
            case REJECTED: return "已拒绝";
            default: return "待审核";
        }
    }

    private void initTableColumns() {
        nameColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(0));
                    }
                });

        departmentColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(1));
                    }
                });

        submittedAtColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(2));
                    }
                });

        statusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(3));
                    }
                });

        matchScoreColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(4));
                    }
                });

        resumeStatusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(5));
                    }
                });
    }

    private void clearApplicantDetail() {
        applicantNameLabel.setText("No applicant selected");
        applicantStatusLabel.setText("-");
        applicantResumeLabel.setText("-");
        experienceArea.clear();
        coverLetterArea.clear();
        feedbackArea.clear();
    }

    @FXML
    private void handleBack() {
        System.out.println("返回MO控制台首页");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            
            // 传递当前MO用户，刷新数据
            if (currentUser != null) {
                controller.setUser(currentUser);
            }
            
            Stage stage = (Stage) jobSelector.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("模块组织者控制台");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("返回首页失败");
        }
    }

    @FXML
    private void handleScreenApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        
        // 找到对应的Application对象
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("错误", "找不到申请信息");
            return;
        }
        
        // 更新状态
        app.setStatus(ApplicationStatus.SCREENED);
        app.setReviewComment(feedbackArea.getText());
        app.setUpdatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        // 保存到数据存储
        saveApplications();
        loadApplications();
        
        System.out.println("标记申请人【" + selectedName + "】为已筛选");
        showAlert("操作成功", "已标记为已筛选");
    }

    @FXML
    private void handleAcceptApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("错误", "找不到申请信息");
            return;
        }
        
        String feedback = feedbackArea.getText();
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setReviewComment(feedback);
        app.setUpdatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        saveApplications();
        loadApplications();
        
        System.out.println("录用申请人【" + selectedName + "】，备注：" + feedback);
        showAlert("操作成功", "申请人已录用");
    }

    @FXML
    private void handleRejectApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        
        String feedback = feedbackArea.getText();
        if (feedback == null || feedback.trim().isEmpty()) {
            showAlert("提示", "请填写拒绝原因");
            return;
        }
        
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("错误", "找不到申请信息");
            return;
        }
        
        app.setStatus(ApplicationStatus.REJECTED);
        app.setReviewComment(feedback);
        app.setUpdatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        saveApplications();
        loadApplications();
        
        System.out.println("拒绝申请人【" + selectedName + "】，原因：" + feedback);
        showAlert("操作成功", "申请人已拒绝");
    }
    
    // 根据姓名查找对应的Application对象
    private Application findApplicationByName(String taName) {
        java.util.List<Application> allApps = service.DataStorage.getApplications();
        java.util.List<User> allUsers = service.DataStorage.getUsers();
        
        for (Application app : allApps) {
            for (User u : allUsers) {
                if (u.getId().equals(app.getTaId()) && u instanceof model.TA) {
                    model.TA ta = (model.TA) u;
                    String name = ta.getName() != null ? ta.getName() : ta.getUsername();
                    if (name.equals(taName)) {
                        return app;
                    }
                }
            }
        }
        return null;
    }
    
    private void saveApplications() {
        java.util.List<Application> applications = service.DataStorage.getApplications();
        service.DataStorage.saveApplications(applications);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}