package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TA;
import model.ProfileStatus;
import service.UserService;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.Timer;
import java.util.TimerTask;

public class TAProfileEditController {
    @FXML
    private TextField nameField;
    @FXML
    private RadioButton genderMaleRadio;
    @FXML
    private RadioButton genderFemaleRadio;
    @FXML
    private ToggleGroup genderToggleGroup;
    @FXML
    private TextField ageField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField gradeField;
    @FXML
    private TextField studentIdField;
    @FXML
    private TextArea availableTimeField;
    @FXML
    private TextField skillsField;
    @FXML
    private TextArea experienceField;
    @FXML
    private TextArea awardsField;
    @FXML
    private TextField languageSkillsField;
    @FXML
    private TextArea otherSkillsField;
    @FXML
    private Label messageLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox personalInfoModule;
    @FXML
    private VBox workAvailabilityModule;
    @FXML
    private VBox professionalSkillsModule;
    @FXML
    private Button personalInfoBtn;
    @FXML
    private Button workAvailabilityBtn;
    @FXML
    private Button professionalSkillsBtn;
    
    private Stage stage;
    private TA user;
    private Timer autoSaveTimer;
    private static final int AUTO_SAVE_INTERVAL = 60000; // 1分钟
    
    public void initialize() {
        // 添加年龄输入过滤器
        setupAgeFieldFilter();
        
        // 添加实时校验和进度更新
        setupRealTimeValidation();
        
        // 启动自动保存定时器
        startAutoSaveTimer();
    }
    
    private void setupRealTimeValidation() {
        // Add text change listener
        ChangeListener<String> textChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateProgress();
        };
        
        // Add listeners to all required fields
        nameField.textProperty().addListener(textChangeListener);
        departmentField.textProperty().addListener(textChangeListener);
        gradeField.textProperty().addListener(textChangeListener);
        studentIdField.textProperty().addListener(textChangeListener);
        availableTimeField.textProperty().addListener(textChangeListener);
        skillsField.textProperty().addListener(textChangeListener);
        experienceField.textProperty().addListener(textChangeListener);
        
