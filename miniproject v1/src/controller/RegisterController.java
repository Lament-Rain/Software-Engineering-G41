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
    private UserRole selectedRole = UserRole.TA;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        taToggle.setSelected(true);
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
        errorMessage.setText("");
        successMessage.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String department = departmentField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorMessage.setText("Please complete all required fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setText("The two passwords do not match.");
            return;
        }

        if (password.length() < 8) {
            errorMessage.setText("Password must be at least 8 characters long.");
            return;
        }

        if (selectedRole == UserRole.MO && department.isEmpty()) {
            errorMessage.setText("Please enter a department for the module organizer account.");
            return;
        }

        boolean success = UserService.register(username, password, email, phone, selectedRole, department) != null;

        if (success) {
            successMessage.setText("Registration successful. Please return to the login page.");
            clearForm();
        } else {
            errorMessage.setText("Registration failed. The username may already exist.");
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
            errorMessage.setText("Failed to load the page.");
        }
    }
}
