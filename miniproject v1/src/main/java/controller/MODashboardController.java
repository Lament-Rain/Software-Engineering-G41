package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import service.DataStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MODashboardController {

    @FXML
    private AnchorPane moDashboardPane;
    @FXML
    private Label publishedJobsLabel;
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label acceptedApplicationsLabel;
    @FXML
    private TableView<ObservableList<String>> jobsTable;
    
    private model.MO user;
    
    public void setUser(model.MO user) {
        this.user = user;
        if (user != null) {
            loadData();
        }
    }

    @FXML
    public void initialize() {
        initJobTableColumns();
    }
    
    public void refreshData() {
        if (user == null) return;
        
        loadData();
    }
    
    private void loadData() {
        if (jobsTable == null || user == null) {
            return; // FXML还没初始化或者用户未设置，稍后再加载
        }
        
        // 加载当前MO发布的所有职位
        java.util.List<Job> allJobs = service.DataStorage.getJobs();
        java.util.List<Job> myJobs = new java.util.ArrayList<>();
        
        for (Job job : allJobs) {
            if (user.getId().equals(job.getMoId())) {
                myJobs.add(job);
            }
        }
        
        // 统计数据
        int publishedCount = myJobs.size();
        publishedJobsLabel.setText(String.valueOf(publishedCount));
        
        // 统计申请数量
        java.util.List<Application> allApplications = service.DataStorage.getApplications();
        int totalApps = 0;
        int acceptedApps = 0;
        
        for (Application app : allApplications) {
            // 检查申请是否属于当前MO的某个职位
            for (Job job : myJobs) {
                if (job.getId().equals(app.getJobId())) {
                    totalApps++;
                    if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                        acceptedApps++;
                    }
                    break;
                }
            }
        }
        
        totalApplicationsLabel.setText(String.valueOf(totalApps));
        acceptedApplicationsLabel.setText(String.valueOf(acceptedApps));
        
        // 加载职位表格数据
        ObservableList<ObservableList<String>> jobList = FXCollections.observableArrayList();
        for (Job job : myJobs) {
            // 统计这个职位的申请数量
            int appCount = 0;
            for (Application app : allApplications) {
                if (job.getId().equals(app.getJobId())) {
                    appCount++;
                }
            }
            jobList.add(FXCollections.observableArrayList(
                    job.getTitle(),
                    job.getDepartment(),
                    job.getDeadline() != null ? job.getDeadline() : "-",
                    String.valueOf(appCount)
            ));
        }
        
        // 如果没有数据，添加提示
        if (jobList.isEmpty()) {
            jobList.add(FXCollections.observableArrayList("暂无发布职位", "", "", "0"));
        }
        
        jobsTable.setItems(jobList);
        jobsTable.refresh();
    }

    @SuppressWarnings("unchecked")
    private void initJobTableColumns() {
        if (jobsTable.getColumns().size() >= 4) {
            TableColumn<ObservableList<String>, String> titleCol = (TableColumn<ObservableList<String>, String>) jobsTable
                    .getColumns().get(0);
            titleCol.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                        @Override
                        public javafx.beans.value.ObservableValue<String> call(
                                TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                            return new javafx.beans.property.SimpleStringProperty(param.getValue().get(0));
                        }
                    });

            TableColumn<ObservableList<String>, String> deptCol = (TableColumn<ObservableList<String>, String>) jobsTable
                    .getColumns().get(1);
            deptCol.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                        @Override
                        public javafx.beans.value.ObservableValue<String> call(
                                TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                            return new javafx.beans.property.SimpleStringProperty(param.getValue().get(1));
                        }
                    });

            TableColumn<ObservableList<String>, String> deadlineCol = (TableColumn<ObservableList<String>, String>) jobsTable
                    .getColumns().get(2);
            deadlineCol.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                        @Override
                        public javafx.beans.value.ObservableValue<String> call(
                                TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                            return new javafx.beans.property.SimpleStringProperty(param.getValue().get(2));
                        }
                    });

            TableColumn<ObservableList<String>, String> countCol = (TableColumn<ObservableList<String>, String>) jobsTable
                    .getColumns().get(3);
            countCol.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                        @Override
                        public javafx.beans.value.ObservableValue<String> call(
                                TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                            return new javafx.beans.property.SimpleStringProperty(param.getValue().get(3));
                        }
                    });
        }
    }

    @FXML
    private void handleHome() {
        refreshData();
    }

    @FXML
    private void handleJobManagement() {
        jumpToJobList("职位列表");
    }

    @FXML
    private void handleApplicationManagement() {
        jumpToUserPage("/fxml/MOApplicationReview.fxml", "申请人审核");
    }

    @FXML
    private void handleStatisticsAnalysis() {
        // TODO: 统计分析页面待开发
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText("统计分析功能待开发");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            Stage currentStage = (Stage) moDashboardPane.getScene().getWindow();
            controller.setStage(currentStage);
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("登出失败");
        }
    }

    @FXML
    private void handleCreateJob() {
        jumpToUserPage("/fxml/MOCreateJob.fxml", "发布TA职位");
    }

    @FXML
    private void handleViewAllJobs() {
        jumpToJobList("全部职位");
    }
    
    private void jumpToJobList(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);
            
            Stage currentStage = (Stage) moDashboardPane.getScene().getWindow();
            controller.setStage(currentStage);
            
            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("页面加载失败: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleViewApplications() {
        jumpToUserPage("/fxml/MOApplicationReview.fxml", "申请人审核");
    }

    @FXML
    private void handleExportData() {
        // TODO: 数据导出待开发
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText("数据导出功能待开发");
        alert.showAndWait();
    }

    @FXML
    private void handleViewStatistics() {
        // TODO: 统计详情页面待开发
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText("统计详情功能待开发");
        alert.showAndWait();
    }

    private void jumpToPage(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) moDashboardPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("页面跳转失败：" + fxmlPath);
        }
    }
    
    private void jumpToUserPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // 设置当前用户到控制器
            if (loader.getController() instanceof MOCreateJobController) {
                ((MOCreateJobController) loader.getController()).setUser(user);
            } else if (loader.getController() instanceof MOApplicationReviewController) {
                ((MOApplicationReviewController) loader.getController()).setUser(user);
            }
            
            Stage stage = (Stage) moDashboardPane.getScene().getWindow();
            if (loader.getController() instanceof MOCreateJobController) {
                ((MOCreateJobController) loader.getController()).setStage(stage);
            }
            
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("页面跳转失败：" + fxmlPath);
        }
    }
}