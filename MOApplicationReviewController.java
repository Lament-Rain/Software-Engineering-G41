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

import java.io.IOException;

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

    @FXML
    public void initialize() {
        initJobSelector();
        initStatData();
        initTableColumns();
        initApplicantTableData();

        jobSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("全部职位")) {
                selectedJobLabel.setText(newVal);
                System.out.println("已选择职位：" + newVal);
            } else {
                selectedJobLabel.setText("全部职位");
            }
        });

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applicantNameLabel.setText(newVal.get(0));
                applicantStatusLabel.setText(newVal.get(3));
                applicantResumeLabel.setText(newVal.get(5));
                experienceArea.setText("2年Java开发经验，熟悉SpringBoot框架，有课程助教经验");
                coverLetterArea.setText("本人认真负责，擅长知识点讲解，能够胜任助教工作，帮助学生解决学习问题。");
                System.out.println("已选择申请人：" + newVal.get(0));
            } else {
                clearApplicantDetail();
            }
        });
    }

    private void initJobSelector() {
        ObservableList<String> jobList = FXCollections.observableArrayList(
                "全部职位", "高级Java助教", "雅思口语助教", "微积分助教", "机器学习助教", "商务英语助教");
        jobSelector.setItems(jobList);
        jobSelector.setValue("全部职位");
        selectedJobLabel.setText("全部职位");
    }

    private void initStatData() {
        applicantCountLabel.setText("15");
        acceptedCountLabel.setText("8");
        pendingCountLabel.setText("7");
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

    private void initApplicantTableData() {
        ObservableList<ObservableList<String>> applicationList = FXCollections.observableArrayList();
        applicationList.add(FXCollections.observableArrayList("张三", "计算机学院", "2026-04-01 10:30", "待审核", "92", "已上传"));
        applicationList.add(FXCollections.observableArrayList("李四", "语言学院", "2026-04-02 14:20", "已通过", "88", "已上传"));
        applicationList.add(FXCollections.observableArrayList("王五", "理学院", "2026-04-03 09:15", "已拒绝", "75", "已上传"));
        applicationList.add(FXCollections.observableArrayList("赵六", "计算机学院", "2026-04-03 16:40", "已录用", "95", "已上传"));
        applicationList.add(FXCollections.observableArrayList("钱七", "商学院", "2026-04-04 11:25", "待审核", "85", "已上传"));
        applicationList.add(FXCollections.observableArrayList("孙八", "人工智能学院", "2026-04-05 15:10", "待审核", "90", "已上传"));
        applicantsTable.setItems(applicationList);
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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MODashboard.fxml"));
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
        ObservableList<String> selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        System.out.println("标记申请人【" + selected.get(0) + "】为已筛选");
        selected.set(3, "已筛选");
        applicantsTable.refresh();
        showAlert("操作成功", "已标记为已筛选");
    }

    @FXML
    private void handleAcceptApplicant() {
        ObservableList<String> selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        String feedback = feedbackArea.getText();
        System.out.println("录用申请人【" + selected.get(0) + "】，备注：" + feedback);
        selected.set(3, "已录用");
        applicantsTable.refresh();
        acceptedCountLabel.setText(String.valueOf(Integer.parseInt(acceptedCountLabel.getText()) + 1));
        pendingCountLabel.setText(String.valueOf(Integer.parseInt(pendingCountLabel.getText()) - 1));
        showAlert("操作成功", "申请人已录用");
    }

    @FXML
    private void handleRejectApplicant() {
        ObservableList<String> selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("提示", "请先选择一位申请人");
            return;
        }
        String feedback = feedbackArea.getText();
        if (feedback == null || feedback.trim().isEmpty()) {
            showAlert("提示", "请填写拒绝原因");
            return;
        }
        System.out.println("拒绝申请人【" + selected.get(0) + "】，原因：" + feedback);
        selected.set(3, "已拒绝");
        applicantsTable.refresh();
        pendingCountLabel.setText(String.valueOf(Integer.parseInt(pendingCountLabel.getText()) - 1));
        showAlert("操作成功", "申请人已拒绝");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}