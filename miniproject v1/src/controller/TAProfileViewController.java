package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.ProfileStatus;
import model.TA;

public class TAProfileViewController {
    @FXML
    private Label avatarLabel;
    @FXML
    private Label displayNameLabel;
    @FXML
    private Label headlineLabel;
    @FXML
    private Label accountStatusLabel;
    @FXML
    private Label profileStatusBadgeLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label resumeStatusLabel;
    @FXML
    private Label profileUpdatedAtLabel;
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
    @FXML
    private Label profileStatusLabel;
    @FXML
    private Label profileFeedbackLabel;

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
        if (user == null) {
            return;
        }

        String displayName = firstNonEmpty(user.getName(), user.getUsername(), "TA Applicant");
        displayNameLabel.setText(displayName);
        avatarLabel.setText(buildAvatarText(displayName));
        headlineLabel.setText(buildHeadline(user));
        accountStatusLabel.setText(user.getStatus() != null ? user.getStatus().name() : "UNKNOWN");
        profileStatusBadgeLabel.setText(user.getProfileStatus() != null ? user.getProfileStatus().name() : "UNKNOWN");

        usernameLabel.setText(firstNonEmpty(user.getUsername(), "未填写"));
        emailLabel.setText(firstNonEmpty(user.getEmail(), "未填写"));
        phoneLabel.setText(firstNonEmpty(user.getPhone(), "未填写"));
        resumeStatusLabel.setText(hasResume() ? "已上传简历" : "尚未上传简历");
        profileUpdatedAtLabel.setText(formatDateTime(user.getProfileUpdatedAt()));

        nameLabel.setText(firstNonEmpty(user.getName(), "未填写"));
        genderLabel.setText(firstNonEmpty(user.getGender(), "未填写"));
        ageLabel.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "未填写");
        departmentLabel.setText(firstNonEmpty(user.getDepartment(), "未填写"));
        gradeLabel.setText(firstNonEmpty(user.getGrade(), "未填写"));
        studentIdLabel.setText(firstNonEmpty(user.getStudentId(), "未填写"));
        availableTimeLabel.setText(firstNonEmpty(user.getAvailableTime(), "未填写"));
        skillsLabel.setText(formatSkills());
        experienceLabel.setText(firstNonEmpty(user.getExperience(), "未填写"));
        awardsLabel.setText(firstNonEmpty(user.getAwards(), "未填写"));
        languageSkillsLabel.setText(firstNonEmpty(user.getLanguageSkills(), "未填写"));
        otherSkillsLabel.setText(firstNonEmpty(user.getOtherSkills(), "未填写"));
        profileStatusLabel.setText(user.getProfileStatus() != null ? user.getProfileStatus().name() : "-");
        profileFeedbackLabel.setText(firstNonEmpty(user.getProfileReviewComment(), "暂无审核反馈"));
    }

    private String buildAvatarText(String displayName) {
        String normalized = displayName == null ? "TA" : displayName.trim();
        if (normalized.isEmpty()) {
            return "TA";
        }
        return normalized.length() >= 2 ? normalized.substring(0, 2).toUpperCase() : normalized.toUpperCase();
    }

    private String buildHeadline(TA ta) {
        ProfileStatus status = ta.getProfileStatus();
        if (status == ProfileStatus.APPROVED) {
            return "档案已审核通过，可以继续浏览职位并提交申请。";
        }
        if (status == ProfileStatus.REJECTED) {
            return "档案审核未通过，请根据反馈修改资料后重新提交。";
        }
        if (status == ProfileStatus.PENDING) {
            return "档案审核中，请耐心等待管理员处理。";
        }
        return hasResume() ? "资料已保存，建议继续完善信息并等待审核。" : "建议先补充资料并上传简历，方便尽快进入审核。";
    }

    private boolean hasResume() {
        return user != null && user.getResumePath() != null && !user.getResumePath().trim().isEmpty();
    }

    private String formatSkills() {
        if (user.getSkills() == null || user.getSkills().isEmpty()) {
            return "未填写";
        }
        return String.join("、", user.getSkills());
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty() && !"null".equalsIgnoreCase(value.trim())) {
                return value.trim();
            }
        }
        return "";
    }

    private String formatDateTime(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "暂无记录";
        }
        return value.replace('T', ' ');
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);

            Stage currentStage = event.getSource() instanceof Button
                    ? (Stage) ((Button) event.getSource()).getScene().getWindow()
                    : stage;

            controller.setStage(currentStage);
            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Update Profile");
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "错误", "打开资料编辑页面失败: " + e.getMessage());
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
            controller.setUser(user, false);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "错误", "返回首页失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user, false);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
            controller.handleMyApplicationsClick(null);
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "错误", "打开申请管理失败: " + e.getMessage());
        }
    }

    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        loadUserData();
    }

    @FXML
    private void handleViewResume(ActionEvent event) {
        if (!hasResume()) {
            showSimpleAlert(Alert.AlertType.INFORMATION, "提示", "你还没有上传简历。请先在资料编辑页上传。");
            return;
        }

        try {
            java.io.File resumeFile = new java.io.File(user.getResumePath());
            if (resumeFile.exists()) {
                java.awt.Desktop.getDesktop().open(resumeFile);
                showSimpleAlert(Alert.AlertType.INFORMATION, "提示", "正在打开简历文件...");
            } else {
                showSimpleAlert(Alert.AlertType.WARNING, "提示", "简历文件不存在，请重新上传。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "错误", "打开简历失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();

            Stage currentStage = event.getSource() instanceof Button
                    ? (Stage) ((Button) event.getSource()).getScene().getWindow()
                    : stage;

            controller.setStage(currentStage);
            Scene scene = new Scene(root, 800, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "错误", "退出失败: " + e.getMessage());
        }
    }

    private void showSimpleAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
