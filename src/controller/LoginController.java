package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;
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
    
    private Stage stage;
    private static final String PREF_NODE = "ta_recruitment_login";
    private static final String PREF_REMEMBER = "remember_me";
    private static final String PREF_USERNAME = "remembered_username";
    private static final String PREF_ROLE = "remembered_role";

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Teaching Assistant", "Module Organizer", "Administrator");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setMinWidth(0);
        
        setupRoleComboBoxDisplay();
        setupForgotPasswordListener();
        setupEnterKeyLogin();
        loadRememberedLogin();
    }
    
    private void setupRoleComboBoxDisplay() {
        roleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Please select your role");
                    setTextFill(Color.web("#9ca3af"));
                } else {
                    setText(item);
                    setTextFill(Color.web("#1f2937"));
                }
            }
        });

        roleComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                setTextFill(Color.web("#1f2937"));
            }
        });
    }

    private void setupForgotPasswordListener() {
        forgotPasswordLabel.setOnMouseClicked(e -> handleForgotPassword());
    }

    private void setupEnterKeyLogin() {
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
        clearErrors();
        
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
            model.User user = service.UserService.login(usernameField.getText(), passwordField.getText());
            if (user == null) {
                errorMessage.setText("Invalid username or password");
                errorMessage.setVisible(true);
                errorMessage.setManaged(true);
                return;
            }

            model.UserRole selectedRole = mapSelectedRole(roleComboBox.getValue());
            if (selectedRole == null) {
                errorMessage.setText("Invalid role selection");
                errorMessage.setVisible(true);
                errorMessage.setManaged(true);
                return;
            }

            if (user.getRole() != selectedRole) {
                errorMessage.setText("Selected role does not match your account role");
                errorMessage.setVisible(true);
                errorMessage.setManaged(true);
                return;
            }

            saveRememberedLogin();
            NavigationHistory.getInstance().clear();
            DataStorage.addLog("LOGIN", user.getId(), "User logged in as " + selectedRole.name());

            switch (selectedRole) {
                case TA:
                    navigateToPageWithUser("TADashboard.fxml", user);
                    break;
                case MO:
                    navigateToPageWithUser("MODashboard.fxml", user);
                    break;
                case ADMIN:
                    navigateToPageWithUser("AdminDashboard.fxml", user);
                    break;
                default:
                    errorMessage.setText("Unsupported role");
                    errorMessage.setVisible(true);
                    errorMessage.setManaged(true);
                    break;
            }
        }
    }

    @FXML
    private void handleCancel() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        rememberMeCheckBox.setSelected(false);
        clearRememberedLogin();
        clearErrors();
    }

    @FXML
    private void handleRegister() {
        navigateToPage("EmailInput.fxml");
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailInput.fxml"));
            Parent root = loader.load();
            EmailInputController controller = loader.getController();
            controller.setStage(stage);
            controller.setFlowMode(EmailInputController.FlowMode.RESET_PASSWORD);

            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Forgot Password");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load forgot password page.");
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }

    private model.UserRole mapSelectedRole(String selectedRoleText) {
        if (selectedRoleText == null) {
            return null;
        }
        switch (selectedRoleText) {
            case "Teaching Assistant":
                return model.UserRole.TA;
            case "Module Organizer":
                return model.UserRole.MO;
            case "Administrator":
                return model.UserRole.ADMIN;
            default:
                return null;
        }
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
    
    private void loadRememberedLogin() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        boolean remember = prefs.getBoolean(PREF_REMEMBER, false);
        rememberMeCheckBox.setSelected(remember);
        if (!remember) {
            return;
        }

        String rememberedUsername = prefs.get(PREF_USERNAME, "");
        String rememberedRole = prefs.get(PREF_ROLE, "");

        if (!rememberedUsername.isEmpty()) {
            usernameField.setText(rememberedUsername);
        }
        if (!rememberedRole.isEmpty() && roleComboBox.getItems().contains(rememberedRole)) {
            roleComboBox.setValue(rememberedRole);
        }
    }

    private void saveRememberedLogin() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        if (!rememberMeCheckBox.isSelected()) {
            clearRememberedLogin();
            return;
        }

        prefs.putBoolean(PREF_REMEMBER, true);
        prefs.put(PREF_USERNAME, usernameField.getText() == null ? "" : usernameField.getText().trim());
        prefs.put(PREF_ROLE, roleComboBox.getValue() == null ? "" : roleComboBox.getValue());
    }

    private void clearRememberedLogin() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.putBoolean(PREF_REMEMBER, false);
        prefs.remove(PREF_USERNAME);
        prefs.remove(PREF_ROLE);
    }

    private void navigateToPage(String fxmlFile) {
        try {
            util.NavigationUtil.navigateTo(stage, "/fxml/" + fxmlFile, controller -> {
                if (controller instanceof EmailInputController) {
                    ((EmailInputController) controller).setStage(stage);
                } else if (controller instanceof LoginController) {
                    ((LoginController) controller).setStage(stage);
                } else if (controller instanceof EmailVerificationController) {
                    ((EmailVerificationController) controller).setStage(stage);
                } else if (controller instanceof RegisterController) {
                    ((RegisterController) controller).setStage(stage);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load page. Please try again.");
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }
    
    private void navigateToPageWithUser(String fxmlFile, model.User user) {
        try {
            util.NavigationUtil.navigateTo(stage, "/fxml/" + fxmlFile, controller -> {
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
            });
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Failed to load page. Please try again.");
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }
}