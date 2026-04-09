package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.UserRole;
import service.UserService;

public class RegisterController {
    @FXML
    private ToggleButton taToggle;
    @FXML
    private ToggleButton moToggle;
    @FXML
    private ToggleButton adminToggle;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField departmentField;
    @FXML
    private Button backButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorMessage;
    @FXML
    private Label successMessage;
    
    private Stage stage;
    private UserRole selectedRole = UserRole.TA; // 默认角色
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void initialize() {
        // 设置默认选中TA角色
        taToggle.setSelected(true);
        
        // 绑定角色选择
        taToggle.setOnAction(e -> selectRole(UserRole.TA));
        moToggle.setOnAction(e -> selectRole(UserRole.MO));
        adminToggle.setOnAction(e -> selectRole(UserRole.ADMIN));
    }
    
    private void selectRole(UserRole role) {
        selectedRole = role;
        taToggle.setSelected(role == UserRole.TA);
        moToggle.setSelected(role == UserRole.MO);
        adminToggle.setSelected(role == UserRole.ADMIN);
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        // 清除之前的消息
        errorMessage.setText("");
        successMessage.setText("");
        
        // 获取输入
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = departmentField.getText().trim();
        
        // 验证输入
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorMessage.setText("请填写所有必填字段");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorMessage.setText("两次输入的密码不一致");
            return;
        }

        String validationMessage = UserService.validateRegistration(username, password, email, phone);
        if (validationMessage != null) {
            errorMessage.setText(validationMessage);
            return;
        }
        
        // 对于MO和Admin，需要填写院系
        if ((selectedRole == UserRole.MO || selectedRole == UserRole.ADMIN) && department.isEmpty()) {
            errorMessage.setText("请填写院系");
            return;
        }
        
        // 注册用户
        boolean success = UserService.register(username, password, email, phone, selectedRole, department) != null;
        
        if (success) {
            successMessage.setText("注册成功！请返回登录页面登录");
            // 清空表单
            clearForm();
        } else {
            errorMessage.setText("注册失败，请检查邮箱、手机号和密码格式");
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        departmentField.setText("");
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
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
            errorMessage.setText("页面加载失败");
        }
    }
}