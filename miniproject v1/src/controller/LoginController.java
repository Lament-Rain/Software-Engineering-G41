package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< Updated upstream
import javafx.scene.control.*;
import javafx.stage.Stage;
=======
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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
    private static final String PREF_NODE = "ta_recruitment_login";
    private static final String PREF_REMEMBER = "remember_me";
    private static final String PREF_USERNAME = "remembered_username";
    private static final String PREF_ROLE = "remembered_role";

<<<<<<< Updated upstream
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
<<<<<<< Updated upstream
    private void initialize() {
        roleComboBox.getItems().addAll("TA Applicant", "Module Organizer", "System Admin");
        roleComboBox.getSelectionModel().selectFirst();
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                roleComboBox.setValue(newValue);
=======
=======
    private VBox loginCard;
    
    private Stage stage;
    private static final String PREF_NODE = "ta_recruitment_login";
    private static final String PREF_REMEMBER = "remember_me";
    private static final String PREF_USERNAME = "remembered_username";
    private static final String PREF_ROLE = "remembered_role";

>>>>>>> Stashed changes
    @FXML
    public void initialize() {
        // ???????
        roleComboBox.getItems().addAll("Teaching Assistant", "Module Organizer", "Administrator");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setMinWidth(0);
        
        
        
        
        setupRoleComboBoxDisplay();
        
        // ????????
        setupButtonHoverEffects();
        
        // ??????????
        setupFocusEffects();
        
        // ????????
        setupEnterKeyLogin();
        
        // ??????????????
        setupForgotPasswordListener();

        // ????????
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

        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> roleComboBox.requestLayout());
    }

    private void setupForgotPasswordListener() {
        forgotPasswordLabel.setOnMouseClicked(e -> handleForgotPassword());
    }

    private void setupButtonHoverEffects() {
        // ????????
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #1a4b80; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.12), 4, 0.2, 0.0, 0.0); -fx-translate-y: -1px;");
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #003366; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        // ????????
        cancelButton.setOnMouseEntered(e -> {
            cancelButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #f9fafb; -fx-text-fill: #374151; -fx-border-color: #9ca3af; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        cancelButton.setOnMouseExited(e -> {
            cancelButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #ffffff; -fx-text-fill: #374151; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
        
        // ????????
        registerButton.setOnMouseEntered(e -> {
            registerButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.12), 4, 0.2, 0.0, 0.0); -fx-translate-y: -1px;");
        });
        
        registerButton.setOnMouseExited(e -> {
            registerButton.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-font-weight: 500; -fx-padding: 12px 24px; -fx-background-color: #10b981; -fx-text-fill: #ffffff; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        });
    }

    private void setupFocusEffects() {
        // ??????????
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                usernameField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
        
        // ??????????
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                passwordField.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
        
        // ??????????
        roleComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                roleComboBox.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #003366; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0, 51, 102, 0.1), 0, 0, 0, 3);");
            } else {
                roleComboBox.setStyle("-fx-font-family: 'Noto Sans SC', 'Inter', sans-serif; -fx-font-size: 14px; -fx-pref-width: 100%; -fx-min-height: 44px; -fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            }
        });
    }

    private void setupEnterKeyLogin() {
        // ????????????????
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
        // ??????
        clearErrors();
        
        // ????
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
        // ????
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        rememberMeCheckBox.setSelected(false);
        clearRememberedLogin();
        clearErrors();
    }

    @FXML
    private void handleRegister() {
        // ??????????
        navigateToPage("EmailInput.fxml");
    }

    @FXML
    private void handleForgotPassword() {
        // ??????
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            
            // Get current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Scene scene = new Scene(root, width, height);
            
            // ??????????stage
            Object controller = loader.getController();
            if (controller instanceof EmailInputController) {
                ((EmailInputController) controller).setStage(stage);
            } else if (controller instanceof LoginController) {
                ((LoginController) controller).setStage(stage);
            } else if (controller instanceof EmailVerificationController) {
                ((EmailVerificationController) controller).setStage(stage);
            } else if (controller instanceof RegisterController) {
                ((RegisterController) controller).setStage(stage);
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            RegisterController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
=======
            
            // Get current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Scene scene = new Scene(root, width, height);
            
            // ??????????stage??????
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
            
>>>>>>> Stashed changes
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


