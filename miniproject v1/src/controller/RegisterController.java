package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.UserRole;
import service.FormValidationService;
import service.KeyboardShortcutService;
import service.ToastService;
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

    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private Label confirmPasswordError;
    @FXML
    private Label usernameError;
    @FXML
    private Label phoneError;
    @FXML
    private Label departmentError;

    private Stage stage;
    private UserRole selectedRole = UserRole.TA;
    private KeyboardShortcutService shortcutService;

    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }

    public void setEmail(String email) {
        emailField.setText(email);
    }

    @FXML
    private void initialize() {
        taToggle.setSelected(true);
        taToggle.setOnAction(e -> selectRole(UserRole.TA));
        moToggle.setOnAction(e -> selectRole(UserRole.MO));
        adminToggle.setOnAction(e -> selectRole(UserRole.ADMIN));

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
        FormValidationService.ValidationResult result = FormValidationService.validateRequired(phoneField.getText(), "Phone number");
        if (!result.isValid()) {
            FormValidationService.showError(phoneField, phoneError, result.getErrorMessage());
            return false;
        } else {
            FormValidationService.clearError(phoneField, phoneError);
            return true;
        }
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
    }

    private void selectRole(UserRole role) {
        selectedRole = role;
        taToggle.setSelected(role == UserRole.TA);
        moToggle.setSelected(role == UserRole.MO);
        adminToggle.setSelected(role == UserRole.ADMIN);

        if (role == UserRole.MO) {
            departmentField.setPromptText("Required for Module Organizer");
        } else {
            departmentField.setPromptText("Optional");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        handleRegisterAction();
    }

    private void handleRegisterAction() {
        completeRegistration();
    }

    public void completeRegistration() {
        clearAllErrors();

        if (!validateAllFields()) {
            ToastService.showToast(stage, "Please fix the errors before registering", ToastService.ToastType.WARNING);
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = departmentField.getText().trim();

        if (!password.equals(confirmPassword)) {
            FormValidationService.showError(confirmPasswordField, confirmPasswordError, "Passwords do not match");
            ToastService.showToast(stage, "Passwords do not match", ToastService.ToastType.WARNING);
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Registering...");

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(500), event -> {
            try {
                Object user = UserService.register(username, password, email, phone, selectedRole, department);

                if (user != null) {
                    ToastService.showToast(stage, "Registration successful! Please log in.", ToastService.ToastType.SUCCESS);
                    successMessage.setText("Registration successful. Please return to the login page.");
                    clearForm();
                    registerButton.setDisable(false);
                    registerButton.setText("Create Account");

                    Timeline redirectTimeline = new Timeline();
                    redirectTimeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(2000), ev -> {
                        handleBackAction();
                    }));
                    redirectTimeline.play();
                } else {
                    FormValidationService.showError(usernameField, usernameError, "Username already exists");
                    ToastService.showToast(stage, "Registration failed. Username may already exist.", ToastService.ToastType.ERROR);
                    registerButton.setDisable(false);
                    registerButton.setText("Create Account");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastService.showToast(stage, "Registration failed: " + e.getMessage(), ToastService.ToastType.ERROR);
                registerButton.setDisable(false);
                registerButton.setText("Create Account");
            }
        }));
        timeline.play();
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        departmentField.setText("");
        clearAllErrors();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        handleBackAction();
    }

    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            // Save current window state
            boolean isFullScreen = stage.isFullScreen();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Create new scene without hardcoded size
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Apply saved window state first
            if (isFullScreen) {
                stage.setFullScreen(true);
            } else if (currentWidth > 0 && currentHeight > 0) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            } else {
                // Default size if no previous size
                stage.setWidth(800);
                stage.setHeight(600);
            }
            
            // Set scene first
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
                
            // Force layout update to ensure components resize properly
            root.requestLayout();
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load login page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }
}
