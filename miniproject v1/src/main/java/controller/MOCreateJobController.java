package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MOCreateJobController {

    @FXML
    private AnchorPane createJobPane;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private TextField workTimeField;
    @FXML
    private TextField recruitNumField;
    @FXML
    private TextField deadlineField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private Label errorMessage;
    @FXML
    private Button cancelButton;
    @FXML
    private Button submitButton;
    @FXML
    private Label welcomeLabel;
    
    private Stage stage;
    private model.Admin adminUser;
    private model.MO moUser;
    
    public void setAdminUser(model.Admin user) {
        this.adminUser = user;
    }
    
    public void setUser(model.MO user) {
        this.moUser = user;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, Module Organizer");
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Course TA", "Lab TA", "Research TA", "Admin TA"));
        departmentComboBox.setItems(FXCollections.observableArrayList(
                "School of Computer Science", "School of Languages", "School of Science", 
                "Business School", "School of AI", "School of Humanities"));
    }

    @FXML
    private void handleHome() {
        System.out.println("Back to MO Dashboard");
        jumpToHome();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            Stage currentStage = (Stage) createJobPane.getScene().getWindow();
            controller.setStage(currentStage);
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to logout");
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel posting, back to dashboard");
        jumpToHome();
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String department = departmentComboBox.getValue();
        String recruitNum = recruitNumField.getText();
        String deadline = deadlineField.getText();

        if (title == null || title.trim().isEmpty()) {
            errorMessage.setText("Please enter job title");
            return;
        }
        if (department == null || department.trim().isEmpty()) {
            errorMessage.setText("Please select department");
            return;
        }
        if (recruitNum == null || recruitNum.trim().isEmpty()) {
            errorMessage.setText("Please enter number of openings");
            return;
        }
        if (deadline == null || deadline.trim().isEmpty()) {
            errorMessage.setText("Please enter application deadline");
            return;
        }

        try {
            // Create new Job object
            model.Job job = new model.Job();
            
            // Generate unique ID
            UUID uuid = UUID.randomUUID();
            job.setId(uuid.toString());
            
            // Set basic information
            job.setTitle(title);
            job.setDepartment(department);
            job.setWorkTime(workTimeField.getText() != null ? workTimeField.getText().trim() : "");
            job.setDescription(descriptionArea.getText() != null ? descriptionArea.getText().trim() : "");
            
            // Parse skills
            String skillsText = skillsArea.getText();
            if (skillsText != null && !skillsText.trim().isEmpty()) {
                List<String> skills = Arrays.asList(skillsText.split("\\n"));
                skills = skills.stream().filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
                job.setSkills(skills);
            }
            
            // Parse recruit number
            try {
                int num = Integer.parseInt(recruitNum.trim());
                job.setRecruitNum(num);
            } catch (NumberFormatException e) {
                errorMessage.setText("Number of openings must be a number");
                return;
            }
            
            job.setDeadline(deadline.trim());
            
            // Set job type
            String typeStr = typeComboBox.getValue();
            if (typeStr != null) {
                // According to existing enum: MODULE_ASSISTANT, INVIGILATION, OTHER
                switch (typeStr) {
                    case "Course TA":
                    case "Lab TA":
                    case "Research TA":
                        job.setType(model.JobType.MODULE_ASSISTANT);
                        break;
                    case "Admin TA":
                    default:
                        job.setType(model.JobType.OTHER);
                        break;
                }
            } else {
                job.setType(model.JobType.OTHER);
            }
            
            // Set publisher MO ID
            String moId = null;
            if (moUser != null) {
                moId = moUser.getId();
            } else if (adminUser != null) {
                moId = adminUser.getId(); // Admin can also post for MO
            }
            job.setMoId(moId);
            
            // Set status to PENDING (waiting for admin approval)
            job.setStatus(model.JobStatus.PENDING);
            
            // Set creation time
            java.time.LocalDate today = java.time.LocalDate.now();
            job.setCreatedAt(today.toString());
            job.setUpdatedAt(today.toString());
            
            // Save to data storage
            List<model.Job> allJobs = service.DataStorage.getJobs();
            allJobs.add(job);
            service.DataStorage.saveJobs(allJobs);
            
            System.out.println("Job posted successfully!");
            System.out.println("Job title: " + title);
            System.out.println("Department: " + department);
            System.out.println("Openings: " + recruitNum);
            System.out.println("Deadline: " + deadline);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Submission Success");
            alert.setHeaderText(null);
            alert.setContentText("Job posted successfully! It will be published after admin approval. Back to dashboard...");
            alert.showAndWait();
            
            jumpToHome();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Failed to post: " + e.getMessage());
            
            // Show alert dialog to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to post job: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void jumpToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            
            // Pass current user to refresh data
            if (moUser != null) {
                controller.setUser(moUser);
            }
            
            Stage stage = (Stage) createJobPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("Module Organizer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to return to dashboard");
        }
    }
}
