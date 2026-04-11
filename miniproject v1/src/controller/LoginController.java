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
        roleComboBox.getItems().addAll("TA Applicant", "Module Organizer", "System Admin");
        roleComboBox.getSelectionModel().selectFirst();
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                roleComboBox.setValue(newValue);
            }
        });
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please enter both username and password.");
            return;
        }

        User user = UserService.login(username, password);
        if (user == null) {
            errorMessage.setText("Login failed. Invalid username or password.");
            return;
        }

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
            errorMessage.setText("The selected role does not match this account.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;

            switch (role) {
                case TA:
                    loader.setLocation(getClass().getResource("/fxml/TADashboard.fxml"));
                    root = loader.load();
                    TADashboardController controller = loader.getController();
                    if (controller != null && user instanceof TA) {
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
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - " + roleStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load the page: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
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
            stage.setTitle("BUPT International School TA Recruitment System - Register");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load the page: " + e.getMessage());
        }
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact the system administrator to reset your password.");
        alert.showAndWait();
    }
}
