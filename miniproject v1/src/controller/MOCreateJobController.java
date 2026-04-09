package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MO;
import model.Admin;
import model.Job;
import model.JobStatus;
import model.JobType;
import model.UserRole;
import service.JobService;

import java.util.List;

public class MOCreateJobController {
    @FXML
    private Label welcomeLabel;
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
    private Label reviewStatusLabel;
    @FXML
    private Label reviewCommentLabel;

    private MO moUser;
    private Admin adminUser;
    private UserRole userRole;
    private Stage stage;
    private Job editingJob;

    public void setUser(MO user) {
        this.moUser = user;
        this.userRole = UserRole.MO;
        if (user != null) {
            welcomeLabel.setText("欢迎，" + user.getName());
        }
    }

    public void setAdminUser(Admin admin) {
        this.adminUser = admin;
        this.userRole = UserRole.ADMIN;
        if (admin != null) {
            welcomeLabel.setText("欢迎，管理员 " + admin.getUsername());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEditingJob(Job job) {
        this.editingJob = job;
        if (job == null) {
            return;
        }

        titleField.setText(job.getTitle());
        typeComboBox.setValue(job.getType() != null ? job.getType().name() : null);
        departmentComboBox.setValue(job.getDepartment());
        workTimeField.setText(job.getWorkTime());
        recruitNumField.setText(String.valueOf(job.getRecruitNum()));
        deadlineField.setText(job.getDeadline());
        descriptionArea.setText(job.getDescription());
        skillsArea.setText(job.getSkills() != null ? String.join(", ", job.getSkills()) : "");
        reviewStatusLabel.setText(job.getStatus() != null ? job.getStatus().name() : "-");
        String comment = job.getReviewComment();
        reviewCommentLabel.setText(comment != null && !comment.isEmpty() ? comment : "No review feedback yet");
    }
    
    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        departmentComboBox.getItems().addAll("计算机学院", "理学院", "外语学院", "人文学院", "经济管理学院", "工学院");
        reviewStatusLabel.setText("Draft / New Job");
        reviewCommentLabel.setText("No review feedback yet");
    }
    
    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String workTime = workTimeField.getText();
        String recruitNumStr = recruitNumField.getText();
        String deadline = deadlineField.getText();
        String description = descriptionArea.getText();
        String skills = skillsArea.getText();
        
        if (title.isEmpty() || type == null || department == null || workTime.isEmpty() || recruitNumStr.isEmpty() || deadline.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            errorMessage.setText("请填写所有必填字段");
            return;
        }
        
        int recruitNum;
        try {
            recruitNum = Integer.parseInt(recruitNumStr);
            if (recruitNum <= 0) {
                errorMessage.setText("招募人数必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            errorMessage.setText("招募人数必须是数字");
            return;
        }
        
        List<String> skillsList = java.util.Arrays.asList(skills.split(","));
        String salary = "";
        String location = "";
        String extraRequirements = "";

        boolean success;
        if (editingJob != null) {
            editingJob.setTitle(title);
            editingJob.setType(JobType.valueOf(type));
            editingJob.setDepartment(department);
            editingJob.setWorkTime(workTime);
            editingJob.setRecruitNum(recruitNum);
            editingJob.setDeadline(deadline);
            editingJob.setDescription(description);
            editingJob.setSkills(skillsList);
            editingJob.setSalary(salary);
            editingJob.setLocation(location);
            editingJob.setExtraRequirements(extraRequirements);
            editingJob.setStatus(JobStatus.PENDING);
            editingJob.setReviewComment("");
            success = JobService.updateJob(editingJob);
        } else {
            Job job = null;
            if (userRole == UserRole.MO && moUser != null) {
                job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, moUser.getId(), "MO", moUser.getName());
            } else if (userRole == UserRole.ADMIN && adminUser != null) {
                job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, adminUser.getId(), "ADMIN", adminUser.getUsername());
            }
            success = job != null;
        }

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(editingJob != null ? "重新提交成功" : "发布成功");
            alert.setHeaderText(editingJob != null ? "职位已重新提交审核" : "职位发布成功");
            alert.setContentText(editingJob != null ? "职位状态已更新为 PENDING。" : "职位已成功发布！");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
            handleHome();
        } else {
            errorMessage.setText(editingJob != null ? "职位重新提交失败，请稍后重试" : "职位发布失败，请稍后重试");
        }
    }
    
    @FXML
    private void handleCancel() {
        handleHome();
    }
    
    @FXML
    private void handleHome() {
        try {
            if (userRole == UserRole.MO && moUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                Parent root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser(moUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT国际学校TA招聘系统 - 模块组织者控制台");
            } else if (userRole == UserRole.ADMIN && adminUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser(adminUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT国际学校TA招聘系统 - 管理员控制台");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("登录页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}
