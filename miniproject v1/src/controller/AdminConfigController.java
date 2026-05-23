package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Admin;
import model.AdminConfig;
import service.AdminConfigService;

// 管理员配置页面控制器：负责读取、显示和保存系统配置参数。
public class AdminConfigController {
    // FXML 注入的页面控件，对应 AdminConfig.fxml 中的输入框和勾选框。
    @FXML private TextField lowThresholdField;
    @FXML private TextField midThresholdField;
    @FXML private TextField highThresholdField;
    @FXML private TextField maxThresholdField;
    @FXML private CheckBox autoReportCheck;
    @FXML private TextField reportHourField;
    @FXML private TextField skillWeightField;
    @FXML private TextField availabilityWeightField;
    @FXML private TextField historyWeightField;

    private Admin user;
    private Stage stage;

    // 设置当前登录的管理员用户，并在进入页面时加载已有配置。
    public void setUser(Admin user) {
        this.user = user;
        loadData();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // 从配置文件读取数据，并显示到页面输入框中。
    private void loadData() {
        AdminConfig c = AdminConfigService.loadConfig();
        lowThresholdField.setText(String.valueOf(c.getLowThreshold()));
        midThresholdField.setText(String.valueOf(c.getMidThreshold()));
        highThresholdField.setText(String.valueOf(c.getHighThreshold()));
        maxThresholdField.setText(String.valueOf(c.getMaxThreshold()));
        autoReportCheck.setSelected(c.isAutoReportEnabled());
        reportHourField.setText(String.valueOf(c.getReportHour()));
        skillWeightField.setText(String.valueOf(c.getAiSkillWeight()));
        availabilityWeightField.setText(String.valueOf(c.getAiAvailabilityWeight()));
        historyWeightField.setText(String.valueOf(c.getAiHistoryWeight()));
    }

    // 点击 Save 时，将页面中的阈值、报告设置和 AI 权重保存到配置文件。
    @FXML
    private void handleSave() {
        AdminConfig c = new AdminConfig();
        c.setLowThreshold(parseInt(lowThresholdField.getText(), 0));
        c.setMidThreshold(parseInt(midThresholdField.getText(), 8));
        c.setHighThreshold(parseInt(highThresholdField.getText(), 10));
        c.setMaxThreshold(parseInt(maxThresholdField.getText(), 12));
        c.setAutoReportEnabled(autoReportCheck.isSelected());
        c.setReportHour(parseInt(reportHourField.getText(), 2));
        c.setAiSkillWeight(parseDouble(skillWeightField.getText(), 0.5));
        c.setAiAvailabilityWeight(parseDouble(availabilityWeightField.getText(), 0.3));
        c.setAiHistoryWeight(parseDouble(historyWeightField.getText(), 0.2));
        AdminConfigService.saveConfig(c);
    }

    // 返回管理员首页，并把当前管理员和窗口对象传回 Dashboard 控制器。
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 简单的输入容错：如果用户输入无法转换，就使用默认值，避免程序崩溃。
    private int parseInt(String text, int def) {
        try { return Integer.parseInt(text.trim()); } catch (Exception e) { return def; }
    }

    private double parseDouble(String text, double def) {
        try { return Double.parseDouble(text.trim()); } catch (Exception e) { return def; }
    }
}
