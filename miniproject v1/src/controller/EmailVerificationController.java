package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.EmailVerificationService;
import service.ToastService;

public class EmailVerificationController {

    @FXML
    private TextField codeField1;
    @FXML
    private TextField codeField2;
    @FXML
    private TextField codeField3;
    @FXML
    private TextField codeField4;
    @FXML
    private TextField codeField5;
    @FXML
    private TextField codeField6;
    @FXML
    private Hyperlink resendLink;
    @FXML
    private Label resendTimer;
    @FXML
    private Button backButton;
    @FXML
    private Button verifyButton;
    @FXML
    private Label emailLabel;
    @FXML
    private Label errorMessage;

    private Stage stage;
    private String email;
    private RegisterController registerController;
    private Timeline resendTimerTimeline;
    private int resendTimeLeft = 60;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEmail(String email) {
        this.email = email;
        emailLabel.setText(email);
        initializeCodeFields();
        sendVerificationCode();
        startResendTimer();
    }

    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }

    private void initializeCodeFields() {
        TextField[] codeFields = {codeField1, codeField2, codeField3, codeField4, codeField5, codeField6};
        for (int i = 0; i < codeFields.length; i++) {
            final int index = i;
            codeFields[i].textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 1) {
                    codeFields[index].setText(newValue.substring(0, 1));
                }
                if (newValue.length() > 0) {
                    if (index < codeFields.length - 1) {
                        codeFields[index + 1].requestFocus();
                    }
                }
            });
            codeFields[i].setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("BACK_SPACE") && codeFields[index].getText().isEmpty() && index > 0) {
                    codeFields[index - 1].requestFocus();
                }
            });
        }
    }

    private void sendVerificationCode() {
        String code = EmailVerificationService.generateVerificationCode();
        EmailVerificationService.storeVerificationCode(email, code);
        EmailVerificationService.sendVerificationCode(email, code);
        ToastService.showToast(stage, "Verification code sent to your email", ToastService.ToastType.INFO);
    }

    private void startResendTimer() {
        resendLink.setDisable(true);
        resendTimeLeft = 60;
        resendTimer.setText("(" + resendTimeLeft + "s)");

        resendTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            resendTimeLeft--;
            resendTimer.setText("(" + resendTimeLeft + "s)");
            if (resendTimeLeft <= 0) {
                resendLink.setDisable(false);
                resendTimer.setText("");
                resendTimerTimeline.stop();
            }
        }));
        resendTimerTimeline.setCycleCount(Timeline.INDEFINITE);
        resendTimerTimeline.play();
    }

    @FXML
    private void handleVerify(ActionEvent event) {
        String code = getEnteredCode();
        if (code.length() != 6) {
            errorMessage.setText("Please enter all 6 digits");
            return;
        }

        if (EmailVerificationService.verifyCode(email, code)) {
            // Code is valid, proceed with registration
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
                Parent root = loader.load();
                RegisterController controller = loader.getController();
                controller.setStage(stage);
                controller.setEmail(email);

                // Get current window size
                double width = stage.getWidth();
                double height = stage.getHeight();
                
                Scene scene = new Scene(root, width, height);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Register");
                
                // Show success message and complete registration
                ToastService.showToast(stage, "Email verified successfully! Please complete your registration.", ToastService.ToastType.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                ToastService.showToast(stage, "Failed to load register page: " + e.getMessage(), ToastService.ToastType.ERROR);
            }
        } else {
            errorMessage.setText("Invalid or expired verification code");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailInput.fxml"));
            Parent root = loader.load();
            EmailInputController controller = loader.getController();
            controller.setStage(stage);
            controller.setRegisterController(registerController);

            // Get current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Email Input");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load email input page: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleResend(ActionEvent event) {
        sendVerificationCode();
        startResendTimer();
        ToastService.showToast(stage, "New verification code sent", ToastService.ToastType.INFO);
    }

    private String getEnteredCode() {
        return codeField1.getText() + codeField2.getText() + codeField3.getText() + 
               codeField4.getText() + codeField5.getText() + codeField6.getText();
    }
}
