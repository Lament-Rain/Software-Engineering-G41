package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.TA;
import java.util.List;
import controller.TADashboardController;
import controller.TAProfileEditController;
import controller.LoginController;
import controller.SearchBarController;
import service.KeyboardShortcutService;
import service.NavigationHistory;

public class TAProfileViewController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label gradeLabel;
    @FXML
    private Label studentIdLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label availableTimeLabel;
    @FXML
    private Label skillsLabel;
    @FXML
    private Label experienceLabel;
    @FXML
    private Label awardsLabel;
    @FXML
    private Label languageSkillsLabel;
    @FXML
    private Label otherSkillsLabel;
    
    private Stage stage;
    private TA user;
    private KeyboardShortcutService shortcutService;
    
    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleHome);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }
    
    private void handleHome() {
        // If we can go back, do so; otherwise, stay on the current page
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            // Refresh current page
            loadUserData();
        }
    }
    
    private void handleSearchAction() {
        showSearchBar();
    }

    private void showSearchBar() {
        try {
            // Create list of TA features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("Job Requirements", () -> handleJobRequirements(new ActionEvent())));
            features.add(new SearchBarController.Feature("My Applications", () -> handleApplicationManagement(new ActionEvent())));
            features.add(new SearchBarController.Feature("Update Profile", () -> handleEditProfile(new ActionEvent())));
            features.add(new SearchBarController.Feature("Upload Resume", () -> handleUploadResume()));
            features.add(new SearchBarController.Feature("Personal Center", () -> handlePersonalCenter(new ActionEvent())));

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            searchStage.initOwner(stage);
            searchStage.setTitle("Search Features");

            // Set up controller
            controller.setStage(searchStage);
            controller.setFeatures(features);
            controller.setOnFeatureSelected(featureName -> {
                // Find and execute the selected feature
                for (SearchBarController.Feature feature : features) {
                    if (feature.getName().equals(featureName)) {
                        feature.getAction().run();
                        break;
                    }
                }
            });

            // Create scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            searchStage.setScene(scene);



            // Show search bar
            searchStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUploadResume() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
            Parent root = loader.load();
            TAUploadResumeController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAUploadResume", () -> {
                try {
                    FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
                    Parent profileRoot = profileLoader.load();
                    TAProfileViewController profileController = profileLoader.getController();
                    profileController.setUser(user);
                    profileController.setStage(stage);
                    
                    Scene scene = new Scene(profileRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
                    
                    // Force layout update to ensure components resize properly
                    profileRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setUser(TA user) {
        this.user = user;
        // 清除缓存并重新获取用户数据
        if (user != null) {
            service.CacheService.invalidate("ta_profile_" + user.getId());
            model.TA updatedUser = service.UserService.getTAProfile(user.getId());
            if (updatedUser != null) {
                this.user = updatedUser;
            }
        }
        loadUserData();
    }
    
    private void loadUserData() {
        if (user != null) {
            nameLabel.setText(user.getName() != null ? user.getName() : "");
            genderLabel.setText(user.getGender() != null ? user.getGender() : "");
            ageLabel.setText(user.getAge() != 0 ? String.valueOf(user.getAge()) : "");
            departmentLabel.setText(user.getDepartment() != null ? user.getDepartment() : "");
            gradeLabel.setText(user.getGrade() != null ? user.getGrade() : "");
            studentIdLabel.setText(user.getStudentId() != null ? user.getStudentId() : "");
            emailLabel.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneLabel.setText(user.getPhone() != null ? user.getPhone() : "");
            availableTimeLabel.setText(user.getAvailableTime() != null ? user.getAvailableTime() : "");
            
            // Convert skills list to string
            if (user.getSkills() != null && !user.getSkills().isEmpty()) {
                StringBuilder skillsStr = new StringBuilder();
                for (String skill : user.getSkills()) {
                    skillsStr.append(skill).append("; ");
                }
                skillsLabel.setText(skillsStr.toString());
            } else {
                skillsLabel.setText("");
            }
            
            experienceLabel.setText(user.getExperience() != null ? user.getExperience() : "");
            awardsLabel.setText(user.getAwards() != null ? user.getAwards() : "");
            languageSkillsLabel.setText(user.getLanguageSkills() != null ? user.getLanguageSkills() : "");
            otherSkillsLabel.setText(user.getOtherSkills() != null ? user.getOtherSkills() : "");
        }
    }
    
    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            final Stage currentStage;
            if (event.getSource() instanceof Button) {
                currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                currentStage = this.stage;
            }
            
            controller.setStage(currentStage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAProfileEdit", () -> {
                try {
                    FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
                    Parent profileRoot = profileLoader.load();
                    TAProfileViewController profileController = profileLoader.getController();
                    profileController.setUser(user);
                    profileController.setStage(currentStage);
                    
                    Scene scene = new Scene(profileRoot, currentStage.getWidth(), currentStage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    currentStage.setScene(scene);
                    currentStage.setTitle("BUPT International School TA Recruitment System - Personal Center");
                    
                    // Force layout update to ensure components resize properly
                    profileRoot.requestLayout();
                    currentStage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                currentStage.sizeToScene();
            currentStage.setTitle("BUPT International School TA Recruitment System - Update Profile");
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        handleHome(event);
    }
    
    @FXML
    private void handleHome(ActionEvent event) {
        // If we can go back, do so; otherwise, go to dashboard
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
                Parent root = loader.load();
                TADashboardController controller = loader.getController();
                // Returning from other pages, don't show welcome guide
                controller.setUser(user, false);
                
                // Keep current window size and add stylesheet
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                // Add stylesheet
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                    
                    // Force layout update to ensure components resize properly
                    root.requestLayout();
                    stage.sizeToScene();
                stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.TA);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("JobList", () -> {
                try {
                    FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
                    Parent profileRoot = profileLoader.load();
                    TAProfileViewController profileController = profileLoader.getController();
                    profileController.setUser(user);
                    profileController.setStage(stage);
                    
                    Scene scene = new Scene(profileRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
                    
                    // Force layout update to ensure components resize properly
                    profileRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
            Parent root = loader.load();
            TAApplicationHistoryController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAApplicationHistory", () -> {
                try {
                    FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
                    Parent profileRoot = profileLoader.load();
                    TAProfileViewController profileController = profileLoader.getController();
                    profileController.setUser(user);
                    profileController.setStage(stage);
                    
                    Scene scene = new Scene(profileRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
                    
                    // Force layout update to ensure components resize properly
                    profileRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - Application History");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        // Already on personal center page, no action needed
    }
    
    @FXML
    private void handleViewResume(ActionEvent event) {
        if (user == null || user.getResumePath() == null || user.getResumePath().isEmpty()) {
            // Show prompt message
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("View Resume");
            alert.setContentText("You haven't uploaded a resume yet");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        try {
            // Open resume file
            java.io.File resumeFile = new java.io.File(user.getResumePath());
            if (resumeFile.exists()) {
                java.awt.Desktop.getDesktop().open(resumeFile);
                // Show prompt message
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("View Resume");
                alert.setContentText("Opening resume file...");
                alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                alert.showAndWait();
            } else {
                // Show prompt message
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("View Resume");
                alert.setContentText("Resume file does not exist");
                alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Show prompt message
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("View Resume");
            alert.setContentText("Failed to open resume file: " + e.getMessage());
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            
            // Get current stage
            Stage currentStage = null;
            if (event.getSource() instanceof Button) {
                currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                currentStage = stage;
            }
            
            controller.setStage(currentStage);
            
            // Save current window state
            boolean isFullScreen = currentStage.isFullScreen();
            double currentWidth = currentStage.getWidth();
            double currentHeight = currentStage.getHeight();
            
            // Create new scene without hardcoded size
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Apply saved window state
            currentStage.setScene(scene);
            if (isFullScreen) {
                currentStage.setFullScreen(true);
            } else if (currentWidth > 0 && currentHeight > 0) {
                currentStage.setWidth(currentWidth);
                currentStage.setHeight(currentHeight);
            } else {
                // Default size if no previous size
                currentStage.setWidth(800);
                currentStage.setHeight(600);
            }
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                currentStage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}