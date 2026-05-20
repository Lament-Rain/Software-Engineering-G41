package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import model.TA;
import java.util.List;
import controller.TADashboardController;
import controller.TAProfileEditController;
import controller.LoginController;
<<<<<<< Updated upstream
=======
import controller.SearchBarController;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import service.WorkloadService;
import service.AdminConfigService;
import model.AdminConfig;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
    private Label availableTimeLabel;
=======
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    @FXML
    private Label skillsLabel;
    @FXML
    private Label experienceLabel;
    @FXML
    private Label awardsLabel;
    @FXML
    private Label otherSkillsLabel;
    @FXML
    private AnchorPane workloadGaugePane;
    
    private Stage stage;
    private TA user;
    
    public void setStage(Stage stage) {
        this.stage = stage;
<<<<<<< Updated upstream
=======
        setupKeyboardShortcuts();
        if (workloadGaugePane != null) {
            workloadGaugePane.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (user != null) {
                    renderWorkloadGauge(WorkloadService.getCurrentWorkload(user), AdminConfigService.loadConfig());
                }
            });
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user, false);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
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
>>>>>>> Stashed changes
    }
    
    public void setUser(TA user) {
        this.user = user;
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
<<<<<<< Updated upstream
            availableTimeLabel.setText(user.getAvailableTime() != null ? user.getAvailableTime() : "");
=======
            emailLabel.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneLabel.setText(user.getPhone() != null ? user.getPhone() : "");
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            
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
            otherSkillsLabel.setText(user.getOtherSkills() != null ? user.getOtherSkills() : "");

            int workload = WorkloadService.getCurrentWorkload(user);
            AdminConfig cfg = AdminConfigService.loadConfig();
            renderWorkloadGauge(workload, cfg);
        }
    }
    
    private void renderWorkloadGauge(int workload, AdminConfig cfg) {
        if (workloadGaugePane == null || cfg == null) {
            return;
        }

        workloadGaugePane.getChildren().clear();

        double paneWidth = workloadGaugePane.getWidth() > 0 ? workloadGaugePane.getWidth() : workloadGaugePane.getPrefWidth();
        if (paneWidth <= 0) {
            paneWidth = 340;
        }

        double left = 12;
        double right = Math.max(left + 110, paneWidth - 12);
        double barY = 18;
        double barH = 8;

        int low = cfg.getLowThreshold();
        int mid = cfg.getMidThreshold();
        int high = cfg.getHighThreshold();
        int max = cfg.getMaxThreshold();
        if (max <= low) {
            max = Math.max(low + 1, 1);
        }

        double usable = right - left;
        double xLow = left;
        double xMid = left + usable * ((double) (mid - low) / (max - low));
        double xHigh = left + usable * ((double) (high - low) / (max - low));
        double xMax = right;

        Rectangle lowSeg = new Rectangle(xLow, barY, Math.max(0, xMid - xLow), barH);
        lowSeg.setArcWidth(6);
        lowSeg.setArcHeight(6);
        lowSeg.setStyle("-fx-fill: #16a34a;");

        Rectangle midSeg = new Rectangle(xMid, barY, Math.max(0, xHigh - xMid), barH);
        midSeg.setStyle("-fx-fill: #eab308;");

        Rectangle highSeg = new Rectangle(xHigh, barY, Math.max(0, xMax - xHigh), barH);
        highSeg.setArcWidth(6);
        highSeg.setArcHeight(6);
        highSeg.setStyle("-fx-fill: #dc2626;");

        Line midLine = new Line(xMid, barY - 2, xMid, barY + barH + 2);
        midLine.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 2;");
        Line highLine = new Line(xHigh, barY - 2, xHigh, barY + barH + 2);
        highLine.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 2;");

        Label lowTick = createTickLabel(String.valueOf(low), xLow - 4, 0);
        Label midTick = createTickLabel(String.valueOf(mid), xMid - 8, 0);
        Label highTick = createTickLabel(String.valueOf(high), xHigh - 10, 0);
        Label maxTick = createTickLabel(String.valueOf(max), xMax - 12, 0);

        double clamped = Math.max(low, Math.min(workload, max));
        double xCurrent = left + usable * ((clamped - low) / (double) (max - low));

        Polygon arrow = new Polygon(
                xCurrent, barY + barH + 2,
                xCurrent - 6, barY + barH + 12,
                xCurrent + 6, barY + barH + 12
        );
        arrow.setStyle("-fx-fill: #1f2937;");

        Label currentLabel = createTickLabel(workload + "h", xCurrent - 12, barY + barH + 14);
        currentLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #1f2937;");

        workloadGaugePane.getChildren().addAll(
                lowSeg, midSeg, highSeg,
                midLine, highLine,
                lowTick, midTick, highTick, maxTick,
                arrow, currentLabel
        );
    }

    private Label createTickLabel(String text, double x, double y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #6b7280;");
        return label;
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = this.stage;
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Update Profile");
            stage.centerOnScreen();
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
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.TA);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
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

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
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
            
            Scene scene = new Scene(root, 800, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}