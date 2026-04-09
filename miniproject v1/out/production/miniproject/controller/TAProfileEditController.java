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
        // 添加文本变化监听器
        ChangeListener<String> textChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateProgress();
        };
        
        // 为所有必填字段添加监听器
        nameField.textProperty().addListener(textChangeListener);
        departmentField.textProperty().addListener(textChangeListener);
        gradeField.textProperty().addListener(textChangeListener);
        studentIdField.textProperty().addListener(textChangeListener);
        availableTimeField.textProperty().addListener(textChangeListener);
        skillsField.textProperty().addListener(textChangeListener);
        experienceField.textProperty().addListener(textChangeListener);
        
        // 为性别RadioButton添加监听器
        genderToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateProgress();
        });
        
        // 为出生年份字段添加特殊校验
        ageField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int birthYear = Integer.parseInt(newValue);
                    int currentYear = java.time.Year.now().getValue();
                    if (birthYear < 1990 || birthYear > currentYear - 16) {
                        showError("请输入合理的出生年份");
                    } else {
                        messageLabel.setText("");
                    }
                } catch (NumberFormatException e) {
                    showError("出生年份必须是数字");
                }
            } else {
                messageLabel.setText("");
            }
            updateProgress();
        });
        
        // 为学号字段添加格式校验
        studentIdField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                // 学号格式校验：如 202221xxxx
                if (!newValue.matches("\\d{8,10}")) {
                    showError("请输入正确的学号格式（如 202221xxxx）");
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
        // 计算必填项完成数量
        int completed = 0;
        int total = 5; // 姓名、性别、院系、年级、学号、可工作时间、擅长科目、TA经验（8项）
        
        if (!nameField.getText().trim().isEmpty()) completed++;
        if (genderToggleGroup.getSelectedToggle() != null) completed++;
        if (!departmentField.getText().trim().isEmpty()) completed++;
        if (!gradeField.getText().trim().isEmpty()) completed++;
        if (!studentIdField.getText().trim().isEmpty()) completed++;
        if (!availableTimeField.getText().trim().isEmpty()) completed++;
        if (!skillsField.getText().trim().isEmpty()) completed++;
        if (!experienceField.getText().trim().isEmpty()) completed++;
        
        total = 8;
        
        // 更新进度条和标签
        double progress = (double) completed / total;
        progressBar.setProgress(progress);
        progressLabel.setText("已完成 " + completed + "/" + total + " 项必填");
    }
    
    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 自动保存草稿
                saveProfile(ProfileStatus.DRAFT);
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
        // 只允许输入数字
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
            if ("男".equals(gender)) {
                genderMaleRadio.setSelected(true);
            } else if ("女".equals(gender)) {
                genderFemaleRadio.setSelected(true);
            }
            ageField.setText(user.getAge() != 0 ? String.valueOf(user.getAge()) : "");
            departmentField.setText(user.getDepartment() != null ? user.getDepartment() : "");
            gradeField.setText(user.getGrade() != null ? user.getGrade() : "");
            studentIdField.setText(user.getStudentId() != null ? user.getStudentId() : "");
            availableTimeField.setText(user.getAvailableTime() != null ? user.getAvailableTime() : "");
            
            // 技能列表转换为字符串
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
            showError("用户信息不存在");
            return;
        }
        
        // 获取输入
        String name = nameField.getText().trim();
        String gender = "";
        if (genderMaleRadio.isSelected()) {
            gender = "男";
        } else if (genderFemaleRadio.isSelected()) {
            gender = "女";
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
        
        // 验证输入
        if (name.isEmpty()) {
            showError("请输入姓名");
            nameField.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            showError("请选择性别");
            genderMaleRadio.requestFocus();
            return;
        }
        if (department.isEmpty()) {
            showError("请输入所属院系");
            departmentField.requestFocus();
            return;
        }
        if (grade.isEmpty()) {
            showError("请输入年级");
            gradeField.requestFocus();
            return;
        }
        if (studentId.isEmpty()) {
            showError("请输入学号");
            studentIdField.requestFocus();
            return;
        }
        if (availableTime.isEmpty()) {
            showError("请输入可工作时间段");
            availableTimeField.requestFocus();
            return;
        }
        if (skillsStr.isEmpty()) {
            showError("请输入擅长科目/技能");
            skillsField.requestFocus();
            return;
        }
        if (experience.isEmpty()) {
            showError("请输入过往TA经验");
            experienceField.requestFocus();
            return;
        }
        
        // 转换出生年份（选填）
        int birthYear = 0;
        if (!ageStr.isEmpty()) {
            try {
                birthYear = Integer.parseInt(ageStr);
                int currentYear = java.time.Year.now().getValue();
                if (birthYear < 1990 || birthYear > currentYear - 16) {
                    showError("请输入合理的出生年份");
                    ageField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                showError("出生年份必须是数字");
                ageField.requestFocus();
                return;
            }
        }
        
        // 转换技能列表
        List<String> skills = Arrays.asList(skillsStr.split(";\\s*"));
        
        // 更新用户信息
        user.setName(name);
        user.setGender(gender);
        user.setAge(birthYear); // 将出生年份存储到age字段中
        user.setDepartment(department);
        user.setGrade(grade);
        user.setStudentId(studentId);
        user.setAvailableTime(availableTime);
        user.setSkills(skills);
        user.setExperience(experience);
        user.setAwards(awards);
        user.setLanguageSkills(languageSkills);
        user.setOtherSkills(otherSkills);
        user.setProfileStatus(status);
        
        // 保存到数据库
        if (UserService.updateTAProfile(user)) {
            showSuccess("档案更新成功！");
        } else {
            showError("档案更新失败");
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        // 重置所有输入字段
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
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            // 从其他页面返回，不显示新手引导
            controller.setUser(user, false);
            
            // 保持当前窗口尺寸并添加样式表
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // 添加样式表
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - TA Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showError("页面加载失败");
        }
    }
    
    // 退出登录
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            
            // 获取当前舞台
            Stage currentStage = null;
            if (event.getSource() instanceof Button) {
                currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                currentStage = stage;
            }
            
            controller.setStage(currentStage);
            
            Scene scene = new Scene(root, 800, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            showError("页面加载失败");
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