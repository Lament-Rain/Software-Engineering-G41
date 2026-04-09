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
    }
    
    @FXML
    private void handleBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择简历文件");
        
        // 设置文件过滤器
        FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter("PDF文件 (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter extFilterWord = new FileChooser.ExtensionFilter("Word文件 (*.doc, *.docx)", "*.doc", "*.docx");
        FileChooser.ExtensionFilter extFilterImage = new FileChooser.ExtensionFilter("图片文件 (*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilterPDF, extFilterWord, extFilterImage);
        
        // 显示文件选择对话框
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleUpload(ActionEvent event) {
        if (selectedFile == null) {
            messageLabel.setText("请先选择文件");
            return;
        }
        
        if (user == null) {
            messageLabel.setText("用户信息不存在");
            return;
        }
        
        // 检查文件大小
        if (!FileUtils.checkFileSize(selectedFile)) {
            messageLabel.setText("文件大小超过10MB限制");
            return;
        }
        
        // 检查文件格式
        if (!FileUtils.checkFileFormat(selectedFile.getName())) {
            messageLabel.setText("文件格式不支持，仅支持PDF、Word、JPG/PNG");
            return;
        }
        
        // 显示进度条
        progressBar.setVisible(true);
        progressBar.setProgress(0.5);
        
        try {
            // 上传文件
            String resumePath = FileUtils.uploadResume(selectedFile, user.getId());
            user.setResumePath(resumePath);
            
            // 更新用户信息
            if (UserService.updateTAProfile(user)) {
                messageLabel.setText("简历上传成功！");
                progressBar.setProgress(1.0);
            } else {
                messageLabel.setText("简历上传失败");
                progressBar.setProgress(0);
                progressBar.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("简历上传失败：" + e.getMessage());
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
            messageLabel.setText("页面加载失败");
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
            messageLabel.setText("页面加载失败");
        }
    }
}