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
<<<<<<< Updated upstream
=======

        setupFieldValidation();
    }

    private void setupFieldValidation() {
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateEmailField();
        });

        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validatePasswordField();
        });

        confirmPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateConfirmPasswordField();
        });

        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateUsernameField();
        });

        phoneField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validatePhoneField();
        });

        departmentField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateDepartmentField();
        });
    }

    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("enter", this::handleRegisterAction);
        shortcutService.registerShortcut("escape", this::handleBackAction);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }

    private boolean validateEmailField() {
        FormValidationService.ValidationResult result = FormValidationService.validateEmail(emailField.getText());
        if (!result.isValid()) {
            FormValidationService.showError(emailField, emailError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(emailField, emailError);
            return true;
        }
    }

    private boolean validatePasswordField() {
        FormValidationService.ValidationResult result = FormValidationService.validatePassword(passwordField.getText());
        if (!result.isValid()) {
            FormValidationService.showError(passwordField, passwordError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(passwordField, passwordError);
            return true;
        }
    }

    private boolean validateConfirmPasswordField() {
        String confirmPassword = confirmPasswordField.getText();
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            FormValidationService.showError(confirmPasswordField, confirmPasswordError, "Please confirm your password");
            return false;
        }
        if (!confirmPassword.equals(passwordField.getText())) {
            FormValidationService.showError(confirmPasswordField, confirmPasswordError, "Passwords do not match");
            return false;
        } else {
            FormValidationService.clearError(confirmPasswordField, confirmPasswordError);
            return true;
        }
    }

    private boolean validateUsernameField() {
        FormValidationService.ValidationResult result = FormValidationService.validateUsername(usernameField.getText());
        if (!result.isValid()) {
            FormValidationService.showError(usernameField, usernameError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(usernameField, usernameError);
            return true;
        }
    }

    private boolean validatePhoneField() {
        String phone = phoneField.getText();
        FormValidationService.ValidationResult required = FormValidationService.validateRequired(phone, "Phone number");
        if (!required.isValid()) {
            FormValidationService.showError(phoneField, phoneError, required.getErrorMessage());
            return false;
        }

        if (!phone.matches("^1\\d{10}$")) {
            FormValidationService.showError(phoneField, phoneError, "Phone number must be 11 digits and start with 1");
            return false;
        }

        FormValidationService.clearError(phoneField, phoneError);
        return true;
    }

    private boolean validateDepartmentField() {
        if (selectedRole == UserRole.MO) {
            FormValidationService.ValidationResult result = FormValidationService.validateRequired(departmentField.getText(), "Department");
            if (!result.isValid()) {
                FormValidationService.showError(departmentField, departmentError, result.getErrorMessage());
                return false;
            } else {
                FormValidationService.clearError(departmentField, departmentError);
                return true;
            }
        } else {
            FormValidationService.clearError(departmentField, departmentError);
            return true;
        }
    }

    private boolean validateAllFields() {
        boolean valid = true;

        if (!validateEmailField()) valid = false;
        if (!validatePasswordField()) valid = false;
        if (!validateConfirmPasswordField()) valid = false;
        if (!validateUsernameField()) valid = false;
        if (!validatePhoneField()) valid = false;
        if (!validateDepartmentField()) valid = false;

        return valid;
    }

    private void clearAllErrors() {
        FormValidationService.clearError(emailField, emailError);
        FormValidationService.clearError(passwordField, passwordError);
        FormValidationService.clearError(confirmPasswordField, confirmPasswordError);
        FormValidationService.clearError(usernameField, usernameError);
        FormValidationService.clearError(phoneField, phoneError);
        FormValidationService.clearError(departmentField, departmentError);
        errorMessage.setText("");
>>>>>>> Stashed changes
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

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = departmentField.getText().trim();

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

        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            errorMessage.setText("Password must contain both letters and numbers.");
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errorMessage.setText("Please enter a valid email address.");
            return;
        }

        if (!phone.matches("^1\\d{10}$")) {
            errorMessage.setText("Please enter a valid phone number.");
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

<<<<<<< Updated upstream
<<<<<<< Updated upstream
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
=======
=======
>>>>>>> Stashed changes
            boolean isFullScreen = stage.isFullScreen();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            if (isFullScreen) {
                stage.setFullScreen(true);
            } else if (currentWidth > 0 && currentHeight > 0) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            } else {
                stage.setWidth(800);
                stage.setHeight(600);
            }

            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");

            root.requestLayout();
            stage.sizeToScene();
>>>>>>> Stashed changes
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load the page.");
        }
    }
}
