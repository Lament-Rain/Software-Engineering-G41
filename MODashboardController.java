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

import java.io.IOException;

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

    @FXML
    public void initialize() {
        initStatData();
        initJobTableColumns();
        initJobTableData();
    }

    private void initStatData() {
        publishedJobsLabel.setText("5");
        totalApplicationsLabel.setText("15");
        acceptedApplicationsLabel.setText("8");
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

    private void initJobTableData() {
        ObservableList<ObservableList<String>> jobList = FXCollections.observableArrayList();
        jobList.add(FXCollections.observableArrayList("高级Java助教", "计算机学院", "2026-05-01", "7"));
        jobList.add(FXCollections.observableArrayList("雅思口语助教", "语言学院", "2026-04-25", "4"));
        jobList.add(FXCollections.observableArrayList("微积分助教", "理学院", "2026-04-30", "2"));
        jobList.add(FXCollections.observableArrayList("机器学习助教", "人工智能学院", "2026-05-10", "1"));
        jobList.add(FXCollections.observableArrayList("商务英语助教", "商学院", "2026-04-20", "1"));
        jobsTable.setItems(jobList);
    }

    @FXML
    private void handleHome() {
        System.out.println("当前已在MO控制台首页");
    }

    @FXML
    private void handleJobManagement() {
        System.out.println("跳转职位管理页面");
    }

    @FXML
    private void handleApplicationManagement() {
        System.out.println("跳转申请管理页面");
        jumpToPage("/fxml/MOApplicationReview.fxml", "申请人审核");
    }

    @FXML
    private void handleStatisticsAnalysis() {
        System.out.println("跳转统计分析页面");
    }

    @FXML
    private void handleLogout() {
        System.out.println("退出登录，跳转登录页");
    }

    @FXML
    private void handleCreateJob() {
        System.out.println("跳转发布职位页面");
        jumpToPage("/fxml/MOCreateJob.fxml", "发布TA职位");
    }

    @FXML
    private void handleViewAllJobs() {
        System.out.println("跳转全部职位列表页");
    }

    @FXML
    private void handleViewApplications() {
        System.out.println("跳转申请审核页面");
        jumpToPage("/fxml/MOApplicationReview.fxml", "申请人审核");
    }

    @FXML
    private void handleExportData() {
        System.out.println("执行数据导出");
    }

    @FXML
    private void handleViewStatistics() {
        System.out.println("跳转统计详情页面");
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
}