package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import service.ToastService;
import service.UserService;

public class ResetPasswordController {

    @FXML
    private Label emailLabel;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorMessage;

    private Stage stage;
    private String email;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEmail(String email) {
        this.email = email;
        emailLabel.setText(email == null ? "" : email);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        String newPassword = newPasswordField.getText() == null ? "" : newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.setText("Please fill in both password fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setText("Passwords do not match");
            return;
        }

        if (!UserService.resetPassword(email, newPassword)) {
            errorMessage.setText("Reset failed. Check email or password format.");
            return;
        }

        ToastService.showToast(stage, "Password reset successful. Please login.", ToastService.ToastType.SUCCESS);
        navigateToLogin();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to return to login page");
        }
    }
}
