package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.TA;
import model.Task;
import model.ProfileStatus;
import model.Job;
import model.ApplicationStatus;
import model.Application;
import service.ApplicationService;
import service.JobService;
import service.AIService;
import service.UserService;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class TADashboardController {
    @FXML
    private Label openPositionsLabel;
    @FXML
    private Label myApplicationsLabel;
    @FXML
    private Label pendingActionsLabel;
    @FXML
    private TableView<Task> tasksTable;
    @FXML
    private VBox emptyTasksBox;
    
    private TA user;
    private boolean showWelcomeGuideOnInit = false;
    
    public void setUser(TA user) {
        this.user = user;
        initializeDashboard();
    }
    
    public void setUser(TA user, boolean showWelcomeGuide) {
        this.user = user;
        this.showWelcomeGuideOnInit = showWelcomeGuide;
        initializeDashboard();
    }
    
    private void initializeDashboard() {
        if (user == null) return;
        
        try {
            // Show welcome guide only on first login
            if (showWelcomeGuideOnInit) {
                showWelcomeGuide();
                showWelcomeGuideOnInit = false; // Reset flag after showing
            }
            
            // Load statistics
            loadStatistics();
            
            // Load pending tasks
            loadPendingTasks();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to initialize dashboard");
            alert.setContentText(e.getMessage());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    private void loadStatistics() {
        // Get number of open positions
        int openJobsCount = JobService.getAvailableJobs().size();
        openPositionsLabel.setText(String.valueOf(openJobsCount));
        
        // Get number of my applications
        int myApplicationsCount = ApplicationService.getApplicationsByTA(user.getId()).size();
        myApplicationsLabel.setText(String.valueOf(myApplicationsCount));
        
        // Get number of pending tasks
        // Simplified handling here; actual implementation should calculate based on the user's pending tasks
        int pendingTasksCount = 2; // Mock data
        pendingActionsLabel.setText(String.valueOf(pendingTasksCount));
    }
    
    private void loadPendingTasks() {
        // Get pending tasks from database or service
        // For demonstration, we use mock data
        ObservableList<model.Task> tasks = FXCollections.observableArrayList();
        
        // Check user profile status, add complete profile task if not completed
        if (user.getProfileStatus() == ProfileStatus.DRAFT) {
            tasks.add(new model.Task("Complete Personal Profile", "2026-03-30", "Pending"));
        }
        
        // Check if user has uploaded resume, add upload resume task if not
        if (user.getResumePath() == null || user.getResumePath().isEmpty()) {
            tasks.add(new model.Task("Upload Resume", "2026-03-25", "Pending"));
        }
        
        // Mock other tasks
        tasks.add(new model.Task("Apply for Position", "2026-03-28", "Approved"));
        
        // Set table data
        tasksTable.setItems(tasks);
        
        // Handle empty state
        if (tasks.isEmpty()) {
            tasksTable.setVisible(false);
            emptyTasksBox.setVisible(true);
        } else {
            tasksTable.setVisible(true);
            emptyTasksBox.setVisible(false);
        }
        
        // Update pending tasks count
        int pendingTasksCount = tasks.size();
        pendingActionsLabel.setText(String.valueOf(pendingTasksCount));
    }
    
    // AI Job Matching
    @FXML
    private void handleAIJobMatching(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            // Show prompt message
            Alert alert1 = new Alert(AlertType.INFORMATION);
            alert1.setTitle("Information");
            alert1.setHeaderText("Cannot use AI Job Matching");
            alert1.setContentText("Your profile has not been approved yet, cannot use AI job matching feature.");
            alert1.initModality(Modality.APPLICATION_MODAL);
            alert1.showAndWait();
            return;
        }
        
        Alert alert2 = new Alert(AlertType.INFORMATION);
        alert2.setTitle("Information");
        alert2.setHeaderText("AI Job Matching");
        alert2.setContentText("Analyzing your personal information and skills...");
        alert2.initModality(Modality.APPLICATION_MODAL);
        alert2.showAndWait();
        
        // Get recommended jobs
        List<Job> recommendedJobs = AIService.recommendJobsForTA(user, 5);
        
        if (recommendedJobs.isEmpty()) {
            Alert alert3 = new Alert(AlertType.INFORMATION);
            alert3.setTitle("Information");
            alert3.setHeaderText("AI Job Matching");
            alert3.setContentText("No available job recommendations!");
            alert3.initModality(Modality.APPLICATION_MODAL);
            alert3.showAndWait();
            return;
        }
        
        // Build recommended jobs information
        StringBuilder message = new StringBuilder("=== Recommended Jobs ===\n\n");
        for (int i = 0; i < recommendedJobs.size(); i++) {
            Job job = recommendedJobs.get(i);
            double matchScore = AIService.calculateSkillMatch(user, job);
            message.append((i + 1)).append(". " ).append(job.getTitle()).append(" (" ).append(job.getType()).append(")\n" );
            message.append("   Department: " ).append(job.getDepartment()).append("\n" );
            message.append("   Work Time: " ).append(job.getWorkTime()).append("\n" );
            message.append("   Deadline: " ).append(job.getDeadline()).append("\n" );
            message.append("   Match Score: " ).append(String.format("%.2f%%", matchScore)).append("\n\n" );
        }
        
        Alert alert4 = new Alert(AlertType.INFORMATION);
        alert4.setTitle("AI Job Matching");
        alert4.setHeaderText("Recommended Jobs");
        alert4.setContentText(message.toString());
        alert4.initModality(Modality.APPLICATION_MODAL);
        alert4.showAndWait();
    }
    
    // Skill Gap Identification
    @FXML
    private void handleIdentifyMissingSkills(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Cannot use Skill Gap Identification");
            alert.setContentText("Your profile has not been approved yet, cannot use skill gap identification feature.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list information
        List<Job> availableJobs = JobService.getAvailableJobs();
        if (availableJobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Skill Gap Identification");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list
        StringBuilder jobList = new StringBuilder("Please select a job to analyze skill gaps:\n\n");
        for (int i = 0; i < availableJobs.size(); i++) {
            Job job = availableJobs.get(i);
            jobList.append((i + 1)).append(". " ).append(job.getTitle()).append(" (" ).append(job.getType()).append(")\n" );
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Skill Gap Identification");
        alert.setHeaderText("Select Job");
        alert.setContentText(jobList.toString());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
        
        // Simplified processing here, should pop up a dialog for user to select job in actual implementation
        // For demonstration, we select the first job
        if (!availableJobs.isEmpty()) {
            Job selectedJob = availableJobs.get(0);
            
            Alert analyzingAlert = new Alert(AlertType.INFORMATION);
            analyzingAlert.setTitle("Skill Gap Identification");
            analyzingAlert.setHeaderText("Analyzing");
            analyzingAlert.setContentText("Analyzing your skill match with position \"" + selectedJob.getTitle() + "\"...");
            analyzingAlert.initModality(Modality.APPLICATION_MODAL);
            analyzingAlert.showAndWait();
            
            // Identify missing skills
            List<String> missingSkills = AIService.identifyMissingSkills(user, selectedJob);
            
            if (missingSkills.isEmpty()) {
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Skill Gap Identification");
                successAlert.setHeaderText("Analysis Result");
                successAlert.setContentText("Congratulations! Your skills meet all requirements for this position.");
                successAlert.initModality(Modality.APPLICATION_MODAL);
                successAlert.showAndWait();
            } else {
                // Build skill gap analysis
                StringBuilder analysis = new StringBuilder("=== Skill Gap Analysis ===\n\n");
                analysis.append("You are missing the following skills:\n");
                for (String skill : missingSkills) {
                    analysis.append("- " + skill + "\n");
                }
                
                // Generate skill improvement suggestions
                String suggestions = AIService.generateSkillSuggestions(missingSkills);
                analysis.append("\n" + suggestions);
                
                Alert analysisAlert = new Alert(AlertType.INFORMATION);
                analysisAlert.setTitle("Skill Gap Identification");
                analysisAlert.setHeaderText("Analysis Result");
                analysisAlert.setContentText(analysis.toString());
                analysisAlert.initModality(Modality.APPLICATION_MODAL);
                analysisAlert.showAndWait();
            }
        }
    }
    
    // View Jobs
    @FXML
    private void handleViewJobs(ActionEvent event) {
        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("View Jobs");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list
        StringBuilder jobList = new StringBuilder("=== Available Jobs ===\n\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobList.append((i + 1)).append(". " + job.getTitle() + " (" + job.getType() + ")\n");
            jobList.append("   Department: " + job.getDepartment() + "\n");
            jobList.append("   Work Time: " + job.getWorkTime() + "\n");
            jobList.append("   Recruitment Number: " + job.getRecruitNum() + "\n");
            jobList.append("   Deadline: " + job.getDeadline() + "\n\n");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("View Jobs");
        alert.setHeaderText("Available Jobs");
        alert.setContentText(jobList.toString());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // Apply for Job
    @FXML
    private void handleApplyForJob(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Apply for Job");
            alert.setContentText("Your profile has not been approved yet, cannot apply for jobs.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Apply for Job");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list
        StringBuilder jobList = new StringBuilder("=== Apply for Job ===\n\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobList.append((i + 1)).append(". " + job.getTitle() + " (" + job.getType() + ")\n");
        }
        
        Alert jobAlert = new Alert(AlertType.INFORMATION);
        jobAlert.setTitle("Apply for Job");
        jobAlert.setHeaderText("Select Job");
        jobAlert.setContentText(jobList.toString());
        jobAlert.initModality(Modality.APPLICATION_MODAL);
        jobAlert.showAndWait();
        
        // Simplified processing here, should pop up a dialog for user to select job and input cover letter in actual implementation
        // For demonstration, we select the first job
        if (!jobs.isEmpty()) {
            Job job = jobs.get(0);
            String coverLetter = "I am very interested in this position and hope to have the opportunity to join.";
            
            Application application = ApplicationService.submitApplication(user.getId(), job.getId(), coverLetter);
            if (application != null) {
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Application Success");
                successAlert.setHeaderText("Application Submitted Successfully");
                successAlert.setContentText("Application submitted successfully! Match Score: " + application.getMatchScore());
                successAlert.initModality(Modality.APPLICATION_MODAL);
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(AlertType.INFORMATION);
                errorAlert.setTitle("Application Failed");
                errorAlert.setHeaderText("Failed to Submit Application");
                errorAlert.setContentText("Failed to submit application, you may have already applied for this position or the position has expired.");
                errorAlert.initModality(Modality.APPLICATION_MODAL);
                errorAlert.showAndWait();
            }
        }
    }
    
    // Update Profile
    @FXML
    private void handleUpdateProfile(ActionEvent event) {
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
                // If event not triggered from Button, use other way to get stage
                // Simplified processing here, assume there is a visible stage
                stage = (Stage) tasksTable.getScene().getWindow();
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
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load update profile page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Upload Resume
    @FXML
    private void handleUploadResume(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
            Parent root = loader.load();
            TAUploadResumeController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                // If event not triggered from Button, use other way to get stage
                stage = (Stage) tasksTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load upload resume page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Logout
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage((Stage) ((Button) event.getSource()).getScene().getWindow());
            
            Scene scene = new Scene(root, 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load login page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Show welcome guide
    private void showWelcomeGuide() {
        // Check if user is first login and profile not completed
        // Only show when profile status is DRAFT (initial state) and resume not uploaded
        if (user.getProfileStatus() == ProfileStatus.DRAFT && (user.getResumePath() == null || user.getResumePath().isEmpty())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Welcome to TA Recruitment System");
            alert.setHeaderText("Welcome Guide");
            alert.setContentText("To successfully apply for TA positions, please follow these steps:\n\n" +
                    "1. Complete Personal Profile - Fill in basic information, skills and experience\n" +
                    "2. Upload Resume - Upload your personal resume\n" +
                    "3. Use AI Job Matching - Find the most suitable positions for you\n" +
                    "4. Apply for Jobs - Submit applications and wait for review\n\n" +
                    "Please click 'Update Profile' button to start your TA application journey!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle open positions card click
    @FXML
    private void handleOpenPositionsClick(MouseEvent event) {
        // Show job list
        handleViewJobs(new ActionEvent());
    }
    
    // Handle my applications card click
    @FXML
    public void handleMyApplicationsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
            Parent root = loader.load();
            TAApplicationHistoryController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) tasksTable.getScene().getWindow();
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application History");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load application history page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle pending tasks card click
    @FXML
    private void handlePendingTasksClick(MouseEvent event) {
        // Refresh task list
        loadPendingTasks();
    }
    
    // Handle task table click
    @FXML
    private void handleTaskClick(MouseEvent event) {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // Execute corresponding action based on task name
            if (selectedTask.getName().equals("Complete Personal Profile")) {
                handleUpdateProfile(new ActionEvent());
            } else if (selectedTask.getName().equals("Upload Resume")) {
                handleUploadResume(new ActionEvent());
            } else if (selectedTask.getName().equals("Apply for Position")) {
                handleApplyForJob(new ActionEvent());
            }
        }
    }
    
    // Handle home button click
    @FXML
    private void handleHome(ActionEvent event) {
        // Refresh current page
        initializeDashboard();
    }

    // Handle application management button click
    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        // Show my applications
        handleMyApplicationsClick(new MouseEvent(null, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, null));
    }

    // Handle job requirements button click
    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(user, model.UserRole.TA);

            // Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) tasksTable.getScene().getWindow();
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load job list page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // Handle personal center button click
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        // Navigate to personal center display page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
            Parent root = loader.load();
            TAProfileViewController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) tasksTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load personal center page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}