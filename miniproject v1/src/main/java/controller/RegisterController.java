package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
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
        // Select TA by default
        taToggle.setSelected(true);
        
        // Bind role selection
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
        // Clear previous messages
        errorMessage.setText("");
        successMessage.setText("");
        
        // Get input
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String department = departmentField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorMessage.setText("Please fill in all required fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorMessage.setText("Passwords do not match");
            return;
        }
        
        if (password.length() < 8) {
            errorMessage.setText("Password must be at least 8 characters long");
            return;
        }
        
        // For MO, department is required
        if (selectedRole == UserRole.MO && department.isEmpty()) {
            errorMessage.setText("Please enter department");
            return;
        }
        
        // Register user
        User newUser = UserService.register(username, password, email, phone, selectedRole, department);
        
        if (newUser != null) {
            successMessage.setText("Registration successful! Please go back to login page");
            // Clear form
            clearForm();
        } else {
            // Check which validation failed
            if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
                errorMessage.setText("Password must contain both letters and digits");
            } else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                errorMessage.setText("Invalid email format");
            } else if (!phone.matches("^1\\d{10}$")) {
                errorMessage.setText("Invalid phone format, must be 11 digits");
            } else {
                errorMessage.setText("Registration failed, username already exists");
            }
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
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load page");
        }
    }
}