        // Add listener to gender RadioButton
        genderToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateProgress();
        });
        
        // Add special validation for birth year field
        ageField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int birthYear = Integer.parseInt(newValue);
                    int currentYear = java.time.Year.now().getValue();
                    if (birthYear < 1990 || birthYear > currentYear - 16) {
                        showError("Please enter a reasonable birth year");
                    } else {
                        messageLabel.setText("");
                    }
                } catch (NumberFormatException e) {
                    showError("Birth year must be a number");
                }
            } else {
                messageLabel.setText("");
            }
            updateProgress();
        });
        
        // Add format validation for student ID field
        studentIdField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                // Student ID format validation: e.g. 202221xxxx
                if (!newValue.matches("\\d{8,10}")) {
                    showError("Please enter correct student ID format (e.g. 202221xxxx)");
                } else {
                    messageLabel.setText("");
                }
            } else {
                messageLabel.setText("");
            }
            updateProgress();
        });
    }
    
    private void updateProgress() {
        // Calculate number of completed required fields
        int completed = 0;
        int total = 5; // Name, Gender, Department, Grade, Student ID, Available Time, Skills, TA Experience (8 items)
        
        if (!nameField.getText().trim().isEmpty()) completed++;
        if (genderToggleGroup.getSelectedToggle() != null) completed++;
        if (!departmentField.getText().trim().isEmpty()) completed++;
        if (!gradeField.getText().trim().isEmpty()) completed++;
        if (!studentIdField.getText().trim().isEmpty()) completed++;
        if (!availableTimeField.getText().trim().isEmpty()) completed++;
        if (!skillsField.getText().trim().isEmpty()) completed++;
        if (!experienceField.getText().trim().isEmpty()) completed++;
        
        total = 8;
        
        // Update progress bar and label
        double progress = (double) completed / total;
        progressBar.setProgress(progress);
        progressLabel.setText("Completed " + completed + "/" + total + " required items");
    }
    
    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (user != null && user.getProfileStatus() == ProfileStatus.DRAFT) {
                    saveProfile(ProfileStatus.DRAFT);
                }
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setUser(TA user) {
        this.user = user;
        loadUserData();
    }
    
    private void setupAgeFieldFilter() {
        // Only allow numbers input
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,3}")) {
                return change;
            }
            return null;
        };
        ageField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, filter));
    }
    
    private void loadUserData() {
        if (user != null) {
            nameField.setText(user.getName() != null ? user.getName() : "");
            String gender = user.getGender();
            if ("Male".equals(gender)) {
                genderMaleRadio.setSelected(true);
            } else if ("Female".equals(gender)) {
                genderFemaleRadio.setSelected(true);
            }
            ageField.setText(user.getAge() != 0 ? String.valueOf(user.getAge()) : "");
            departmentField.setText(user.getDepartment() != null ? user.getDepartment() : "");
            gradeField.setText(user.getGrade() != null ? user.getGrade() : "");
            studentIdField.setText(user.getStudentId() != null ? user.getStudentId() : "");
            availableTimeField.setText(user.getAvailableTime() != null ? user.getAvailableTime() : "");
            
            // Convert skills list to string
            if (user.getSkills() != null && !user.getSkills().isEmpty()) {
                StringBuilder skillsStr = new StringBuilder();
                for (String skill : user.getSkills()) {
                    skillsStr.append(skill).append("; ");
                }
                skillsField.setText(skillsStr.toString());
            }
            
            experienceField.setText(user.getExperience() != null ? user.getExperience() : "");
            awardsField.setText(user.getAwards() != null ? user.getAwards() : "");
            languageSkillsField.setText(user.getLanguageSkills() != null ? user.getLanguageSkills() : "");
            otherSkillsField.setText(user.getOtherSkills() != null ? user.getOtherSkills() : "");
        }
    }
    
    @FXML
    private void handleSaveDraft(ActionEvent event) {
        saveProfile(ProfileStatus.DRAFT);
    }
    
    @FXML
    private void handleSubmitForReview(ActionEvent event) {
        saveProfile(ProfileStatus.PENDING);
    }
    
    private void saveProfile(ProfileStatus status) {
        if (user == null) {
            showError("User information does not exist");
            return;
        }
        
        // Get input
        String name = nameField.getText().trim();
        String gender = "";
        if (genderMaleRadio.isSelected()) {
            gender = "Male";
        } else if (genderFemaleRadio.isSelected()) {
            gender = "Female";
        }
        String ageStr = ageField.getText().trim();
        String department = departmentField.getText().trim();
        String grade = gradeField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String availableTime = availableTimeField.getText().trim();
        String skillsStr = skillsField.getText().trim();
        String experience = experienceField.getText().trim();
        String awards = awardsField.getText().trim();
        String languageSkills = languageSkillsField.getText().trim();
        String otherSkills = otherSkillsField.getText().trim();
        
        // Validate input
        if (name.isEmpty()) {
            showError("Please enter name");
            nameField.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            showError("Please select gender");
            genderMaleRadio.requestFocus();
            return;
        }
        if (department.isEmpty()) {
            showError("Please enter department");
            departmentField.requestFocus();
            return;
        }
        if (grade.isEmpty()) {
            showError("Please enter grade");
            gradeField.requestFocus();
            return;
        }
        if (studentId.isEmpty()) {
            showError("Please enter student ID");
            studentIdField.requestFocus();
            return;
        }
        if (availableTime.isEmpty()) {
            showError("Please enter available work time");
            availableTimeField.requestFocus();
            return;
        }
        if (skillsStr.isEmpty()) {
            showError("Please enter specialized subjects/skills");
            skillsField.requestFocus();
            return;
        }
        if (experience.isEmpty()) {
            showError("Please enter previous TA experience");
            experienceField.requestFocus();
            return;
        }
        
        // Convert birth year (optional)
        int birthYear = 0;
        if (!ageStr.isEmpty()) {
            try {
                birthYear = Integer.parseInt(ageStr);
                int currentYear = java.time.Year.now().getValue();
                if (birthYear < 1990 || birthYear > currentYear - 16) {
                    showError("Please enter a reasonable birth year");
                    ageField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Birth year must be a number");
                ageField.requestFocus();
                return;
            }
        }
        
        // Convert skills list
        List<String> skills = Arrays.asList(skillsStr.split(";\\s*"));
        
        // Update user information
        user.setName(name);
        user.setGender(gender);
        user.setAge(birthYear); // Store birth year in age field
        user.setDepartment(department);
        user.setGrade(grade);
        user.setStudentId(studentId);
        user.setAvailableTime(availableTime);
        user.setSkills(skills);
        user.setExperience(experience);
        user.setAwards(awards);
        user.setLanguageSkills(languageSkills);
        user.setOtherSkills(otherSkills);
        if (status == ProfileStatus.PENDING) {
            user.setProfileStatus(ProfileStatus.PENDING);
        } else if (user.getProfileStatus() == null) {
            user.setProfileStatus(ProfileStatus.DRAFT);
        }
        
        // Save to database
        if (UserService.updateTAProfile(user)) {
            if (status == ProfileStatus.PENDING) {
                showSuccess("Submitted successfully!");
                // Automatically return to homepage after submission
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(1000); // Wait 1 second for user to see the message
                        handleBack(new ActionEvent());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                showSuccess("Profile updated successfully!");
            }
        } else {
            showError("Failed to update profile");
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        // Reset all input fields
        nameField.clear();
        genderToggleGroup.selectToggle(null);
        ageField.clear();
        departmentField.clear();
        gradeField.clear();
        studentIdField.clear();
        availableTimeField.clear();
        skillsField.clear();
        experienceField.clear();
        awardsField.clear();
        languageSkillsField.clear();
        otherSkillsField.clear();
        messageLabel.setText("");
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
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load page");
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
            showError("Failed to load page");
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
            showError("Failed to load page");
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
            showError("Failed to load page");
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message");
        messageLabel.getStyleClass().add("error-message");
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-message");
        messageLabel.getStyleClass().add("success-message");
    }
    
    private void updateButtonStyles(Button activeButton) {
        // 重置所有按钮样式
        personalInfoBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-padding: 6 12; -fx-background-radius: 4px; -fx-font-weight: normal;");
        workAvailabilityBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-padding: 6 12; -fx-background-radius: 4px; -fx-font-weight: normal;");
        professionalSkillsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-padding: 6 12; -fx-background-radius: 4px; -fx-font-weight: normal;");
        
        // 设置激活按钮样式
        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #0d6efd; -fx-padding: 6 12; -fx-background-radius: 4px; -fx-font-weight: bold;");
        }
    }
    
    @FXML
    private void scrollToPersonalInfo() {
        updateButtonStyles(personalInfoBtn);
        personalInfoModule.requestFocus();
        scrollPane.layout();
        scrollPane.setVvalue(0.0);
    }
    
    @FXML
    private void scrollToWorkAvailability() {
        updateButtonStyles(workAvailabilityBtn);
        workAvailabilityModule.requestFocus();
        scrollPane.layout();
        double workAvailabilityPosition = workAvailabilityModule.getLayoutY() / (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());
        scrollPane.setVvalue(workAvailabilityPosition);
    }
    
    @FXML
    private void scrollToProfessionalSkills() {
        updateButtonStyles(professionalSkillsBtn);
        professionalSkillsModule.requestFocus();
        scrollPane.layout();
        double professionalSkillsPosition = professionalSkillsModule.getLayoutY() / (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());
        scrollPane.setVvalue(professionalSkillsPosition);
    }
}