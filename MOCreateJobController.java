package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MOCreateJobController {

    @FXML
    private AnchorPane createJobPane;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private TextField workTimeField;
    @FXML
    private TextField recruitNumField;
    @FXML
    private TextField deadlineField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private Label errorMessage;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("欢迎，模块组织者");
        typeComboBox.setItems(FXCollections.observableArrayList(
                "课程助教", "实验助教", "科研助教", "行政助教"));
        departmentComboBox.setItems(FXCollections.observableArrayList(
                "计算机学院", "语言学院", "理学院", "商学院", "人工智能学院", "人文学院"));
    }

    @FXML
    private void handleHome() {
        System.out.println("返回MO控制台首页");
        jumpToHome();
    }

    @FXML
    private void handleLogout() {
        System.out.println("退出登录");
    }

    @FXML
    private void handleCancel() {
        System.out.println("取消发布，返回首页");
        jumpToHome();
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String department = departmentComboBox.getValue();
        String recruitNum = recruitNumField.getText();
        String deadline = deadlineField.getText();

        if (title == null || title.trim().isEmpty()) {
            errorMessage.setText("请输入职位标题");
            return;
        }
        if (department == null || department.trim().isEmpty()) {
            errorMessage.setText("请选择所属院系");
            return;
        }
        if (recruitNum == null || recruitNum.trim().isEmpty()) {
            errorMessage.setText("请输入招募人数");
            return;
        }
        if (deadline == null || deadline.trim().isEmpty()) {
            errorMessage.setText("请输入截止时间");
            return;
        }

        errorMessage.setText("");
        System.out.println("职位发布成功！");
        System.out.println("职位标题：" + title);
        System.out.println("所属院系：" + department);
        System.out.println("招募人数：" + recruitNum);
        System.out.println("截止时间：" + deadline);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提交成功");
        alert.setHeaderText(null);
        alert.setContentText("职位发布成功！即将返回首页");
        alert.showAndWait();

        jumpToHome();
    }

    private void jumpToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MODashboard.fxml"));
            Stage stage = (Stage) createJobPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("模块组织者控制台");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("返回首页失败");
        }
    }
}