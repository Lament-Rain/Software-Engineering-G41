package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import model.*;
import service.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Label usernameError;

    @FXML
    private Label passwordError;

    @FXML
    private Label roleError;

    @FXML
    private Label errorMessage;

    @FXML
    private Label forgotPasswordLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private VBox loginCard;
    
    private Stage stage;

    @FXML
    public void initialize() {
        // 初始化角色选项
        roleComboBox.getItems().addAll("Teaching Assistant", "Module Organizer", "Administrator");
        
        // 设置按钮悬停效果
        setupButtonHoverEffects();
        
        // 设置输入框聚焦效果
        setupFocusEffects();
        
        // 设置回车键登录
        setupEnterKeyLogin();
        
        // 设置忘记密码标签的点击事件
        setupForgotPasswordListener();
    }
    
    private void setupForgotPasswordListener() {
        forgotPasswordLabel.setOnMouseClicked(e -> handleForgotPassword());
    }

    private void setupButtonHoverEffects() {
        // 登录按钮悬停效果
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #1a4b80; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.12), 4, 0.2, 0.0, 0.0); -fx-translate-y: -1px;");
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #003366; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        // 取消按钮悬停效果
        cancelButton.setOnMouseEntered(e -> {
            cancelButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #f9fafb; -fx-text-fill: #374151; -fx-border-color: #9ca3af; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        cancelButton.setOnMouseExited(e -> {
            cancelButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #ffffff; -fx-text-fill: #374151; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        // 注册按钮悬停效果
        registerButton.setOnMouseEntered(e -> {
            registerButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.12), 4, 0.2, 0.0, 0.0); -fx-translate-y: -1px;");
        });
        
        registerButton.setOnMouseExited(e -> {
            registerButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #10b981; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
    }

    private void setupFocusEffects() {
        // 用户名输入框聚焦效果
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                usernameField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
        
        // 密码输入框聚焦效果
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                passwordField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
        
        // 角色下拉框聚焦效果
        roleComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                roleComboBox.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                roleComboBox.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
    }

    private void setupEnterKeyLogin() {
        // 为所有输入控件添加回车键登录功能
        usernameField.setOnKeyPressed(this::handleEnterKey);
        passwordField.setOnKeyPressed(this::handleEnterKey);
        roleComboBox.setOnKeyPressed(this::handleEnterKey);
        rememberMeCheckBox.setOnKeyPressed(this::handleEnterKey);
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {
        // 重置错误消息
        clearErrors();
        
        // 验证表单
        boolean isValid = true;
        
        if (usernameField.getText().isEmpty()) {
            usernameError.setText("Please enter username");
            usernameError.setVisible(true);
            usernameError.setManaged(true);
            isValid = false;
        }
        
        if (passwordField.getText().isEmpty()) {
            passwordError.setText("Please enter password");
            passwordError.setVisible(true);
            passwordError.setManaged(true);
            isValid = false;
        }
        
        if (roleComboBox.getValue() == null) {
            roleError.setText("Please select role");
            roleError.setVisible(true);
            roleError.setManaged(true);
            isValid = false;
        }
        
        if (isValid) {
            // 获取用户对象
            model.User user = service.UserService.login(usernameField.getText(), passwordField.getText());
            if (user != null) {
                // 根据角色跳转到不同的仪表盘
                String role = roleComboBox.getValue();
                if (role.equals("Teaching Assistant")) {
                    // 跳转到TA仪表盘
                    navigateToPageWithUser("TADashboard.fxml", user);
                } else if (role.equals("Module Organizer")) {
                    // 跳转到MO仪表盘
                    navigateToPageWithUser("MODashboard.fxml", user);
                } else if (role.equals("Administrator")) {
                    // 跳转到Admin仪表盘
                    navigateToPageWithUser("AdminDashboard.fxml", user);
                }
            } else {
                errorMessage.setText("Invalid username or password");
                errorMessage.setVisible(true);
                errorMessage.setManaged(true);
            }
        }
    }

    @FXML
    private void handleCancel() {
        // 重置表单
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        rememberMeCheckBox.setSelected(false);
        clearErrors();
    }

    @FXML
    private void handleRegister() {
        // 跳转到邮箱输入页面
        navigateToPage("EmailInput.fxml");
    }

    @FXML
    private void handleForgotPassword() {
        // 处理忘记密码
        System.out.println("Forgot Password clicked");
    }

    private void clearErrors() {
        usernameError.setVisible(false);
        usernameError.setManaged(false);
        passwordError.setVisible(false);
        passwordError.setManaged(false);
        roleError.setVisible(false);
        roleError.setManaged(false);
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void navigateToPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            
            // Get current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Scene scene = new Scene(root, width, height);
            
            // 获取控制器实例并设置stage
            Object controller = loader.getController();
            if (controller instanceof EmailInputController) {
                ((EmailInputController) controller).setStage(stage);
            } else if (controller instanceof LoginController) {
                ((LoginController) controller).setStage(stage);
            } else if (controller instanceof EmailVerificationController) {
                ((EmailVerificationController) controller).setStage(stage);
            } else if (controller instanceof RegisterController) {
                ((RegisterController) controller).setStage(stage);
            }
            
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load page. Please try again.");
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }
    
    private void navigateToPageWithUser(String fxmlFile, model.User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            
            // Get current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Scene scene = new Scene(root, width, height);
            
            // 获取控制器实例并设置stage和用户对象
            Object controller = loader.getController();
            if (controller instanceof TADashboardController && user instanceof model.TA) {
                ((TADashboardController) controller).setUser((model.TA) user);
                ((TADashboardController) controller).setStage(stage);
            } else if (controller instanceof MODashboardController && user instanceof model.MO) {
                ((MODashboardController) controller).setUser((model.MO) user);
                ((MODashboardController) controller).setStage(stage);
            } else if (controller instanceof AdminDashboardController && user instanceof model.Admin) {
                ((AdminDashboardController) controller).setUser((model.Admin) user);
                ((AdminDashboardController) controller).setStage(stage);
            }
            
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load page. Please try again.");
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }
}
