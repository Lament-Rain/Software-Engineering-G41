package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.CaptchaService;
import service.ToastService;

public class EmailInputController {

    public enum FlowMode {
        REGISTER,
        RESET_PASSWORD
    }

    @FXML
    private TextField emailField;
    @FXML
    private TextField captchaField;
    @FXML
    private Label captchaLabel;
    @FXML
    private Button refreshCaptchaButton;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label emailError;
    @FXML
    private Label captchaError;
    @FXML
    private Label errorMessage;

    private Stage stage;
    private String currentCaptcha;
    private RegisterController registerController;
    private FlowMode flowMode = FlowMode.REGISTER;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }

    public void setFlowMode(FlowMode flowMode) {
        this.flowMode = flowMode == null ? FlowMode.REGISTER : flowMode;
    }

    @FXML
    private void initialize() {
        generateNewCaptcha();
        refreshCaptchaButton.setOnMouseClicked(this::handleRefreshCaptcha);
    }

    private void generateNewCaptcha() {
        currentCaptcha = CaptchaService.generateCaptcha();
        captchaLabel.setText(currentCaptcha);
    }

    private void handleRefreshCaptcha(MouseEvent event) {
        generateNewCaptcha();
        captchaField.clear();
        clearCaptchaError();
    }

    @FXML
    private void handleNext(ActionEvent event) {
        clearAllErrors();

        boolean isValid = true;

        if (!validateEmailField()) {
            isValid = false;
        }

        if (!validateCaptchaField()) {
            isValid = false;
        }

        if (isValid) {
            String email = emailField.getText().trim();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailVerification.fxml"));
                Parent root = loader.load();
                EmailVerificationController controller = loader.getController();
                controller.setStage(stage);
                controller.setEmail(email);
                controller.setRegisterController(registerController);
                controller.setFlowMode(flowMode);

                double width = stage.getWidth();
                double height = stage.getHeight();

                Scene scene = new Scene(root, width, height);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Email Verification");
            } catch (Exception e) {
                e.printStackTrace();
                ToastService.showToast(stage, "Failed to load email verification page: " + e.getMessage(), ToastService.ToastType.ERROR);
            }
        } else {
            showErrorMessage();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
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
            ToastService.showToast(stage, "Failed to load login page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private boolean validateEmailField() {
        String email = emailField.getText().trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (email.isEmpty() || !email.matches(emailRegex)) {
            showEmailError("Please enter a valid email format, e.g. name@company.com");
            return false;
        } else {
            clearEmailError();
            return true;
        }
    }
    private boolean validateCaptchaField() {
        String input = captchaField.getText().trim();
        if (input.isEmpty() || input.length() != 4) {
            showCaptchaError("Please enter the correct captcha");
            return false;
        } else if (!CaptchaService.validateCaptcha(input, currentCaptcha)) {
            showCaptchaError("Please enter the correct captcha");
            return false;
        } else {
            clearCaptchaError();
            return true;
        }
    }

    private void clearAllErrors() {
        clearEmailError();
        clearCaptchaError();
        clearErrorMessage();
    }

    private void showEmailError(String message) {
        emailError.setText(message);
        emailError.setVisible(true);
        emailError.setManaged(true);
    }

    private void clearEmailError() {
        emailError.setText("");
        emailError.setVisible(false);
        emailError.setManaged(false);
    }

    private void showCaptchaError(String message) {
        captchaError.setText(message);
        captchaError.setVisible(true);
        captchaError.setManaged(true);
    }

    private void clearCaptchaError() {
        captchaError.setText("");
        captchaError.setVisible(false);
        captchaError.setManaged(false);
    }

    private void showErrorMessage() {
        errorMessage.setText("Please fix the errors above");
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    private void clearErrorMessage() {
        errorMessage.setText("");
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
    }
}


