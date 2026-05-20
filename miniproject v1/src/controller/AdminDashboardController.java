package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Admin;
import model.SystemStatus;
import service.ApplicationService;
import service.JobService;
import service.UserService;
import service.AIService;
<<<<<<< Updated upstream
=======
import service.KeyboardShortcutService;
import service.ToastService;
import service.NavigationHistory;
import controller.SearchBarController;
>>>>>>> Stashed changes
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminDashboardController {
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalJobsLabel;
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private TableView<SystemStatus> systemStatusTable;
    
    private Admin user;
    
    public void setUser(Admin user) {
        this.user = user;
        initializeDashboard();
    }
    
    private void initializeDashboard() {
        if (user == null) return;
        
        try {
            // Load statistics
            loadStatistics();
            
            // Load system status
            loadSystemStatus();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to initialize dashboard");
            alert.setContentText("Failed to initialize the dashboard: " + e.getMessage());
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    private void loadStatistics() {
        // Get total number of users
        int totalUsersCount = UserService.getAllUsers().size();
        totalUsersLabel.setText(String.valueOf(totalUsersCount));
        
        // Get total number of jobs
        int totalJobsCount = JobService.getAllJobs().size();
        totalJobsLabel.setText(String.valueOf(totalJobsCount));
        
        // Get total number of applications
        int totalApplicationsCount = ApplicationService.getAllApplications().size();
        totalApplicationsLabel.setText(String.valueOf(totalApplicationsCount));
    }
    
    private void loadSystemStatus() {
        // Get system status from database or services
        // Use mock data for demonstration
        ObservableList<SystemStatus> statusItems = FXCollections.observableArrayList();
        
        // Mock system status data
        statusItems.add(new SystemStatus("Database Connection", "Active", "Connection is normal"));
        statusItems.add(new SystemStatus("Service Status", "Active", "All services are running normally"));
        statusItems.add(new SystemStatus("Disk Space", "Active", "Available space: 85%"));
        statusItems.add(new SystemStatus("Backup Status", "Active", "Last backup: 2026-03-20 02:00:00"));
        
        // Set table data
        systemStatusTable.setItems(statusItems);
    }
    
    // Admin configuration
    @FXML
    private void handleAdminConfiguration(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminConfig.fxml"));
            Parent root = loader.load();
            AdminConfigController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Admin Configuration");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load admin configuration");
            alert.setContentText("Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Scheduled report generation
    @FXML
    private void handleScheduleReports(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("Scheduled Report Generation");
        alert.setContentText("Scheduled report generation feature is not implemented yet");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // Bulk operations
    @FXML
    private void handleBulkOperations(ActionEvent event) {
        javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(
                "User Enable",
                java.util.List.of(
                        "User Enable",
                        "User Disable",
                        "Job reviewed",
                        "Job available",
                        "Job closed"
                )
        );
        dialog.setTitle("Bulk Operations");
        dialog.setHeaderText("Choose an operation type");
        dialog.setContentText("Operation:");

        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(choice -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Bulk Operations");
            alert.setHeaderText("Selected: " + choice);
            alert.setContentText("Batch flow for '" + choice + "' can be connected here.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        });
    }
    
    // AI workload balancing
    @FXML
    private void handleAIWorkloadBalancing(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminWorkloadOverview.fxml"));
            Parent root = loader.load();
            AdminWorkloadOverviewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Workload Overview");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to open workload overview", ToastService.ToastType.ERROR);
        }
    }
    
    // 大模型API配置
    @FXML
    private void handleModelAPIConfig(ActionEvent event) {
        // 创建API配置对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("大模型API配置");
        dialog.setHeaderText("豆包大模型深度思考能力API配置");
        dialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
        
        // 创建表单
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
        grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        // API密钥输入
        javafx.scene.control.Label apiKeyLabel = new javafx.scene.control.Label("API Key:");
        apiKeyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        javafx.scene.control.TextField apiKeyField = new javafx.scene.control.TextField();
        apiKeyField.setText(AIService.getApiKey());
        apiKeyField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
        
        // 测试输入
        javafx.scene.control.Label testInputLabel = new javafx.scene.control.Label("测试输入:");
        testInputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        javafx.scene.control.TextArea testInputArea = new javafx.scene.control.TextArea();
        testInputArea.setText("常见的十字花科植物有哪些？");
        testInputArea.setPrefHeight(100);
        testInputArea.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
        
        // 测试结果
        javafx.scene.control.Label testResultLabel = new javafx.scene.control.Label("测试结果:");
        testResultLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        javafx.scene.control.TextArea testResultArea = new javafx.scene.control.TextArea();
        testResultArea.setPrefHeight(150);
        testResultArea.setEditable(false);
        testResultArea.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-background-color: #f9f9f9;");
        
        // 测试成功标志
        boolean[] testSuccess = {false};
        
        // 测试按钮
        javafx.scene.control.Button testButton = new javafx.scene.control.Button("测试API");
        testButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
        testButton.setOnMouseEntered(mouseEvent -> testButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        testButton.setOnMouseExited(mouseEvent -> testButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        testButton.setOnAction(testEvent -> {
            String apiKey = apiKeyField.getText().trim();
            String testInput = testInputArea.getText().trim();
            
            if (apiKey.isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText("API Key未设置");
                alert.setContentText("请先输入API Key");
                alert.showAndWait();
                return;
            }
            
            if (testInput.isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText("测试输入为空");
                alert.setContentText("请输入测试问题");
                alert.showAndWait();
                return;
            }
            
            try {
                // 设置API密钥
                AIService.setApiKey(apiKey);
                
                // 调用API
                testResultArea.setText("正在调用API...");
                String response = AIService.callDoubaoDeepThinking(testInput, false);
                String parsedResponse = AIService.parseDoubaoResponse(response);
                testResultArea.setText(parsedResponse);
                
                // 检查是否测试成功
                if (!parsedResponse.contains("API Error") && !parsedResponse.contains("error")) {
                    testSuccess[0] = true;
                    // 显示成功消息
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("成功");
                    successAlert.setHeaderText("API测试成功");
                    successAlert.setContentText("大模型API接入正常");
                    successAlert.showAndWait();
                } else {
                    testSuccess[0] = false;
                    // 显示错误消息
                    javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    errorAlert.setTitle("错误");
                    errorAlert.setHeaderText("API测试失败");
                    errorAlert.setContentText("测试失败，请检查API密钥和网络连接");
                    errorAlert.showAndWait();
                }
                
            } catch (Exception e) {
                testSuccess[0] = false;
                testResultArea.setText("错误: " + e.getMessage());
                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("错误");
                errorAlert.setHeaderText("API测试失败");
                errorAlert.setContentText("测试失败: " + e.getMessage());
                errorAlert.showAndWait();
            }
        });
        
        // 添加到网格
        grid.add(apiKeyLabel, 0, 0);
        grid.add(apiKeyField, 1, 0);
        grid.add(testInputLabel, 0, 1);
        grid.add(testInputArea, 1, 1);
        grid.add(testButton, 1, 2);
        grid.add(testResultLabel, 0, 3);
        grid.add(testResultArea, 1, 3);
        
        // 设置对话框内容
        dialog.getDialogPane().setContent(grid);
        
        // 添加按钮
        javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        
        // 样式按钮
        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
        okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        
        javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
        cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
        
        // 显示对话框并处理结果
        java.util.Optional<Void> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 只有在测试成功时才保存配置
            if (testSuccess[0]) {
                // 保存API密钥
                String apiKey = apiKeyField.getText().trim();
                AIService.setApiKey(apiKey);
                
                // 显示成功消息
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText("API配置保存成功");
                successAlert.setContentText("大模型API配置已保存");
                successAlert.showAndWait();
            } else {
                // 测试失败，不保存配置
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText("API配置未保存");
                alert.setContentText("请先测试API确保配置正确");
                alert.showAndWait();
            }
        }
    }
    
    // Log out
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            
// Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) systemStatusTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("The login page could not be loaded. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle home button click
    @FXML
    private void handleHome(ActionEvent event) {
        // Refresh current page
        initializeDashboard();
    }
    
    // Handle user management button click
    @FXML
    private void handleUserManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminUserManagement.fxml"));
            Parent root = loader.load();
            AdminUserManagementController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - User Management");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to open user management", ToastService.ToastType.ERROR);
        }
    }


    
    // Handle approval center button click
    @FXML
    private void handleApprovalCenter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminApproval.fxml"));
            Parent root = loader.load();
            AdminApprovalController controller = loader.getController();

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            controller.setUser(user);
            controller.setStage(currentStage);

            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Approval Center");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("Failed to load the approval center. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleJobManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminJobManagement.fxml"));
            Parent root = loader.load();
            AdminJobManagementController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Management");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to open job management", ToastService.ToastType.ERROR);
        }
    }



    // 处理系统配置按钮点击
    @FXML
    private void handleSystemConfiguration(ActionEvent event) {
        // 创建系统配置对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("System Configuration");
        dialog.setHeaderText("System Configuration Features");
        
        // Create button layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // Add feature buttons
        javafx.scene.control.Button systemParamsBtn = new javafx.scene.control.Button("System Settings");
        systemParamsBtn.setPrefWidth(200);
        systemParamsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        systemParamsBtn.setOnMouseEntered(mouseEvent -> systemParamsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        systemParamsBtn.setOnMouseExited(mouseEvent -> systemParamsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        systemParamsBtn.setOnAction(e -> {
            // 实现系统参数设置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> systemParamsDialog = new javafx.scene.control.Dialog<>();
            systemParamsDialog.setTitle("System Settings");
            systemParamsDialog.setHeaderText("System Settings");
            systemParamsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create form
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Add form elements
            javafx.scene.control.Label maxUsersLabel = new javafx.scene.control.Label("Maximum Users:");
            maxUsersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField maxUsersField = new javafx.scene.control.TextField("1000");
            maxUsersField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label maxJobsLabel = new javafx.scene.control.Label("Maximum Jobs:");
            maxJobsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField maxJobsField = new javafx.scene.control.TextField("500");
            maxJobsField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label sessionTimeoutLabel = new javafx.scene.control.Label("Session Timeout (minutes):");
            sessionTimeoutLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField sessionTimeoutField = new javafx.scene.control.TextField("30");
            sessionTimeoutField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Add to grid
            grid.add(maxUsersLabel, 0, 0);
            grid.add(maxUsersField, 1, 0);
            grid.add(maxJobsLabel, 0, 1);
            grid.add(maxJobsField, 1, 1);
            grid.add(sessionTimeoutLabel, 0, 2);
            grid.add(sessionTimeoutField, 1, 2);
            
            // Set dialog content
            systemParamsDialog.getDialogPane().setContent(grid);
            
            // Add buttons
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            systemParamsDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // Style buttons
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) systemParamsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) systemParamsDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Set result converter
            systemParamsDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    java.util.Map<String, Object> result = new java.util.HashMap<>();
                    result.put("maxUsers", maxUsersField.getText());
                    result.put("maxJobs", maxJobsField.getText());
                    result.put("sessionTimeout", sessionTimeoutField.getText());
                    return result;
                }
                return null;
            });
            
            // Show dialog and handle result
            java.util.Optional<java.util.Map<String, Object>> result = systemParamsDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存系统参数
                // This is only a simulated implementation; in a real project it should be saved to a config file or database
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Settings updated successfully");
                successAlert.setContentText("System settings have been updated");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button emailConfigBtn = new javafx.scene.control.Button("Email Server Configuration");
        emailConfigBtn.setPrefWidth(200);
        emailConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        emailConfigBtn.setOnMouseEntered(mouseEvent -> emailConfigBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        emailConfigBtn.setOnMouseExited(mouseEvent -> emailConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        emailConfigBtn.setOnAction(e -> {
            // 实现邮件服务器配置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> emailConfigDialog = new javafx.scene.control.Dialog<>();
            emailConfigDialog.setTitle("Email Server Configuration");
            emailConfigDialog.setHeaderText("Email Server Configuration");
            emailConfigDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create form
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Add form elements
            javafx.scene.control.Label smtpServerLabel = new javafx.scene.control.Label("SMTP Server:");
            smtpServerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField smtpServerField = new javafx.scene.control.TextField("smtp.example.com");
            smtpServerField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label smtpPortLabel = new javafx.scene.control.Label("SMTP Port:");
            smtpPortLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField smtpPortField = new javafx.scene.control.TextField("587");
            smtpPortField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailUsernameLabel = new javafx.scene.control.Label("Email Username:");
            emailUsernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField emailUsernameField = new javafx.scene.control.TextField("admin@example.com");
            emailUsernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailPasswordLabel = new javafx.scene.control.Label("Email Password:");
            emailPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.PasswordField emailPasswordField = new javafx.scene.control.PasswordField();
            emailPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Add to grid
            grid.add(smtpServerLabel, 0, 0);
            grid.add(smtpServerField, 1, 0);
            grid.add(smtpPortLabel, 0, 1);
            grid.add(smtpPortField, 1, 1);
            grid.add(emailUsernameLabel, 0, 2);
            grid.add(emailUsernameField, 1, 2);
            grid.add(emailPasswordLabel, 0, 3);
            grid.add(emailPasswordField, 1, 3);
            
            // Set dialog content
            emailConfigDialog.getDialogPane().setContent(grid);
            
            // Add buttons
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            emailConfigDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // Style buttons
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) emailConfigDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) emailConfigDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Set result converter
            emailConfigDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    java.util.Map<String, Object> result = new java.util.HashMap<>();
                    result.put("smtpServer", smtpServerField.getText());
                    result.put("smtpPort", smtpPortField.getText());
                    result.put("emailUsername", emailUsernameField.getText());
                    result.put("emailPassword", emailPasswordField.getText());
                    return result;
                }
                return null;
            });
            
            // Show dialog and handle result
            java.util.Optional<java.util.Map<String, Object>> result = emailConfigDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存邮件服务器配置
                // This is only a simulated implementation; in a real project it should be saved to a config file or database
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Email configuration updated successfully");
                successAlert.setContentText("Email server settings have been updated");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button backupConfigBtn = new javafx.scene.control.Button("Backup Settings");
        backupConfigBtn.setPrefWidth(200);
        backupConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        backupConfigBtn.setOnMouseEntered(mouseEvent -> backupConfigBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        backupConfigBtn.setOnMouseExited(mouseEvent -> backupConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        backupConfigBtn.setOnAction(e -> {
            // 实现数据备份设置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> backupConfigDialog = new javafx.scene.control.Dialog<>();
            backupConfigDialog.setTitle("Backup Settings");
            backupConfigDialog.setHeaderText("Backup Settings");
            backupConfigDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create form
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Add form elements
            javafx.scene.control.Label backupPathLabel = new javafx.scene.control.Label("Backup Path:");
            backupPathLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupPathField = new javafx.scene.control.TextField("d:/backup");
            backupPathField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label backupIntervalLabel = new javafx.scene.control.Label("Backup Interval (hours):");
            backupIntervalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupIntervalField = new javafx.scene.control.TextField("24");
            backupIntervalField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label backupRetentionLabel = new javafx.scene.control.Label("Backup Retention (days):");
            backupRetentionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupRetentionField = new javafx.scene.control.TextField("7");
            backupRetentionField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Add to grid
            grid.add(backupPathLabel, 0, 0);
            grid.add(backupPathField, 1, 0);
            grid.add(backupIntervalLabel, 0, 1);
            grid.add(backupIntervalField, 1, 1);
            grid.add(backupRetentionLabel, 0, 2);
            grid.add(backupRetentionField, 1, 2);
            
            // Set dialog content
            backupConfigDialog.getDialogPane().setContent(grid);
            
            // Add buttons
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            backupConfigDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // Style buttons
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) backupConfigDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) backupConfigDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Set result converter
            backupConfigDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    java.util.Map<String, Object> result = new java.util.HashMap<>();
                    result.put("backupPath", backupPathField.getText());
                    result.put("backupInterval", backupIntervalField.getText());
                    result.put("backupRetention", backupRetentionField.getText());
                    return result;
                }
                return null;
            });
            
            // Show dialog and handle result
            java.util.Optional<java.util.Map<String, Object>> result = backupConfigDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存数据备份设置
                // This is only a simulated implementation; in a real project it should be saved to a config file or database
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Backup settings updated successfully");
                successAlert.setContentText("Backup settings have been updated");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button logManagementBtn = new javafx.scene.control.Button("Log Management");
        logManagementBtn.setPrefWidth(200);
        logManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        logManagementBtn.setOnMouseEntered(mouseEvent -> logManagementBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        logManagementBtn.setOnMouseExited(mouseEvent -> logManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        logManagementBtn.setOnAction(e -> {
            // 实现日志管理功能
            javafx.scene.control.Dialog<Void> logManagementDialog = new javafx.scene.control.Dialog<>();
            logManagementDialog.setTitle("Log Management");
            logManagementDialog.setHeaderText("System Logs");
            logManagementDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建文本区域显示日志
            javafx.scene.control.TextArea logTextArea = new javafx.scene.control.TextArea();
            logTextArea.setEditable(false);
            logTextArea.setPrefHeight(400);
            logTextArea.setPrefWidth(600);
            logTextArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 加载日志内容
            try {
                java.nio.file.Path logPath = java.nio.file.Paths.get("d:\\Soft Engineering\\miniproject(version1)\\src\\data\\logs.txt");
                if (java.nio.file.Files.exists(logPath)) {
                    String logContent = new String(java.nio.file.Files.readAllBytes(logPath));
                    logTextArea.setText(logContent);
                } else {
                    logTextArea.setText("Log file does not exist");
                }
            } catch (Exception ex) {
                logTextArea.setText("Failed to load logs: " + ex.getMessage());
            }
            
            // Create scroll pane
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(logTextArea);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 为清空日志按钮添加事件
            javafx.scene.control.Button clearLogBtn = new javafx.scene.control.Button("Clear Logs");
            clearLogBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            clearLogBtn.setOnMouseEntered(mouseEvent -> clearLogBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            clearLogBtn.setOnMouseExited(mouseEvent -> clearLogBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            clearLogBtn.setOnAction(clearEvent -> {
                javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Clear");
                confirmAlert.setHeaderText("Clear Logs");
                confirmAlert.setContentText("Are you sure you want to clear all logs?");
                
                java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    try {
                        java.nio.file.Path logPath = java.nio.file.Paths.get("d:\\Soft Engineering\\miniproject(version1)\\src\\data\\logs.txt");
                        java.nio.file.Files.write(logPath, new byte[0]);
                        logTextArea.setText("");
                        
                        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Logs cleared successfully");
                        successAlert.setContentText("Logs have been cleared");
                        successAlert.showAndWait();
                    } catch (Exception ex) {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to clear logs");
                        errorAlert.setContentText("Failed to clear logs: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
            
            // 创建VBox包含滚动面板和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(scrollPane, clearLogBtn);
            
            // Set dialog content
            logManagementDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            logManagementDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) logManagementDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            logManagementDialog.showAndWait();
        });
        
        javafx.scene.control.Button permissionManagementBtn = new javafx.scene.control.Button("Role Management");
        permissionManagementBtn.setPrefWidth(200);
        permissionManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        permissionManagementBtn.setOnMouseEntered(mouseEvent -> permissionManagementBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        permissionManagementBtn.setOnMouseExited(mouseEvent -> permissionManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        permissionManagementBtn.setOnAction(e -> {
            // 实现权限管理功能
            javafx.scene.control.Dialog<Void> permissionManagementDialog = new javafx.scene.control.Dialog<>();
            permissionManagementDialog.setTitle("Role Management");
            permissionManagementDialog.setHeaderText("User Role Settings");
            permissionManagementDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("Username");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserRole> roleCol = new javafx.scene.control.TableColumn<>("Role");
            roleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("role"));
            roleCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            userTable.getColumns().addAll(usernameCol, roleCol);
            
            // Load user data
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // Set table to resizable columns
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建修改权限按钮
            javafx.scene.control.Button changePermissionBtn = new javafx.scene.control.Button("Change Selected User Role");
            changePermissionBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            changePermissionBtn.setOnMouseEntered(mouseEvent -> changePermissionBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            changePermissionBtn.setOnMouseExited(mouseEvent -> changePermissionBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            changePermissionBtn.setOnAction(changeEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // 创建权限修改表单
                    javafx.scene.control.Dialog<model.UserRole> permissionFormDialog = new javafx.scene.control.Dialog<>();
                    permissionFormDialog.setTitle("Change Permission");
                    permissionFormDialog.setHeaderText("Change role for: " + selectedUser.getUsername());
                    permissionFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // Create form
                    javafx.scene.layout.VBox vbox1 = new javafx.scene.layout.VBox(15);
                    vbox1.setPadding(new javafx.geometry.Insets(20));
                    vbox1.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加权限选项
                    javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("New Role:");
                    roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.ComboBox<model.UserRole> roleComboBox = new javafx.scene.control.ComboBox<>();
                    roleComboBox.getItems().addAll(model.UserRole.TA, model.UserRole.MO, model.UserRole.ADMIN);
                    roleComboBox.setValue(selectedUser.getRole());
                    roleComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // Add to VBox
                    vbox1.getChildren().addAll(roleLabel, roleComboBox);
                    
                    // Set dialog content
                    permissionFormDialog.getDialogPane().setContent(vbox1);
                    
                    // Add buttons
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    permissionFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // Style buttons
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) permissionFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) permissionFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // Set result converter
                    permissionFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            return roleComboBox.getValue();
                        }
                        return null;
                    });
                    
                    // Show dialog and handle result
                    java.util.Optional<model.UserRole> result = permissionFormDialog.showAndWait();
                    result.ifPresent(role -> {
                        selectedUser.setRole(role);
                        boolean success = service.UserService.updateUser(selectedUser);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("Role updated successfully");
                            successAlert.setContentText("User role has been updated successfully");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Failed to update role");
                            errorAlert.setContentText("Failed to update user role");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No user selected");
                    alert.setContentText("Please select a user to change permissions first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, changePermissionBtn);
            
            // Set dialog content
            permissionManagementDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            permissionManagementDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            permissionManagementDialog.showAndWait();
        });
        
        // Add buttons to VBox
        vbox.getChildren().addAll(systemParamsBtn, emailConfigBtn, backupConfigBtn, logManagementBtn, permissionManagementBtn);
        
        // Set dialog content
        dialog.getDialogPane().setContent(vbox);
        
        // Add confirm button
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        
        // Show dialog
        dialog.showAndWait();
    }
    
    // 处理统计分析按钮点击
    @FXML
    private void handleStatisticsAnalysis(ActionEvent event) {
        // 创建统计分析对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("统计分析");
        dialog.setHeaderText("统计分析功能");
        
        // Create button layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // Add feature buttons
        javafx.scene.control.Button userStatsBtn = new javafx.scene.control.Button("User Statistics");
        userStatsBtn.setPrefWidth(200);
        userStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        userStatsBtn.setOnMouseEntered(mouseEvent -> userStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        userStatsBtn.setOnMouseExited(mouseEvent -> userStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        userStatsBtn.setOnAction(e -> {
            // 实现用户统计功能
            javafx.scene.control.Dialog<Void> userStatsDialog = new javafx.scene.control.Dialog<>();
            userStatsDialog.setTitle("User Statistics");
            userStatsDialog.setHeaderText("User Statistics Data");
            userStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create statistics content
            javafx.scene.layout.VBox statsContent = new javafx.scene.layout.VBox(15);
            statsContent.setPadding(new javafx.geometry.Insets(20));
            statsContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 获取用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            int totalUsers = users.size();
            int taCount = 0;
            int moCount = 0;
            int adminCount = 0;
            int activeCount = 0;
            int lockedCount = 0;
            
            for (model.User user : users) {
                if (user.getRole() == model.UserRole.TA) taCount++;
                if (user.getRole() == model.UserRole.MO) moCount++;
                if (user.getRole() == model.UserRole.ADMIN) adminCount++;
                if (user.getStatus() == model.UserStatus.ACTIVE) activeCount++;
                if (user.getStatus() == model.UserStatus.LOCKED) lockedCount++;
            }
            
            // Display statistics
            javafx.scene.control.Label totalUsersLabel = new javafx.scene.control.Label("Total Users: " + totalUsers);
            totalUsersLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalUsersLabel);
            
            javafx.scene.control.Label taCountLabel = new javafx.scene.control.Label("TA Users: " + taCount);
            taCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(taCountLabel);
            
            javafx.scene.control.Label moCountLabel = new javafx.scene.control.Label("MO Users: " + moCount);
            moCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(moCountLabel);
            
            javafx.scene.control.Label adminCountLabel = new javafx.scene.control.Label("Admin Users: " + adminCount);
            adminCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(adminCountLabel);
            
            javafx.scene.control.Label activeCountLabel = new javafx.scene.control.Label("Active Users: " + activeCount);
            activeCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(activeCountLabel);
            
            javafx.scene.control.Label lockedCountLabel = new javafx.scene.control.Label("Locked Users: " + lockedCount);
            lockedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(lockedCountLabel);
            
            // Set dialog content
            userStatsDialog.getDialogPane().setContent(statsContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            userStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) userStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            userStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button jobStatsBtn = new javafx.scene.control.Button("Job Statistics");
        jobStatsBtn.setPrefWidth(200);
        jobStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        jobStatsBtn.setOnMouseEntered(mouseEvent -> jobStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        jobStatsBtn.setOnMouseExited(mouseEvent -> jobStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        jobStatsBtn.setOnAction(e -> {
            // 实现职位统计功能
            javafx.scene.control.Dialog<Void> jobStatsDialog = new javafx.scene.control.Dialog<>();
            jobStatsDialog.setTitle("Job Statistics");
            jobStatsDialog.setHeaderText("Job Statistics Data");
            jobStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create statistics content
            javafx.scene.layout.VBox statsContent = new javafx.scene.layout.VBox(15);
            statsContent.setPadding(new javafx.geometry.Insets(20));
            statsContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 获取职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            int totalJobs = jobs.size();
            int publishedCount = 0;
            int pendingCount = 0;
            int closedCount = 0;
            int rejectedCount = 0;
            
            for (model.Job job : jobs) {
                if (job.getStatus() == model.JobStatus.PUBLISHED) publishedCount++;
                if (job.getStatus() == model.JobStatus.PENDING) pendingCount++;
                if (job.getStatus() == model.JobStatus.CLOSED) closedCount++;
                if (job.getStatus() == model.JobStatus.REJECTED) rejectedCount++;
            }
            
            // Display statistics
            javafx.scene.control.Label totalJobsLabel = new javafx.scene.control.Label("Total Jobs: " + totalJobs);
            totalJobsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalJobsLabel);
            
            javafx.scene.control.Label publishedCountLabel = new javafx.scene.control.Label("Published Jobs: " + publishedCount);
            publishedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(publishedCountLabel);
            
            javafx.scene.control.Label pendingCountLabel = new javafx.scene.control.Label("Pending Review Jobs: " + pendingCount);
            pendingCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(pendingCountLabel);
            
            javafx.scene.control.Label closedCountLabel = new javafx.scene.control.Label("Closed Jobs: " + closedCount);
            closedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(closedCountLabel);
            
            javafx.scene.control.Label rejectedCountLabel = new javafx.scene.control.Label("Rejected Jobs: " + rejectedCount);
            rejectedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(rejectedCountLabel);
            
            // Set dialog content
            jobStatsDialog.getDialogPane().setContent(statsContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            jobStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) jobStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            jobStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button applicationStatsBtn = new javafx.scene.control.Button("Application Statistics");
        applicationStatsBtn.setPrefWidth(200);
        applicationStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        applicationStatsBtn.setOnMouseEntered(mouseEvent -> applicationStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        applicationStatsBtn.setOnMouseExited(mouseEvent -> applicationStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        applicationStatsBtn.setOnAction(e -> {
            // 实现申请统计功能
            javafx.scene.control.Dialog<Void> applicationStatsDialog = new javafx.scene.control.Dialog<>();
            applicationStatsDialog.setTitle("Application Statistics");
            applicationStatsDialog.setHeaderText("Application Statistics Data");
            applicationStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create statistics content
            javafx.scene.layout.VBox statsContent = new javafx.scene.layout.VBox(15);
            statsContent.setPadding(new javafx.geometry.Insets(20));
            statsContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 获取申请数据
            java.util.List<model.Application> applications = service.ApplicationService.getAllApplications();
            int totalApplications = applications.size();
            int pendingCount = 0;
            int acceptedCount = 0;
            int rejectedCount = 0;
            
            for (model.Application application : applications) {
                if (application.getStatus() == model.ApplicationStatus.PENDING) pendingCount++;
                if (application.getStatus() == model.ApplicationStatus.ACCEPTED) acceptedCount++;
                if (application.getStatus() == model.ApplicationStatus.REJECTED) rejectedCount++;
            }
            
            // Display statistics
            javafx.scene.control.Label totalApplicationsLabel = new javafx.scene.control.Label("Total Applications: " + totalApplications);
            totalApplicationsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalApplicationsLabel);
            
            javafx.scene.control.Label pendingCountLabel = new javafx.scene.control.Label("Pending Applications: " + pendingCount);
            pendingCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(pendingCountLabel);
            
            javafx.scene.control.Label acceptedCountLabel = new javafx.scene.control.Label("Accepted Applications: " + acceptedCount);
            acceptedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(acceptedCountLabel);
            
            javafx.scene.control.Label rejectedCountLabel = new javafx.scene.control.Label("Rejected Applications: " + rejectedCount);
            rejectedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(rejectedCountLabel);
            
            // Set dialog content
            applicationStatsDialog.getDialogPane().setContent(statsContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            applicationStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) applicationStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            applicationStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button recruitmentStatsBtn = new javafx.scene.control.Button("Recruitment Analytics");
        recruitmentStatsBtn.setPrefWidth(200);
        recruitmentStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        recruitmentStatsBtn.setOnMouseEntered(mouseEvent -> recruitmentStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        recruitmentStatsBtn.setOnMouseExited(mouseEvent -> recruitmentStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        recruitmentStatsBtn.setOnAction(e -> {
            // Implement recruitment effectiveness analysis feature
            javafx.scene.control.Dialog<Void> recruitmentStatsDialog = new javafx.scene.control.Dialog<>();
            recruitmentStatsDialog.setTitle("Recruitment Analytics");
            recruitmentStatsDialog.setHeaderText("Recruitment Analytics");
            recruitmentStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create statistics content
            javafx.scene.layout.VBox statsContent = new javafx.scene.layout.VBox(15);
            statsContent.setPadding(new javafx.geometry.Insets(20));
            statsContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Get data
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            java.util.List<model.Application> applications = service.ApplicationService.getAllApplications();
            
            // 计算平均每个职位的申请数
            double avgApplicationsPerJob = jobs.size() > 0 ? (double) applications.size() / jobs.size() : 0;
            
            // 计算申请通过率
            int acceptedApplications = 0;
            for (model.Application application : applications) {
                if (application.getStatus() == model.ApplicationStatus.ACCEPTED) {
                    acceptedApplications++;
                }
            }
            double acceptanceRate = applications.size() > 0 ? (double) acceptedApplications / applications.size() * 100 : 0;
            
            // Display statistics
            javafx.scene.control.Label avgApplicationsLabel = new javafx.scene.control.Label("Average applications per job: " + String.format("%.2f", avgApplicationsPerJob));
            avgApplicationsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(avgApplicationsLabel);
            
            javafx.scene.control.Label acceptanceRateLabel = new javafx.scene.control.Label("Acceptance Rate: " + String.format("%.2f%%", acceptanceRate));
            acceptanceRateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(acceptanceRateLabel);
            
            // Set dialog content
            recruitmentStatsDialog.getDialogPane().setContent(statsContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            recruitmentStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) recruitmentStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            recruitmentStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button dataExportBtn = new javafx.scene.control.Button("Data Export");
        dataExportBtn.setPrefWidth(200);
        dataExportBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        dataExportBtn.setOnMouseEntered(mouseEvent -> dataExportBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        dataExportBtn.setOnMouseExited(mouseEvent -> dataExportBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        dataExportBtn.setOnAction(e -> {
            // Implement data export feature
            javafx.scene.control.Dialog<Void> dataExportDialog = new javafx.scene.control.Dialog<>();
            dataExportDialog.setTitle("Data Export");
            dataExportDialog.setHeaderText("Export Data");
            dataExportDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create export options
            javafx.scene.layout.VBox exportContent = new javafx.scene.layout.VBox(15);
            exportContent.setPadding(new javafx.geometry.Insets(20));
            exportContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Add export options
            javafx.scene.control.CheckBox usersCheckBox = new javafx.scene.control.CheckBox("Export User Data");
            usersCheckBox.setStyle("-fx-font-size: 14px;");
            
            javafx.scene.control.CheckBox jobsCheckBox = new javafx.scene.control.CheckBox("Export Job Data");
            jobsCheckBox.setStyle("-fx-font-size: 14px;");
            
            javafx.scene.control.CheckBox applicationsCheckBox = new javafx.scene.control.CheckBox("Export Application Data");
            applicationsCheckBox.setStyle("-fx-font-size: 14px;");
            
            // Add to VBox
            exportContent.getChildren().addAll(usersCheckBox, jobsCheckBox, applicationsCheckBox);
            
            // Create export button
            javafx.scene.control.Button exportBtn = new javafx.scene.control.Button("Start Export");
            exportBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            exportBtn.setOnMouseEntered(mouseEvent -> exportBtn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            exportBtn.setOnMouseExited(mouseEvent -> exportBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            exportBtn.setOnAction(exportEvent -> {
                // Simulate export process
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Export successful");
                successAlert.setContentText("The data has been exported successfully");
                successAlert.showAndWait();
            });
            
            // 添加导出按钮
            exportContent.getChildren().add(exportBtn);
            
            // Set dialog content
            dataExportDialog.getDialogPane().setContent(exportContent);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dataExportDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) dataExportDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            dataExportDialog.showAndWait();
        });
        
        // Add buttons to VBox
        vbox.getChildren().addAll(userStatsBtn, jobStatsBtn, applicationStatsBtn, recruitmentStatsBtn, dataExportBtn);

        // Set dialog content
        dialog.getDialogPane().setContent(vbox);

        // Add confirm button
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);

        // Show dialog
        dialog.showAndWait();
    }

    // View all job requirements
    @FXML
    private void handleViewAllJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(user, model.UserRole.ADMIN);

// Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) systemStatusTable.getScene().getWindow();
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("Failed to load the job list page. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // Publish new job
    @FXML
    private void handleCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setAdminUser(user);

// Get current stage
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) systemStatusTable.getScene().getWindow();
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Publish Job");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("Failed to load the publish job page. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}