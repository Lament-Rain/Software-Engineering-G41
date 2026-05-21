package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.TA;
import service.UserService;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import controller.SearchBarController;
import utils.FileUtils;
import java.io.File;
import java.util.List;

public class TAUploadResumeController {
    @FXML
    private TextField filePathField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label messageLabel;
    
    private Stage stage;
    private TA user;
    private File selectedFile;
    private KeyboardShortcutService shortcutService;
    
    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleBackAction);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
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
            features.add(new SearchBarController.Feature("Update Profile", () -> handleUpdateProfile(new ActionEvent())));
            features.add(new SearchBarController.Feature("Upload Resume", () -> handleUpload(new ActionEvent())));
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
            messageLabel.setText("Failed to load search bar");
        }
    }

    private void handleUpdateProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAProfileEdit", () -> {
                try {
                    FXMLLoader resumeLoader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
                    Parent resumeRoot = resumeLoader.load();
                    TAUploadResumeController resumeController = resumeLoader.getController();
                    resumeController.setUser(user);
                    resumeController.setStage(stage);
                    
                    Scene scene = new Scene(resumeRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
                    
                    // Force layout update to ensure components resize properly
                    resumeRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Failed to load upload resume page");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Update Profile");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load profile edit page");
        }
    }

    private void handleBackAction() {
        handleBack(new ActionEvent());
    }
    
    public void setUser(TA user) {
        this.user = user;
        if (user != null && user.getResumePath() != null && !user.getResumePath().isEmpty()) {
            filePathField.setText(user.getResumePath());
        }
    }
    
    @FXML
    private void handleBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume File");
        
        // Set file filters
        FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilterPDF);
        
        // Show file selection dialog
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleViewResume(ActionEvent event) {
        if (user == null || user.getResumePath() == null || user.getResumePath().isEmpty()) {
            messageLabel.setText("You haven't uploaded a resume yet");
            return;
        }
        
        try {
            File resumeFile = new File(user.getResumePath());
            if (resumeFile.exists()) {
                java.awt.Desktop.getDesktop().open(resumeFile);
                messageLabel.setText("Opening resume file...");
            } else {
                messageLabel.setText("Resume file does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to open resume file: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpload(ActionEvent event) {
        if (selectedFile == null) {
            messageLabel.setText("Please select a file first");
            return;
        }
        
        if (user == null) {
            messageLabel.setText("User information does not exist");
            return;
        }
        
        if (!FileUtils.checkFileSize(selectedFile)) {
            messageLabel.setText("File size exceeds 10MB limit");
            return;
        }
        
        if (!FileUtils.checkFileFormat(selectedFile.getName())) {
            messageLabel.setText("File format not supported, only PDF files are allowed");
            return;
        }
        
        progressBar.setVisible(true);
        progressBar.setProgress(0.5);
        
        try {
            String resumePath = FileUtils.uploadResume(selectedFile, user.getId());
            user.setResumePath(resumePath);
            
            if (UserService.updateTAProfile(user)) {
                messageLabel.setText("Resume uploaded successfully!");
                progressBar.setProgress(1.0);
            } else {
                messageLabel.setText("Failed to upload resume");
                progressBar.setProgress(0);
                progressBar.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to upload resume: " + e.getMessage());
            progressBar.setProgress(0);
            progressBar.setVisible(false);
        }
    }
    
    @FXML
    private void handleHome(ActionEvent event) {
        handleBack(event);
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
                    FXMLLoader resumeLoader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
                    Parent resumeRoot = resumeLoader.load();
                    TAUploadResumeController resumeController = resumeLoader.getController();
                    resumeController.setUser(user);
                    resumeController.setStage(stage);
                    
                    Scene scene = new Scene(resumeRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
                    
                    // Force layout update to ensure components resize properly
                    resumeRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Failed to load upload resume page");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
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
                    FXMLLoader resumeLoader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
                    Parent resumeRoot = resumeLoader.load();
                    TAUploadResumeController resumeController = resumeLoader.getController();
                    resumeController.setUser(user);
                    resumeController.setStage(stage);
                    
                    Scene scene = new Scene(resumeRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
                    
                    // Force layout update to ensure components resize properly
                    resumeRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Failed to load upload resume page");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application History");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
        }
    }

    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
            Parent root = loader.load();
            TAProfileViewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("TAProfileView", () -> {
                try {
                    FXMLLoader resumeLoader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
                    Parent resumeRoot = resumeLoader.load();
                    TAUploadResumeController resumeController = resumeLoader.getController();
                    resumeController.setUser(user);
                    resumeController.setStage(stage);
                    
                    Scene scene = new Scene(resumeRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
                    
                    // Force layout update to ensure components resize properly
                    resumeRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Failed to load upload resume page");
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user, false);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationHistory.getInstance().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            
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


        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
        }
    }
}
