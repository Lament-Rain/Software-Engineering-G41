package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import service.UserService;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorMessage;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private Hyperlink forgotPasswordLink;
    
    private Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void initialize() {
        // 添加角色选项
        roleComboBox.getItems().addAll("TA Applicant", "Module Organizer", "System Admin");
        // 设置默认角色
        roleComboBox.getSelectionModel().selectFirst();
        // 设置选择变化监听器，确保选择后正确显示
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                roleComboBox.setValue(newValue);
            }
        });
        
        // 添加忘记密码链接的点击事件
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();
        
        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("请输入用户名和密码");
            return;
        }
        
        // 登录验证
        User user = UserService.login(username, password);
        if (user == null) {
            errorMessage.setText("登录失败，用户名或密码错误");
            return;
        }
        
        // 验证角色
        UserRole role = null;
        switch (roleStr) {
            case "TA Applicant":
                role = UserRole.TA;
                break;
            case "Module Organizer":
                role = UserRole.MO;
                break;
            case "System Admin":
                role = UserRole.ADMIN;
                break;
        }
        
        if (user.getRole() != role) {
            errorMessage.setText("角色不匹配");
            return;
        }
        
        // 登录成功，跳转到相应的主页
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            
            switch (role) {
                case TA:
                    loader.setLocation(getClass().getResource("/fxml/TADashboard.fxml"));
                    root = loader.load();
                    TADashboardController controller = loader.getController();
                    if (controller != null && user instanceof TA) {
                        // 登录时显示新手引导
                        controller.setUser((TA) user, true);
                    }
                    break;
                case MO:
                    loader.setLocation(getClass().getResource("/fxml/MODashboard.fxml"));
                    root = loader.load();
                    MODashboardController moController = loader.getController();
                    if (moController != null && user instanceof MO) {
                        moController.setUser((MO) user);
                    }
                    break;
                case ADMIN:
                    loader.setLocation(getClass().getResource("/fxml/AdminDashboard.fxml"));
                    root = loader.load();
                    AdminDashboardController adminController = loader.getController();
                    if (adminController != null && user instanceof Admin) {
                        adminController.setUser((Admin) user);
                    }
                    break;
            }
            
            if (root != null) {
                Scene scene = new Scene(root, 1000, 600);
                // 添加样式表
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT国际学校TA招聘系统 - " + roleStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("页面加载失败: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        // 清空输入
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().selectFirst();
        rememberMeCheckBox.setSelected(false);
        errorMessage.setText("");
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent root = loader.load();
            RegisterController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 注册");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("页面加载失败: " + e.getMessage());
        }
    }
    
    private void handleForgotPassword() {
        // 这里可以实现忘记密码的逻辑
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("忘记密码");
        alert.setHeaderText(null);
        alert.setContentText("请联系系统管理员重置密码");
        alert.showAndWait();
    }
}