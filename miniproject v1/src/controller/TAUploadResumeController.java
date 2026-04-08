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
        FileChooser.ExtensionFilter extFilterWord = new FileChooser.ExtensionFilter("Word Files (*.doc, *.docx)", "*.doc", "*.docx");
        FileChooser.ExtensionFilter extFilterImage = new FileChooser.ExtensionFilter("Image Files (*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilterPDF, extFilterWord, extFilterImage);
        
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
            // Open resume file
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
        
        // Check file size
        if (!FileUtils.checkFileSize(selectedFile)) {
            messageLabel.setText("File size exceeds 10MB limit");
            return;
        }
        
        // Check file format
        if (!FileUtils.checkFileFormat(selectedFile.getName())) {
            messageLabel.setText("File format not supported, only PDF, Word, JPG/PNG are supported");
            return;
        }
        
        // Show progress bar
        progressBar.setVisible(true);
        progressBar.setProgress(0.5);
        
        try {
            // Upload file
            String resumePath = FileUtils.uploadResume(selectedFile, user.getId());
            user.setResumePath(resumePath);
            
            // Update user information
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
    private void handleBack(ActionEvent event) {
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
            messageLabel.setText("Failed to load page");
        }
    }
    
    // Logout
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
            messageLabel.setText("Failed to load page");
        }
    }
}