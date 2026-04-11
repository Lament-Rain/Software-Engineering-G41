package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

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
        welcomeLabel.setText("欢迎，模块组织者");
        typeComboBox.setItems(FXCollections.observableArrayList(
                "课程助教", "实验助教", "科研助教", "行政助教"));
        departmentComboBox.setItems(FXCollections.observableArrayList(
                "计算机学院", "语言学院", "理学院", "商学院", "人工智能学院", "人文学院"));
    }

    @FXML
    private void handleHome() {
        System.out.println("返回MO控制台首页");
        jumpToHome();
    }

    @FXML
    private void handleLogout() {
        System.out.println("退出登录");
    }

    @FXML
    private void handleCancel() {
        System.out.println("取消发布，返回首页");
        jumpToHome();
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String department = departmentComboBox.getValue();
        String recruitNum = recruitNumField.getText();
        String deadline = deadlineField.getText();

        if (title == null || title.trim().isEmpty()) {
            errorMessage.setText("请输入职位标题");
            return;
        }
        if (department == null || department.trim().isEmpty()) {
            errorMessage.setText("请选择所属院系");
            return;
        }
        if (recruitNum == null || recruitNum.trim().isEmpty()) {
            errorMessage.setText("请输入招募人数");
            return;
        }
        if (deadline == null || deadline.trim().isEmpty()) {
            errorMessage.setText("请输入截止时间");
            return;
        }

        try {
            // 创建新Job对象
            model.Job job = new model.Job();
            
            // 生成唯一ID
            java.util.UUID uuid = java.util.UUID.randomUUID();
            job.setId(uuid.toString());
            
            // 设置基本信息
            job.setTitle(title);
            job.setDepartment(department);
            job.setWorkTime(workTimeField.getText() != null ? workTimeField.getText().trim() : "");
            job.setDescription(descriptionArea.getText() != null ? descriptionArea.getText().trim() : "");
            
            // 解析技能要求
            String skillsText = skillsArea.getText();
            if (skillsText != null && !skillsText.trim().isEmpty()) {
                java.util.List<String> skills = java.util.Arrays.asList(skillsText.split("\\n"));
                skills = skills.stream().filter(s -> !s.trim().isEmpty()).collect(java.util.stream.Collectors.toList());
                job.setSkills(skills);
            }
            
            // 解析招募人数
            try {
                int num = Integer.parseInt(recruitNum.trim());
                job.setRecruitNum(num);
            } catch (NumberFormatException e) {
                errorMessage.setText("招募人数必须是数字");
                return;
            }
            
            job.setDeadline(deadline.trim());
            
            // 设置职位类型
            String typeStr = typeComboBox.getValue();
            if (typeStr != null) {
                // 根据现有枚举：MODULE_ASSISTANT, INVIGILATION, OTHER
                switch (typeStr) {
                    case "课程助教":
                    case "实验助教":
                    case "科研助教":
                        job.setType(model.JobType.MODULE_ASSISTANT);
                        break;
                    case "行政助教":
                    default:
                        job.setType(model.JobType.OTHER);
                        break;
                }
            } else {
                job.setType(model.JobType.OTHER);
            }
            
            // 设置发布者MO ID
            String moId = null;
            if (moUser != null) {
                moId = moUser.getId();
            } else if (adminUser != null) {
                moId = adminUser.getId(); // 管理员也可以替MO发布
            }
            job.setMoId(moId);
            
            // 设置状态为PENDING（等待管理员审核）
            job.setStatus(model.JobStatus.PENDING);
            
            // 设置创建时间
            java.time.LocalDate today = java.time.LocalDate.now();
            job.setCreatedAt(today.toString());
            job.setUpdatedAt(today.toString());
            
            // 保存到数据存储
            java.util.List<model.Job> allJobs = service.DataStorage.getJobs();
            allJobs.add(job);
            service.DataStorage.saveJobs(allJobs);
            
            System.out.println("职位发布成功！");
            System.out.println("职位标题：" + title);
            System.out.println("所属院系：" + department);
            System.out.println("招募人数：" + recruitNum);
            System.out.println("截止时间：" + deadline);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提交成功");
            alert.setHeaderText(null);
            alert.setContentText("职位发布成功！等待管理员审核后即可发布，即将返回首页");
            alert.showAndWait();
            
            jumpToHome();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("发布失败：" + e.getMessage());
        }
    }

    private void jumpToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            
            // 传递当前用户，让它刷新数据
            if (moUser != null) {
                controller.setUser(moUser);
            } else if (adminUser != null) {
                // admin用户不应该从这里返回dashboard
            }
            
            Stage stage = (Stage) createJobPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("模块组织者控制台");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("返回首页失败");
        }
    }
}