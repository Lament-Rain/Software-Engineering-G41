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
import utils.FileUtils;
import java.io.File;

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
    
    public void setStage(Stage stage) {
        this.stage = stage;
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
            
            Scene scene = new Scene(root, 800, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load page");
        }
    }
}
