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
    
    public void setStage(Stage stage) {
        this.stage = stage;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
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