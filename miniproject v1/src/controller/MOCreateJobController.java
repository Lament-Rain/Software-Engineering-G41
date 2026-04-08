package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MO;
import model.Admin;
import model.Job;
import model.JobType;
import model.UserRole;
import service.JobService;

import java.util.Arrays;
import java.util.List;

public class MOCreateJobController {
    @FXML
    private Label welcomeLabel;
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

    private MO moUser;
    private Admin adminUser;
    private UserRole userRole;
    private Stage stage;

    public void setUser(MO user) {
        this.moUser = user;
        this.userRole = UserRole.MO;
        if (user != null) {
            welcomeLabel.setText("欢迎，" + user.getName());
        }
    }

    public void setAdminUser(Admin admin) {
        this.adminUser = admin;
        this.userRole = UserRole.ADMIN;
        if (admin != null) {
            welcomeLabel.setText("欢迎，管理员 " + admin.getUsername());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void initialize() {
        // 初始化职位类型选项
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        
        // 初始化院系选项
        departmentComboBox.getItems().addAll("计算机学院", "理学院", "外语学院", "人文学院", "经济管理学院", "工学院");
    }
    
    @FXML
    private void handleSubmit() {
        // 验证输入
        String title = titleField.getText();
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String workTime = workTimeField.getText();
        String recruitNumStr = recruitNumField.getText();
        String deadline = deadlineField.getText();
        String description = descriptionArea.getText();
        String skills = skillsArea.getText();
        
        if (title.isEmpty() || type == null || department == null || workTime.isEmpty() || recruitNumStr.isEmpty() || deadline.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            errorMessage.setText("请填写所有必填字段");
            return;
        }
        
        // 验证招募人数
        int recruitNum;
        try {
            recruitNum = Integer.parseInt(recruitNumStr);
            if (recruitNum <= 0) {
                errorMessage.setText("招募人数必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            errorMessage.setText("招募人数必须是数字");
            return;
        }
        
        // 准备参数并保存职位
        List<String> skillsList = java.util.Arrays.asList(skills.split(","));
        String salary = ""; // 默认为空
        String location = ""; // 默认为空
        String extraRequirements = ""; // 默认为空

        Job job = null;
        if (userRole == UserRole.MO && moUser != null) {
            job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, moUser.getId(), "MO", moUser.getName());
        } else if (userRole == UserRole.ADMIN && adminUser != null) {
            job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, adminUser.getId(), "ADMIN", adminUser.getUsername());
        }

        boolean success = job != null;
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("发布成功");
            alert.setHeaderText("职位发布成功");
            alert.setContentText("职位已成功发布！");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();

            // 返回到仪表盘
            handleHome();
        } else {
            errorMessage.setText("职位发布失败，请稍后重试");
        }
    }
    
    @FXML
    private void handleCancel() {
        // 返回到仪表盘
        handleHome();
    }
    
    @FXML
    private void handleHome() {
        try {
            if (userRole == UserRole.MO && moUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                Parent root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser(moUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT国际学校TA招聘系统 - 模块组织者控制台");
            } else if (userRole == UserRole.ADMIN && adminUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser(adminUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT国际学校TA招聘系统 - 管理员控制台");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("登录页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}