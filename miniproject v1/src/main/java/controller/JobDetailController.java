package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;
import model.Application;
import model.Job;
import model.MO;
import model.ProfileStatus;
import model.TA;
import model.UserRole;
import service.ApplicationService;
import service.JobService;

import java.util.List;

public class JobDetailController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label workTimeLabel;
    @FXML
    private Label recruitNumLabel;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Label salaryLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label publishDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private TextArea extraRequirementsArea;
    @FXML
    private Button applyButton;
    @FXML
    private Button backButton;

    private Job currentJob;
    private Object currentUser;
    private UserRole userRole;
    private Stage stage;

    public void setJob(String jobId, Object user, UserRole role, Stage stage) {
        this.currentUser = user;
        this.userRole = role;
        this.stage = stage;
        this.currentJob = JobService.getJobById(jobId);

        if (currentJob != null) {
            loadJobDetails();
        }
    }

    private void loadJobDetails() {
        titleLabel.setText(currentJob.getTitle());
        departmentLabel.setText(currentJob.getDepartment());
        typeLabel.setText(currentJob.getType() != null ? currentJob.getType().toString() : "");
        workTimeLabel.setText(currentJob.getWorkTime());
        recruitNumLabel.setText(String.valueOf(currentJob.getRecruitNum()));
        deadlineLabel.setText(currentJob.getDeadline());
        salaryLabel.setText(currentJob.getSalary() != null && !currentJob.getSalary().isEmpty() ? currentJob.getSalary() : "未指定");
        locationLabel.setText(currentJob.getLocation() != null && !currentJob.getLocation().isEmpty() ? currentJob.getLocation() : "未指定");

        String publisher = currentJob.getPublisherName();
        if (publisher == null || publisher.isEmpty()) {
            if ("ADMIN".equals(currentJob.getPublisherType())) {
                publisher = "管理员";
            } else {
                publisher = "模块组织者";
            }
        }
        publisherLabel.setText(publisher);

        String publishDate = "";
        if (currentJob.getCreatedAt() != null && currentJob.getCreatedAt().length() >= 10) {
            publishDate = currentJob.getCreatedAt().substring(0, 10);
        }
        publishDateLabel.setText(publishDate);

        statusLabel.setText(currentJob.getStatus() != null ? currentJob.getStatus().toString() : "UNKNOWN");

        descriptionArea.setText(currentJob.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);

        List<String> skills = currentJob.getSkills();
        if (skills != null && !skills.isEmpty()) {
            skillsArea.setText(String.join("\n", skills));
        } else {
            skillsArea.setText("无特殊技能要求");
        }
        skillsArea.setEditable(false);
        skillsArea.setWrapText(true);

        String extraReq = currentJob.getExtraRequirements();
        extraRequirementsArea.setText(extraReq != null && !extraReq.isEmpty() ? extraReq : "无额外要求");
        extraRequirementsArea.setEditable(false);
        extraRequirementsArea.setWrapText(true);

        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        switch (userRole) {
            case TA:
                applyButton.setVisible(true);
                applyButton.setDisable(false);
                applyButton.setText("申请职位");
                if (currentUser instanceof TA) {
                    TA ta = (TA) currentUser;
                    List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
                    Application latestApplication = applications.stream()
                            .filter(app -> app.getJobId().equals(currentJob.getId()))
                            .max(java.util.Comparator.comparing(Application::getCreatedAt, java.util.Comparator.nullsLast(String::compareTo)))
                            .orElse(null);
                    if (latestApplication != null) {
                        if (latestApplication.getStatus() == model.ApplicationStatus.WITHDRAWN ||
                                latestApplication.getStatus() == model.ApplicationStatus.REJECTED) {
                            applyButton.setText("重新申请");
                        } else {
                            applyButton.setText("已申请");
                            applyButton.setDisable(true);
                        }
                    }
                    if (ApplicationService.isDeadlinePassed(currentJob.getDeadline())) {
                        applyButton.setText("已截止");
                        applyButton.setDisable(true);
                    }
                    if (ta.getProfileStatus() != ProfileStatus.APPROVED) {
                        applyButton.setText("资料未审核");
                        applyButton.setDisable(true);
                    }
                }
                break;
            case MO:
            case ADMIN:
                applyButton.setVisible(false);
                break;
        }
    }

    @FXML
    private void handleApply() {
        if (userRole != UserRole.TA || !(currentUser instanceof TA)) {
            showAlert("错误", "只有TA可以申请职位", Alert.AlertType.ERROR);
            return;
        }

        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != ProfileStatus.APPROVED) {
            showAlert("提示", "您的资料尚未审核通过，无法申请职位", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认申请");
        confirmAlert.setHeaderText("申请职位: " + currentJob.getTitle());
        confirmAlert.setContentText("确定要申请这个职位吗？");

        TextArea coverLetterArea = new TextArea();
        coverLetterArea.setPromptText("请输入申请信（可选）...");
        coverLetterArea.setPrefRowCount(5);
        coverLetterArea.setWrapText(true);

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.setSpacing(10);
        dialogPaneContent.getChildren().addAll(
            new Label("申请信:"),
            coverLetterArea
        );

        confirmAlert.getDialogPane().setExpandableContent(dialogPaneContent);
        confirmAlert.getDialogPane().setExpanded(true);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String coverLetter = coverLetterArea.getText();
                if (coverLetter == null || coverLetter.trim().isEmpty()) {
                    coverLetter = "我对这个职位非常感兴趣，希望能有机会加入。";
                }

                Application application = ApplicationService.submitApplication(ta.getId(), currentJob.getId(), coverLetter);
                if (application != null) {
                    showAlert("申请成功", "您的申请已提交！匹配度得分: " + String.format("%.2f%%", application.getMatchScore()), Alert.AlertType.INFORMATION);
                    applyButton.setText("已申请");
                    applyButton.setDisable(true);
                } else {
                    showAlert("申请失败", "申请提交失败，当前职位可能仍有未撤回/未处理申请，或已过截止时间。", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(currentUser, userRole);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 职位需求");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "返回失败: " + e.getMessage(), Alert.AlertType.ERROR);
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
            showAlert("错误", "退出失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
