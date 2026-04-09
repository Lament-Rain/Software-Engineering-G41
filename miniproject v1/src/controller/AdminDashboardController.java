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
            // 加载统计数据
            loadStatistics();
            
            // 加载系统状态
            loadSystemStatus();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("初始化仪表板失败");
            alert.setContentText("初始化仪表板失败: " + e.getMessage());
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    private void loadStatistics() {
        // 获取总用户数量
        int totalUsersCount = UserService.getAllUsers().size();
        totalUsersLabel.setText(String.valueOf(totalUsersCount));
        
        // 获取总职位数量
        int totalJobsCount = JobService.getAllJobs().size();
        totalJobsLabel.setText(String.valueOf(totalJobsCount));
        
        // 获取总申请数量
        int totalApplicationsCount = ApplicationService.getAllApplications().size();
        totalApplicationsLabel.setText(String.valueOf(totalApplicationsCount));
    }
    
    private void loadSystemStatus() {
        // 从数据库或服务中获取系统状态
        // 为了演示，我们使用模拟数据
        ObservableList<SystemStatus> statusItems = FXCollections.observableArrayList();
        
        // 模拟系统状态数据
        statusItems.add(new SystemStatus("数据库连接", "Active", "连接正常"));
        statusItems.add(new SystemStatus("服务状态", "Active", "所有服务运行正常"));
        statusItems.add(new SystemStatus("磁盘空间", "Active", "可用空间: 85%"));
        statusItems.add(new SystemStatus("备份状态", "Active", "上次备份: 2026-03-20 02:00:00"));
        
        // 设置表格数据
        systemStatusTable.setItems(statusItems);
    }

    private java.util.List<model.TA> getTaUsers() {
        java.util.List<model.TA> taUsers = new java.util.ArrayList<>();
        for (model.User userItem : service.UserService.getAllUsers()) {
            if (userItem instanceof model.TA) {
                taUsers.add((model.TA) userItem);
            }
        }
        return taUsers;
    }
    
    // 管理员配置
    @FXML
    private void handleAdminConfiguration(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("管理员配置");
        alert.setContentText("管理员配置功能待实现");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // 报告定时生成
    @FXML
    private void handleScheduleReports(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("报告定时生成");
        alert.setContentText("报告定时生成功能待实现");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // 批量操作
    @FXML
    private void handleBulkOperations(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("批量操作");
        alert.setContentText("批量操作功能待实现");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // AI工作量平衡
    @FXML
    private void handleAIWorkloadBalancing(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("AI工作量平衡");
        alert.setContentText("AI工作量平衡功能待实现");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // 退出登录
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            
            // 获取当前舞台
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) systemStatusTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("登录页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // 处理首页按钮点击
    @FXML
    private void handleHome(ActionEvent event) {
        // 刷新当前页面
        initializeDashboard();
    }
    
    // 处理用户管理按钮点击
    @FXML
    private void handleUserManagement(ActionEvent event) {
        // 创建用户管理对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("用户管理");
        dialog.setHeaderText("用户管理功能");
        
        // 创建按钮网格
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // 添加功能按钮
        javafx.scene.control.Button viewUsersBtn = new javafx.scene.control.Button("查看所有用户");
        viewUsersBtn.setPrefWidth(200);
        viewUsersBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        viewUsersBtn.setOnMouseEntered(mouseEvent -> viewUsersBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        viewUsersBtn.setOnMouseExited(mouseEvent -> viewUsersBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        viewUsersBtn.setOnAction(e -> {
            // 实现查看所有用户功能
            javafx.scene.control.Dialog<Void> viewUsersDialog = new javafx.scene.control.Dialog<>();
            viewUsersDialog.setTitle("查看所有用户");
            viewUsersDialog.setHeaderText("所有用户列表");
            viewUsersDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("邮箱");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> phoneCol = new javafx.scene.control.TableColumn<>("电话");
            phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
            phoneCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserRole> roleCol = new javafx.scene.control.TableColumn<>("角色");
            roleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("role"));
            roleCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserStatus> statusCol = new javafx.scene.control.TableColumn<>("状态");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            userTable.getColumns().addAll(usernameCol, emailCol, phoneCol, roleCol, statusCol);
            
            // 加载用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // 设置表格为可调整列宽
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建滚动面板
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(userTable);
            scrollPane.setPrefHeight(400);
            scrollPane.setPrefWidth(600);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 设置对话框内容
            viewUsersDialog.getDialogPane().setContent(scrollPane);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            viewUsersDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) viewUsersDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            viewUsersDialog.showAndWait();
        });
        
        javafx.scene.control.Button addUserBtn = new javafx.scene.control.Button("添加新用户");
        addUserBtn.setPrefWidth(200);
        addUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        addUserBtn.setOnMouseEntered(mouseEvent -> addUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        addUserBtn.setOnMouseExited(mouseEvent -> addUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        addUserBtn.setOnAction(e -> {
            // 实现添加新用户功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> addUserDialog = new javafx.scene.control.Dialog<>();
            addUserDialog.setTitle("添加新用户");
            addUserDialog.setHeaderText("添加新用户");
            addUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表单
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 添加表单元素
            javafx.scene.control.Label usernameLabel = new javafx.scene.control.Label("用户名:");
            usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField usernameField = new javafx.scene.control.TextField();
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label passwordLabel = new javafx.scene.control.Label("密码:");
            passwordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("邮箱:");
            emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
            emailField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label phoneLabel = new javafx.scene.control.Label("电话:");
            phoneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
            phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("角色:");
            roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.ComboBox<model.UserRole> roleComboBox = new javafx.scene.control.ComboBox<>();
            roleComboBox.getItems().addAll(model.UserRole.TA, model.UserRole.MO, model.UserRole.ADMIN);
            roleComboBox.setValue(model.UserRole.TA);
            roleComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label departmentLabel = new javafx.scene.control.Label("部门:");
            departmentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField departmentField = new javafx.scene.control.TextField();
            departmentField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 添加到网格
            grid.add(usernameLabel, 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(passwordLabel, 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(emailLabel, 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(phoneLabel, 0, 3);
            grid.add(phoneField, 1, 3);
            grid.add(roleLabel, 0, 4);
            grid.add(roleComboBox, 1, 4);
            grid.add(departmentLabel, 0, 5);
            grid.add(departmentField, 1, 5);
            
            // 设置对话框内容
            addUserDialog.getDialogPane().setContent(grid);
            
            // 添加按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            addUserDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // 美化按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) addUserDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) addUserDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 设置结果转换器
            addUserDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    java.util.Map<String, Object> result = new java.util.HashMap<>();
                    result.put("username", usernameField.getText());
                    result.put("password", passwordField.getText());
                    result.put("email", emailField.getText());
                    result.put("phone", phoneField.getText());
                    result.put("role", roleComboBox.getValue());
                    result.put("department", departmentField.getText());
                    return result;
                }
                return null;
            });
            
            // 显示对话框并处理结果
            java.util.Optional<java.util.Map<String, Object>> result = addUserDialog.showAndWait();
            result.ifPresent(data -> {
                model.User user = service.UserService.register(
                    (String) data.get("username"),
                    (String) data.get("password"),
                    (String) data.get("email"),
                    (String) data.get("phone"),
                    (model.UserRole) data.get("role"),
                    (String) data.get("department")
                );
                
                if (user != null) {
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("成功");
                    successAlert.setHeaderText("添加用户成功");
                    successAlert.setContentText("用户 " + user.getUsername() + " 已成功添加");
                    successAlert.showAndWait();
                } else {
                    javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    errorAlert.setTitle("错误");
                    errorAlert.setHeaderText("添加用户失败");
                    errorAlert.setContentText("请检查输入信息是否正确");
                    errorAlert.showAndWait();
                }
            });
        });
        
        javafx.scene.control.Button editUserBtn = new javafx.scene.control.Button("编辑用户信息");
        editUserBtn.setPrefWidth(200);
        editUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        editUserBtn.setOnMouseEntered(mouseEvent -> editUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        editUserBtn.setOnMouseExited(mouseEvent -> editUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        editUserBtn.setOnAction(e -> {
            // 实现编辑用户信息功能
            javafx.scene.control.Dialog<Void> editUserDialog = new javafx.scene.control.Dialog<>();
            editUserDialog.setTitle("编辑用户信息");
            editUserDialog.setHeaderText("选择要编辑的用户");
            editUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #90caf9; -fx-selection-bar-text: #000000; -fx-cell-focus-inner-border: #90caf9; -fx-focus-color: #90caf9;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("邮箱");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> phoneCol = new javafx.scene.control.TableColumn<>("电话");
            phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
            phoneCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            userTable.getColumns().addAll(usernameCol, emailCol, phoneCol);
            
            // 加载用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // 设置表格为可调整列宽
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 设置选择模式为单行选择
            userTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // 创建编辑按钮
            javafx.scene.control.Button editBtn = new javafx.scene.control.Button("编辑选中用户");
            editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            editBtn.setOnMouseEntered(mouseEvent -> editBtn.setStyle("-fx-background-color: #e68a00; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnMouseExited(mouseEvent -> editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnAction(editEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // 创建编辑表单
                    javafx.scene.control.Dialog<java.util.Map<String, Object>> editFormDialog = new javafx.scene.control.Dialog<>();
                    editFormDialog.setTitle("编辑用户信息");
                    editFormDialog.setHeaderText("编辑用户: " + selectedUser.getUsername());
                    editFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // 创建表单
                    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                    grid.setHgap(15);
                    grid.setVgap(15);
                    grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
                    grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加表单元素
                    javafx.scene.control.Label usernameLabel = new javafx.scene.control.Label("用户名:");
                    usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField usernameField = new javafx.scene.control.TextField(selectedUser.getUsername());
                    usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("邮箱:");
                    emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField emailField = new javafx.scene.control.TextField(selectedUser.getEmail());
                    emailField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label phoneLabel = new javafx.scene.control.Label("电话:");
                    phoneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField(selectedUser.getPhone());
                    phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // 添加到网格
                    grid.add(usernameLabel, 0, 0);
                    grid.add(usernameField, 1, 0);
                    grid.add(emailLabel, 0, 1);
                    grid.add(emailField, 1, 1);
                    grid.add(phoneLabel, 0, 2);
                    grid.add(phoneField, 1, 2);
                    
                    // 设置对话框内容
                    editFormDialog.getDialogPane().setContent(grid);
                    
                    // 添加按钮
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    editFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // 美化按钮
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // 设置结果转换器
                    editFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            java.util.Map<String, Object> result = new java.util.HashMap<>();
                            result.put("username", usernameField.getText());
                            result.put("email", emailField.getText());
                            result.put("phone", phoneField.getText());
                            return result;
                        }
                        return null;
                    });
                    
                    // 显示对话框并处理结果
                    java.util.Optional<java.util.Map<String, Object>> result = editFormDialog.showAndWait();
                    result.ifPresent(data -> {
                        selectedUser.setUsername((String) data.get("username"));
                        selectedUser.setEmail((String) data.get("email"));
                        selectedUser.setPhone((String) data.get("phone"));
                        
                        boolean success = service.UserService.updateUser(selectedUser);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("编辑用户成功");
                            successAlert.setContentText("用户信息已成功更新");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("编辑用户失败");
                            errorAlert.setContentText("更新用户信息失败");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择用户");
                    alert.setContentText("请先选择要编辑的用户");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, editBtn);
            
            // 设置对话框内容
            editUserDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            editUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            editUserDialog.showAndWait();
        });
        
        javafx.scene.control.Button disableUserBtn = new javafx.scene.control.Button("禁用/启用用户");
        disableUserBtn.setPrefWidth(200);
        disableUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        disableUserBtn.setOnMouseEntered(mouseEvent -> disableUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        disableUserBtn.setOnMouseExited(mouseEvent -> disableUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        disableUserBtn.setOnAction(e -> {
            // 实现禁用/启用用户功能
            javafx.scene.control.Dialog<Void> toggleUserDialog = new javafx.scene.control.Dialog<>();
            toggleUserDialog.setTitle("禁用/启用用户");
            toggleUserDialog.setHeaderText("选择要操作的用户");
            toggleUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserStatus> statusCol = new javafx.scene.control.TableColumn<>("状态");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            userTable.getColumns().addAll(usernameCol, statusCol);
            
            // 加载用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // 设置表格为可调整列宽
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建切换状态按钮
            javafx.scene.control.Button toggleBtn = new javafx.scene.control.Button("切换选中用户状态");
            toggleBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            toggleBtn.setOnMouseEntered(mouseEvent -> toggleBtn.setStyle("-fx-background-color: #7b1fa2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            toggleBtn.setOnMouseExited(mouseEvent -> toggleBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            toggleBtn.setOnAction(toggleEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    model.UserStatus newStatus = selectedUser.getStatus() == model.UserStatus.ACTIVE ? model.UserStatus.LOCKED : model.UserStatus.ACTIVE;
                    boolean success = service.UserService.toggleUserStatus(selectedUser.getId(), newStatus);
                    if (success) {
                        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        successAlert.setTitle("成功");
                        successAlert.setHeaderText("操作成功");
                        successAlert.setContentText("用户状态已成功切换为 " + newStatus);
                        successAlert.showAndWait();
                        
                        // 刷新表格
                        userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                    } else {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("错误");
                        errorAlert.setHeaderText("操作失败");
                        errorAlert.setContentText("切换用户状态失败");
                        errorAlert.showAndWait();
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择用户");
                    alert.setContentText("请先选择要操作的用户");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, toggleBtn);
            
            // 设置对话框内容
            toggleUserDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            toggleUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            toggleUserDialog.showAndWait();
        });
        
        javafx.scene.control.Button reviewTAProfileBtn = new javafx.scene.control.Button("审核TA档案");
        reviewTAProfileBtn.setPrefWidth(200);
        reviewTAProfileBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        reviewTAProfileBtn.setOnMouseEntered(mouseEvent -> reviewTAProfileBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        reviewTAProfileBtn.setOnMouseExited(mouseEvent -> reviewTAProfileBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        reviewTAProfileBtn.setOnAction(e -> {
            javafx.scene.control.Dialog<Void> reviewDialog = new javafx.scene.control.Dialog<>();
            reviewDialog.setTitle("审核TA档案");
            reviewDialog.setHeaderText("选择TA并审核档案状态");
            reviewDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");

            javafx.scene.control.TableView<model.TA> taTable = new javafx.scene.control.TableView<>();
            taTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");

            javafx.scene.control.TableColumn<model.TA, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            javafx.scene.control.TableColumn<model.TA, String> departmentCol = new javafx.scene.control.TableColumn<>("院系");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            javafx.scene.control.TableColumn<model.TA, model.ProfileStatus> profileStatusCol = new javafx.scene.control.TableColumn<>("档案状态");
            profileStatusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("profileStatus"));
            javafx.scene.control.TableColumn<model.TA, String> reviewCommentCol = new javafx.scene.control.TableColumn<>("审核意见");
            reviewCommentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("profileReviewComment"));
            taTable.getColumns().addAll(usernameCol, departmentCol, profileStatusCol, reviewCommentCol);
            taTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

            java.util.List<model.TA> taUsers = new java.util.ArrayList<>();
            for (model.User userItem : service.UserService.getAllUsers()) {
                if (userItem instanceof model.TA) {
                    taUsers.add((model.TA) userItem);
                }
            }
            taTable.setItems(javafx.collections.FXCollections.observableArrayList(taUsers));

            javafx.scene.control.TextArea commentArea = new javafx.scene.control.TextArea();
            commentArea.setPromptText("输入审核意见；批准时可留空，拒绝时建议填写原因");
            commentArea.setPrefRowCount(4);
            commentArea.setWrapText(true);

            javafx.scene.control.Button approveBtn = new javafx.scene.control.Button("通过选中TA");
            approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            approveBtn.setOnAction(actionEvent -> {
                model.TA selectedTa = taTable.getSelectionModel().getSelectedItem();
                if (selectedTa == null) {
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "请先选择一个TA。", javafx.scene.control.ButtonType.OK).showAndWait();
                    return;
                }
                String comment = commentArea.getText() == null || commentArea.getText().trim().isEmpty() ? "Approved by admin" : commentArea.getText().trim();
                boolean success = service.UserService.reviewTAProfile(selectedTa.getId(), model.ProfileStatus.APPROVED, comment);
                if (success) {
                    taTable.setItems(javafx.collections.FXCollections.observableArrayList(getTaUsers()));
                    taTable.refresh();
                    commentArea.clear();
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "已将 " + selectedTa.getUsername() + " 的档案设为 APPROVED。", javafx.scene.control.ButtonType.OK).showAndWait();
                }
            });

            javafx.scene.control.Button rejectBtn = new javafx.scene.control.Button("驳回选中TA");
            rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            rejectBtn.setOnAction(actionEvent -> {
                model.TA selectedTa = taTable.getSelectionModel().getSelectedItem();
                if (selectedTa == null) {
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "请先选择一个TA。", javafx.scene.control.ButtonType.OK).showAndWait();
                    return;
                }
                String comment = commentArea.getText() == null ? "" : commentArea.getText().trim();
                if (comment.isEmpty()) {
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "驳回时请填写审核意见。", javafx.scene.control.ButtonType.OK).showAndWait();
                    return;
                }
                boolean success = service.UserService.reviewTAProfile(selectedTa.getId(), model.ProfileStatus.REJECTED, comment);
                if (success) {
                    taTable.setItems(javafx.collections.FXCollections.observableArrayList(getTaUsers()));
                    taTable.refresh();
                    commentArea.clear();
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "已将 " + selectedTa.getUsername() + " 的档案设为 REJECTED。", javafx.scene.control.ButtonType.OK).showAndWait();
                }
            });

            javafx.scene.layout.HBox actionBox = new javafx.scene.layout.HBox(12, approveBtn, rejectBtn);
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(12, taTable, new javafx.scene.control.Label("审核意见"), commentArea, actionBox);
            content.setPadding(new javafx.geometry.Insets(10));
            reviewDialog.getDialogPane().setContent(content);
            reviewDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            reviewDialog.showAndWait();
        });

        javafx.scene.control.Button deleteUserBtn = new javafx.scene.control.Button("删除用户");
        deleteUserBtn.setPrefWidth(200);
        deleteUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        deleteUserBtn.setOnMouseEntered(mouseEvent -> deleteUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        deleteUserBtn.setOnMouseExited(mouseEvent -> deleteUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        deleteUserBtn.setOnAction(e -> {
            // 实现删除用户功能
            javafx.scene.control.Dialog<Void> deleteUserDialog = new javafx.scene.control.Dialog<>();
            deleteUserDialog.setTitle("删除用户");
            deleteUserDialog.setHeaderText("选择要删除的用户");
            deleteUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("邮箱");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            userTable.getColumns().addAll(usernameCol, emailCol);
            
            // 加载用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // 设置表格为可调整列宽
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建删除按钮
            javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("删除选中用户");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            deleteBtn.setOnMouseEntered(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnMouseExited(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnAction(deleteEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // 确认删除
                    javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("确认删除");
                    confirmAlert.setHeaderText("删除用户");
                    confirmAlert.setContentText("确定要删除用户 " + selectedUser.getUsername() + " 吗？");
                    
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        // 从数据存储中删除用户
                        java.util.List<model.User> allUsers = service.DataStorage.getUsers();
                        boolean removed = allUsers.removeIf(user -> user.getId().equals(selectedUser.getId()));
                        if (removed) {
                            service.DataStorage.saveUsers(allUsers);
                            service.DataStorage.addLog("DELETE_USER", "admin", "User deleted: " + selectedUser.getUsername());
                            
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("删除用户成功");
                            successAlert.setContentText("用户已成功删除");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("删除用户失败");
                            errorAlert.setContentText("删除用户失败");
                            errorAlert.showAndWait();
                        }
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择用户");
                    alert.setContentText("请先选择要删除的用户");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, deleteBtn);
            
            // 设置对话框内容
            deleteUserDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            deleteUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            deleteUserDialog.showAndWait();
        });
        
        // 添加按钮到VBox
        vbox.getChildren().addAll(viewUsersBtn, addUserBtn, editUserBtn, disableUserBtn, reviewTAProfileBtn, deleteUserBtn);
        
        // 设置对话框内容
        dialog.getDialogPane().setContent(vbox);
        
        // 添加确定按钮
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        
        // 显示对话框
        dialog.showAndWait();
    }
    
    // 处理职位管理按钮点击
    @FXML
    private void handleJobManagement(ActionEvent event) {
        // 创建职位管理对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("职位管理");
        dialog.setHeaderText("职位管理功能");
        
        // 创建按钮网格
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // 添加功能按钮
        javafx.scene.control.Button viewJobsBtn = new javafx.scene.control.Button("查看所有职位");
        viewJobsBtn.setPrefWidth(200);
        viewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        viewJobsBtn.setOnMouseEntered(mouseEvent -> viewJobsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        viewJobsBtn.setOnMouseExited(mouseEvent -> viewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        viewJobsBtn.setOnAction(e -> {
            // 实现查看所有职位功能
            javafx.scene.control.Dialog<Void> viewJobsDialog = new javafx.scene.control.Dialog<>();
            viewJobsDialog.setTitle("查看所有职位");
            viewJobsDialog.setHeaderText("所有职位列表");
            viewJobsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("职位名称");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobType> typeCol = new javafx.scene.control.TableColumn<>("职位类型");
            typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
            typeCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("部门");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobStatus> statusCol = new javafx.scene.control.TableColumn<>("状态");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            jobTable.getColumns().addAll(titleCol, typeCol, departmentCol, statusCol);
            
            // 加载职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // 设置表格为可调整列宽
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建滚动面板
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(jobTable);
            scrollPane.setPrefHeight(400);
            scrollPane.setPrefWidth(600);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 设置对话框内容
            viewJobsDialog.getDialogPane().setContent(scrollPane);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            viewJobsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) viewJobsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            viewJobsDialog.showAndWait();
        });
        
        javafx.scene.control.Button reviewJobsBtn = new javafx.scene.control.Button("审核职位发布");
        reviewJobsBtn.setPrefWidth(200);
        reviewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        reviewJobsBtn.setOnMouseEntered(mouseEvent -> reviewJobsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        reviewJobsBtn.setOnMouseExited(mouseEvent -> reviewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        reviewJobsBtn.setOnAction(e -> {
            // 实现审核职位发布功能
            javafx.scene.control.Dialog<Void> reviewJobsDialog = new javafx.scene.control.Dialog<>();
            reviewJobsDialog.setTitle("审核职位发布");
            reviewJobsDialog.setHeaderText("待审核职位列表");
            reviewJobsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("职位名称");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("部门");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            jobTable.getColumns().addAll(titleCol, departmentCol);
            
            // 加载待审核职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            java.util.List<model.Job> pendingJobs = new java.util.ArrayList<>();
            for (model.Job job : jobs) {
                if (job.getStatus() == model.JobStatus.PENDING) {
                    pendingJobs.add(job);
                }
            }
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(pendingJobs));
            
            // 设置表格为可调整列宽
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建审核按钮
            javafx.scene.control.Button reviewBtn = new javafx.scene.control.Button("审核选中职位");
            reviewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            reviewBtn.setOnMouseEntered(mouseEvent -> reviewBtn.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            reviewBtn.setOnMouseExited(mouseEvent -> reviewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            reviewBtn.setOnAction(reviewEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // 创建审核表单
                    javafx.scene.control.Dialog<model.JobStatus> reviewFormDialog = new javafx.scene.control.Dialog<>();
                    reviewFormDialog.setTitle("审核职位");
                    reviewFormDialog.setHeaderText("审核职位: " + selectedJob.getTitle());
                    reviewFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // 创建表单
                    javafx.scene.layout.VBox vbox1 = new javafx.scene.layout.VBox(15);
                    vbox1.setPadding(new javafx.geometry.Insets(20));
                    vbox1.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加审核选项
                    javafx.scene.control.Label statusLabel = new javafx.scene.control.Label("审核结果:");
                    statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.ToggleGroup toggleGroup = new javafx.scene.control.ToggleGroup();
                    
                    javafx.scene.control.RadioButton approveBtn = new javafx.scene.control.RadioButton("通过");
                    approveBtn.setToggleGroup(toggleGroup);
                    approveBtn.setSelected(true);
                    approveBtn.setStyle("-fx-font-size: 14px;");
                    
                    javafx.scene.control.RadioButton rejectBtn = new javafx.scene.control.RadioButton("拒绝");
                    rejectBtn.setToggleGroup(toggleGroup);
                    rejectBtn.setStyle("-fx-font-size: 14px;");
                    
                    javafx.scene.control.Label commentLabel = new javafx.scene.control.Label("审核意见:");
                    commentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextArea commentField = new javafx.scene.control.TextArea();
                    commentField.setPrefHeight(100);
                    commentField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // 添加到VBox
                    vbox1.getChildren().addAll(statusLabel, approveBtn, rejectBtn, commentLabel, commentField);
                    
                    // 设置对话框内容
                    reviewFormDialog.getDialogPane().setContent(vbox1);
                    
                    // 添加按钮
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    reviewFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // 美化按钮
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) reviewFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) reviewFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // 设置结果转换器
                    reviewFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            if (approveBtn.isSelected()) {
                                return model.JobStatus.PUBLISHED;
                            } else {
                                return model.JobStatus.REJECTED;
                            }
                        }
                        return null;
                    });
                    
                    // 显示对话框并处理结果
                    java.util.Optional<model.JobStatus> result = reviewFormDialog.showAndWait();
                    result.ifPresent(status -> {
                        boolean success = service.JobService.reviewJob(selectedJob.getId(), status, commentField.getText(), user.getId());
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("审核成功");
                            successAlert.setContentText("职位审核已完成");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            java.util.List<model.Job> updatedPendingJobs = new java.util.ArrayList<>();
                            for (model.Job job : service.JobService.getAllJobs()) {
                                if (job.getStatus() == model.JobStatus.PENDING) {
                                    updatedPendingJobs.add(job);
                                }
                            }
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(updatedPendingJobs));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("审核失败");
                            errorAlert.setContentText("职位审核失败");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择职位");
                    alert.setContentText("请先选择要审核的职位");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, reviewBtn);
            
            // 设置对话框内容
            reviewJobsDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            reviewJobsDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            reviewJobsDialog.showAndWait();
        });
        
        javafx.scene.control.Button editJobBtn = new javafx.scene.control.Button("编辑职位信息");
        editJobBtn.setPrefWidth(200);
        editJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        editJobBtn.setOnMouseEntered(mouseEvent -> editJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        editJobBtn.setOnMouseExited(mouseEvent -> editJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        editJobBtn.setOnAction(e -> {
            // 实现编辑职位信息功能
            javafx.scene.control.Dialog<Void> editJobDialog = new javafx.scene.control.Dialog<>();
            editJobDialog.setTitle("编辑职位信息");
            editJobDialog.setHeaderText("选择要编辑的职位");
            editJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("职位名称");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("部门");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            jobTable.getColumns().addAll(titleCol, departmentCol);
            
            // 加载职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // 设置表格为可调整列宽
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建编辑按钮
            javafx.scene.control.Button editBtn = new javafx.scene.control.Button("编辑选中职位");
            editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            editBtn.setOnMouseEntered(mouseEvent -> editBtn.setStyle("-fx-background-color: #e68a00; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnMouseExited(mouseEvent -> editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnAction(editEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // 创建编辑表单
                    javafx.scene.control.Dialog<java.util.Map<String, Object>> editFormDialog = new javafx.scene.control.Dialog<>();
                    editFormDialog.setTitle("编辑职位信息");
                    editFormDialog.setHeaderText("编辑职位: " + selectedJob.getTitle());
                    editFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // 创建表单
                    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                    grid.setHgap(15);
                    grid.setVgap(15);
                    grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
                    grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加表单元素
                    javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("职位名称:");
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField titleField = new javafx.scene.control.TextField(selectedJob.getTitle());
                    titleField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label descriptionLabel = new javafx.scene.control.Label("职位描述:");
                    descriptionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextArea descriptionField = new javafx.scene.control.TextArea(selectedJob.getDescription());
                    descriptionField.setPrefHeight(100);
                    descriptionField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // 添加到网格
                    grid.add(titleLabel, 0, 0);
                    grid.add(titleField, 1, 0);
                    grid.add(descriptionLabel, 0, 1);
                    grid.add(descriptionField, 1, 1);
                    
                    // 设置对话框内容
                    editFormDialog.getDialogPane().setContent(grid);
                    
                    // 添加按钮
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    editFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // 美化按钮
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // 设置结果转换器
                    editFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            java.util.Map<String, Object> result = new java.util.HashMap<>();
                            result.put("title", titleField.getText());
                            result.put("description", descriptionField.getText());
                            return result;
                        }
                        return null;
                    });
                    
                    // 显示对话框并处理结果
                    java.util.Optional<java.util.Map<String, Object>> result = editFormDialog.showAndWait();
                    result.ifPresent(data -> {
                        selectedJob.setTitle((String) data.get("title"));
                        selectedJob.setDescription((String) data.get("description"));
                        
                        boolean success = service.JobService.updateJob(selectedJob);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("编辑职位成功");
                            successAlert.setContentText("职位信息已成功更新");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("编辑职位失败");
                            errorAlert.setContentText("更新职位信息失败");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择职位");
                    alert.setContentText("请先选择要编辑的职位");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, editBtn);
            
            // 设置对话框内容
            editJobDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            editJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            editJobDialog.showAndWait();
        });
        
        javafx.scene.control.Button disableJobBtn = new javafx.scene.control.Button("禁用/启用职位");
        disableJobBtn.setPrefWidth(200);
        disableJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        disableJobBtn.setOnMouseEntered(mouseEvent -> disableJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        disableJobBtn.setOnMouseExited(mouseEvent -> disableJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        disableJobBtn.setOnAction(e -> {
            // 实现禁用/启用职位功能
            javafx.scene.control.Dialog<Void> toggleJobDialog = new javafx.scene.control.Dialog<>();
            toggleJobDialog.setTitle("禁用/启用职位");
            toggleJobDialog.setHeaderText("选择要操作的职位");
            toggleJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("职位名称");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobStatus> statusCol = new javafx.scene.control.TableColumn<>("状态");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            jobTable.getColumns().addAll(titleCol, statusCol);
            
            // 加载职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // 设置表格为可调整列宽
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建切换状态按钮
            javafx.scene.control.Button toggleBtn = new javafx.scene.control.Button("切换选中职位状态");
            toggleBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            toggleBtn.setOnMouseEntered(mouseEvent -> toggleBtn.setStyle("-fx-background-color: #7b1fa2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            toggleBtn.setOnMouseExited(mouseEvent -> toggleBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            toggleBtn.setOnAction(toggleEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    model.JobStatus newStatus;
                    if (selectedJob.getStatus() == model.JobStatus.PUBLISHED) {
                        newStatus = model.JobStatus.CLOSED;
                    } else if (selectedJob.getStatus() == model.JobStatus.CLOSED) {
                        newStatus = model.JobStatus.PUBLISHED;
                    } else {
                        newStatus = model.JobStatus.PUBLISHED;
                    }
                    
                    boolean success = service.JobService.closeJob(selectedJob.getId(), user.getId());
                    if (success) {
                        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        successAlert.setTitle("成功");
                        successAlert.setHeaderText("操作成功");
                        successAlert.setContentText("职位状态已成功切换为 " + newStatus);
                        successAlert.showAndWait();
                        
                        // 刷新表格
                        jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                    } else {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("错误");
                        errorAlert.setHeaderText("操作失败");
                        errorAlert.setContentText("切换职位状态失败");
                        errorAlert.showAndWait();
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择职位");
                    alert.setContentText("请先选择要操作的职位");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, toggleBtn);
            
            // 设置对话框内容
            toggleJobDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            toggleJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            toggleJobDialog.showAndWait();
        });
        
        javafx.scene.control.Button deleteJobBtn = new javafx.scene.control.Button("删除职位");
        deleteJobBtn.setPrefWidth(200);
        deleteJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        deleteJobBtn.setOnMouseEntered(mouseEvent -> deleteJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        deleteJobBtn.setOnMouseExited(mouseEvent -> deleteJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        deleteJobBtn.setOnAction(e -> {
            // 实现删除职位功能
            javafx.scene.control.Dialog<Void> deleteJobDialog = new javafx.scene.control.Dialog<>();
            deleteJobDialog.setTitle("删除职位");
            deleteJobDialog.setHeaderText("选择要删除的职位");
            deleteJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("职位名称");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("部门");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            jobTable.getColumns().addAll(titleCol, departmentCol);
            
            // 加载职位数据
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // 设置表格为可调整列宽
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建删除按钮
            javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("删除选中职位");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            deleteBtn.setOnMouseEntered(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnMouseExited(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnAction(deleteEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // 确认删除
                    javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("确认删除");
                    confirmAlert.setHeaderText("删除职位");
                    confirmAlert.setContentText("确定要删除职位 " + selectedJob.getTitle() + " 吗？");
                    
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        // 从数据存储中删除职位
                        java.util.List<model.Job> allJobs = service.DataStorage.getJobs();
                        boolean removed = allJobs.removeIf(job -> job.getId().equals(selectedJob.getId()));
                        if (removed) {
                            service.DataStorage.saveJobs(allJobs);
                            service.DataStorage.addLog("DELETE_JOB", "admin", "Job deleted: " + selectedJob.getTitle());
                            
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("删除职位成功");
                            successAlert.setContentText("职位已成功删除");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("删除职位失败");
                            errorAlert.setContentText("删除职位失败");
                            errorAlert.showAndWait();
                        }
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择职位");
                    alert.setContentText("请先选择要删除的职位");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, deleteBtn);
            
            // 设置对话框内容
            deleteJobDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            deleteJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            deleteJobDialog.showAndWait();
        });
        
        // 添加按钮到VBox
        vbox.getChildren().addAll(viewJobsBtn, reviewJobsBtn, editJobBtn, disableJobBtn, deleteJobBtn);

        // 设置对话框内容
        dialog.getDialogPane().setContent(vbox);

        // 添加确定按钮
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);

        // 显示对话框
        dialog.showAndWait();
    }

    // 处理系统配置按钮点击
    @FXML
    private void handleSystemConfiguration(ActionEvent event) {
        // 创建系统配置对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("系统配置");
        dialog.setHeaderText("系统配置功能");
        
        // 创建按钮网格
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // 添加功能按钮
        javafx.scene.control.Button systemParamsBtn = new javafx.scene.control.Button("系统参数设置");
        systemParamsBtn.setPrefWidth(200);
        systemParamsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        systemParamsBtn.setOnMouseEntered(mouseEvent -> systemParamsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        systemParamsBtn.setOnMouseExited(mouseEvent -> systemParamsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        systemParamsBtn.setOnAction(e -> {
            // 实现系统参数设置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> systemParamsDialog = new javafx.scene.control.Dialog<>();
            systemParamsDialog.setTitle("系统参数设置");
            systemParamsDialog.setHeaderText("系统参数设置");
            systemParamsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表单
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 添加表单元素
            javafx.scene.control.Label maxUsersLabel = new javafx.scene.control.Label("最大用户数:");
            maxUsersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField maxUsersField = new javafx.scene.control.TextField("1000");
            maxUsersField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label maxJobsLabel = new javafx.scene.control.Label("最大职位数:");
            maxJobsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField maxJobsField = new javafx.scene.control.TextField("500");
            maxJobsField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label sessionTimeoutLabel = new javafx.scene.control.Label("会话超时(分钟):");
            sessionTimeoutLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField sessionTimeoutField = new javafx.scene.control.TextField("30");
            sessionTimeoutField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 添加到网格
            grid.add(maxUsersLabel, 0, 0);
            grid.add(maxUsersField, 1, 0);
            grid.add(maxJobsLabel, 0, 1);
            grid.add(maxJobsField, 1, 1);
            grid.add(sessionTimeoutLabel, 0, 2);
            grid.add(sessionTimeoutField, 1, 2);
            
            // 设置对话框内容
            systemParamsDialog.getDialogPane().setContent(grid);
            
            // 添加按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            systemParamsDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // 美化按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) systemParamsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) systemParamsDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 设置结果转换器
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
            
            // 显示对话框并处理结果
            java.util.Optional<java.util.Map<String, Object>> result = systemParamsDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存系统参数
                // 这里只是模拟实现，实际项目中应该保存到配置文件或数据库
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText("系统参数设置成功");
                successAlert.setContentText("系统参数已成功更新");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button emailConfigBtn = new javafx.scene.control.Button("邮件服务器配置");
        emailConfigBtn.setPrefWidth(200);
        emailConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        emailConfigBtn.setOnMouseEntered(mouseEvent -> emailConfigBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        emailConfigBtn.setOnMouseExited(mouseEvent -> emailConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        emailConfigBtn.setOnAction(e -> {
            // 实现邮件服务器配置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> emailConfigDialog = new javafx.scene.control.Dialog<>();
            emailConfigDialog.setTitle("邮件服务器配置");
            emailConfigDialog.setHeaderText("邮件服务器配置");
            emailConfigDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表单
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 添加表单元素
            javafx.scene.control.Label smtpServerLabel = new javafx.scene.control.Label("SMTP服务器:");
            smtpServerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField smtpServerField = new javafx.scene.control.TextField("smtp.example.com");
            smtpServerField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label smtpPortLabel = new javafx.scene.control.Label("SMTP端口:");
            smtpPortLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField smtpPortField = new javafx.scene.control.TextField("587");
            smtpPortField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailUsernameLabel = new javafx.scene.control.Label("邮箱用户名:");
            emailUsernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField emailUsernameField = new javafx.scene.control.TextField("admin@example.com");
            emailUsernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailPasswordLabel = new javafx.scene.control.Label("邮箱密码:");
            emailPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.PasswordField emailPasswordField = new javafx.scene.control.PasswordField();
            emailPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 添加到网格
            grid.add(smtpServerLabel, 0, 0);
            grid.add(smtpServerField, 1, 0);
            grid.add(smtpPortLabel, 0, 1);
            grid.add(smtpPortField, 1, 1);
            grid.add(emailUsernameLabel, 0, 2);
            grid.add(emailUsernameField, 1, 2);
            grid.add(emailPasswordLabel, 0, 3);
            grid.add(emailPasswordField, 1, 3);
            
            // 设置对话框内容
            emailConfigDialog.getDialogPane().setContent(grid);
            
            // 添加按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            emailConfigDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // 美化按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) emailConfigDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) emailConfigDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 设置结果转换器
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
            
            // 显示对话框并处理结果
            java.util.Optional<java.util.Map<String, Object>> result = emailConfigDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存邮件服务器配置
                // 这里只是模拟实现，实际项目中应该保存到配置文件或数据库
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText("邮件服务器配置成功");
                successAlert.setContentText("邮件服务器配置已成功更新");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button backupConfigBtn = new javafx.scene.control.Button("数据备份设置");
        backupConfigBtn.setPrefWidth(200);
        backupConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        backupConfigBtn.setOnMouseEntered(mouseEvent -> backupConfigBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        backupConfigBtn.setOnMouseExited(mouseEvent -> backupConfigBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        backupConfigBtn.setOnAction(e -> {
            // 实现数据备份设置功能
            javafx.scene.control.Dialog<java.util.Map<String, Object>> backupConfigDialog = new javafx.scene.control.Dialog<>();
            backupConfigDialog.setTitle("数据备份设置");
            backupConfigDialog.setHeaderText("数据备份设置");
            backupConfigDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表单
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 添加表单元素
            javafx.scene.control.Label backupPathLabel = new javafx.scene.control.Label("备份路径:");
            backupPathLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupPathField = new javafx.scene.control.TextField("d:/backup");
            backupPathField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label backupIntervalLabel = new javafx.scene.control.Label("备份间隔(小时):");
            backupIntervalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupIntervalField = new javafx.scene.control.TextField("24");
            backupIntervalField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label backupRetentionLabel = new javafx.scene.control.Label("备份保留天数:");
            backupRetentionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField backupRetentionField = new javafx.scene.control.TextField("7");
            backupRetentionField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 添加到网格
            grid.add(backupPathLabel, 0, 0);
            grid.add(backupPathField, 1, 0);
            grid.add(backupIntervalLabel, 0, 1);
            grid.add(backupIntervalField, 1, 1);
            grid.add(backupRetentionLabel, 0, 2);
            grid.add(backupRetentionField, 1, 2);
            
            // 设置对话框内容
            backupConfigDialog.getDialogPane().setContent(grid);
            
            // 添加按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            backupConfigDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // 美化按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) backupConfigDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) backupConfigDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 设置结果转换器
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
            
            // 显示对话框并处理结果
            java.util.Optional<java.util.Map<String, Object>> result = backupConfigDialog.showAndWait();
            result.ifPresent(data -> {
                // 保存数据备份设置
                // 这里只是模拟实现，实际项目中应该保存到配置文件或数据库
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText("数据备份设置成功");
                successAlert.setContentText("数据备份设置已成功更新");
                successAlert.showAndWait();
            });
        });
        
        javafx.scene.control.Button logManagementBtn = new javafx.scene.control.Button("日志管理");
        logManagementBtn.setPrefWidth(200);
        logManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        logManagementBtn.setOnMouseEntered(mouseEvent -> logManagementBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        logManagementBtn.setOnMouseExited(mouseEvent -> logManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        logManagementBtn.setOnAction(e -> {
            // 实现日志管理功能
            javafx.scene.control.Dialog<Void> logManagementDialog = new javafx.scene.control.Dialog<>();
            logManagementDialog.setTitle("日志管理");
            logManagementDialog.setHeaderText("系统日志");
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
                    logTextArea.setText("日志文件不存在");
                }
            } catch (Exception ex) {
                logTextArea.setText("加载日志失败: " + ex.getMessage());
            }
            
            // 创建滚动面板
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(logTextArea);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // 为清空日志按钮添加事件
            javafx.scene.control.Button clearLogBtn = new javafx.scene.control.Button("清空日志");
            clearLogBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            clearLogBtn.setOnMouseEntered(mouseEvent -> clearLogBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            clearLogBtn.setOnMouseExited(mouseEvent -> clearLogBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            clearLogBtn.setOnAction(clearEvent -> {
                javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("确认清空");
                confirmAlert.setHeaderText("清空日志");
                confirmAlert.setContentText("确定要清空所有日志吗？");
                
                java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    try {
                        java.nio.file.Path logPath = java.nio.file.Paths.get("d:\\Soft Engineering\\miniproject(version1)\\src\\data\\logs.txt");
                        java.nio.file.Files.write(logPath, new byte[0]);
                        logTextArea.setText("");
                        
                        javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        successAlert.setTitle("成功");
                        successAlert.setHeaderText("清空日志成功");
                        successAlert.setContentText("日志已成功清空");
                        successAlert.showAndWait();
                    } catch (Exception ex) {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("错误");
                        errorAlert.setHeaderText("清空日志失败");
                        errorAlert.setContentText("清空日志失败: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
            
            // 创建VBox包含滚动面板和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(scrollPane, clearLogBtn);
            
            // 设置对话框内容
            logManagementDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            logManagementDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) logManagementDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            logManagementDialog.showAndWait();
        });
        
        javafx.scene.control.Button permissionManagementBtn = new javafx.scene.control.Button("权限管理");
        permissionManagementBtn.setPrefWidth(200);
        permissionManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        permissionManagementBtn.setOnMouseEntered(mouseEvent -> permissionManagementBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        permissionManagementBtn.setOnMouseExited(mouseEvent -> permissionManagementBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        permissionManagementBtn.setOnAction(e -> {
            // 实现权限管理功能
            javafx.scene.control.Dialog<Void> permissionManagementDialog = new javafx.scene.control.Dialog<>();
            permissionManagementDialog.setTitle("权限管理");
            permissionManagementDialog.setHeaderText("用户权限设置");
            permissionManagementDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建表格视图
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // 创建列
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("用户名");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserRole> roleCol = new javafx.scene.control.TableColumn<>("角色");
            roleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("role"));
            roleCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // 添加列到表格
            userTable.getColumns().addAll(usernameCol, roleCol);
            
            // 加载用户数据
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // 设置表格为可调整列宽
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // 创建修改权限按钮
            javafx.scene.control.Button changePermissionBtn = new javafx.scene.control.Button("修改选中用户权限");
            changePermissionBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            changePermissionBtn.setOnMouseEntered(mouseEvent -> changePermissionBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            changePermissionBtn.setOnMouseExited(mouseEvent -> changePermissionBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            changePermissionBtn.setOnAction(changeEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // 创建权限修改表单
                    javafx.scene.control.Dialog<model.UserRole> permissionFormDialog = new javafx.scene.control.Dialog<>();
                    permissionFormDialog.setTitle("修改权限");
                    permissionFormDialog.setHeaderText("修改用户: " + selectedUser.getUsername() + " 的权限");
                    permissionFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // 创建表单
                    javafx.scene.layout.VBox vbox1 = new javafx.scene.layout.VBox(15);
                    vbox1.setPadding(new javafx.geometry.Insets(20));
                    vbox1.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加权限选项
                    javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("新角色:");
                    roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.ComboBox<model.UserRole> roleComboBox = new javafx.scene.control.ComboBox<>();
                    roleComboBox.getItems().addAll(model.UserRole.TA, model.UserRole.MO, model.UserRole.ADMIN);
                    roleComboBox.setValue(selectedUser.getRole());
                    roleComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // 添加到VBox
                    vbox1.getChildren().addAll(roleLabel, roleComboBox);
                    
                    // 设置对话框内容
                    permissionFormDialog.getDialogPane().setContent(vbox1);
                    
                    // 添加按钮
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("取消", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    permissionFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // 美化按钮
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) permissionFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) permissionFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // 设置结果转换器
                    permissionFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            return roleComboBox.getValue();
                        }
                        return null;
                    });
                    
                    // 显示对话框并处理结果
                    java.util.Optional<model.UserRole> result = permissionFormDialog.showAndWait();
                    result.ifPresent(role -> {
                        selectedUser.setRole(role);
                        boolean success = service.UserService.updateUser(selectedUser);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("成功");
                            successAlert.setHeaderText("修改权限成功");
                            successAlert.setContentText("用户权限已成功更新");
                            successAlert.showAndWait();
                            
                            // 刷新表格
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("错误");
                            errorAlert.setHeaderText("修改权限失败");
                            errorAlert.setContentText("更新用户权限失败");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("未选择用户");
                    alert.setContentText("请先选择要修改权限的用户");
                    alert.showAndWait();
                }
            });
            
            // 创建VBox包含表格和按钮
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, changePermissionBtn);
            
            // 设置对话框内容
            permissionManagementDialog.getDialogPane().setContent(dialogContent);
            
            // 添加确定按钮
            permissionManagementDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // 显示对话框
            permissionManagementDialog.showAndWait();
        });
        
        // 添加按钮到VBox
        vbox.getChildren().addAll(systemParamsBtn, emailConfigBtn, backupConfigBtn, logManagementBtn, permissionManagementBtn);
        
        // 设置对话框内容
        dialog.getDialogPane().setContent(vbox);
        
        // 添加确定按钮
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        
        // 显示对话框
        dialog.showAndWait();
    }
    
    // 处理统计分析按钮点击
    @FXML
    private void handleStatisticsAnalysis(ActionEvent event) {
        // 创建统计分析对话框
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("统计分析");
        dialog.setHeaderText("统计分析功能");
        
        // 创建按钮网格
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // 添加功能按钮
        javafx.scene.control.Button userStatsBtn = new javafx.scene.control.Button("用户统计");
        userStatsBtn.setPrefWidth(200);
        userStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        userStatsBtn.setOnMouseEntered(mouseEvent -> userStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        userStatsBtn.setOnMouseExited(mouseEvent -> userStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        userStatsBtn.setOnAction(e -> {
            // 实现用户统计功能
            javafx.scene.control.Dialog<Void> userStatsDialog = new javafx.scene.control.Dialog<>();
            userStatsDialog.setTitle("用户统计");
            userStatsDialog.setHeaderText("用户统计数据");
            userStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建统计内容
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
            
            // 添加统计信息
            javafx.scene.control.Label totalUsersLabel = new javafx.scene.control.Label("总用户数: " + totalUsers);
            totalUsersLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalUsersLabel);
            
            javafx.scene.control.Label taCountLabel = new javafx.scene.control.Label("TA用户数: " + taCount);
            taCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(taCountLabel);
            
            javafx.scene.control.Label moCountLabel = new javafx.scene.control.Label("MO用户数: " + moCount);
            moCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(moCountLabel);
            
            javafx.scene.control.Label adminCountLabel = new javafx.scene.control.Label("管理员用户数: " + adminCount);
            adminCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(adminCountLabel);
            
            javafx.scene.control.Label activeCountLabel = new javafx.scene.control.Label("活跃用户数: " + activeCount);
            activeCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(activeCountLabel);
            
            javafx.scene.control.Label lockedCountLabel = new javafx.scene.control.Label("锁定用户数: " + lockedCount);
            lockedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(lockedCountLabel);
            
            // 设置对话框内容
            userStatsDialog.getDialogPane().setContent(statsContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            userStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) userStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            userStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button jobStatsBtn = new javafx.scene.control.Button("职位统计");
        jobStatsBtn.setPrefWidth(200);
        jobStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        jobStatsBtn.setOnMouseEntered(mouseEvent -> jobStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        jobStatsBtn.setOnMouseExited(mouseEvent -> jobStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        jobStatsBtn.setOnAction(e -> {
            // 实现职位统计功能
            javafx.scene.control.Dialog<Void> jobStatsDialog = new javafx.scene.control.Dialog<>();
            jobStatsDialog.setTitle("职位统计");
            jobStatsDialog.setHeaderText("职位统计数据");
            jobStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建统计内容
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
            
            // 添加统计信息
            javafx.scene.control.Label totalJobsLabel = new javafx.scene.control.Label("总职位数: " + totalJobs);
            totalJobsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalJobsLabel);
            
            javafx.scene.control.Label publishedCountLabel = new javafx.scene.control.Label("已发布职位数: " + publishedCount);
            publishedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(publishedCountLabel);
            
            javafx.scene.control.Label pendingCountLabel = new javafx.scene.control.Label("待审核职位数: " + pendingCount);
            pendingCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(pendingCountLabel);
            
            javafx.scene.control.Label closedCountLabel = new javafx.scene.control.Label("已关闭职位数: " + closedCount);
            closedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(closedCountLabel);
            
            javafx.scene.control.Label rejectedCountLabel = new javafx.scene.control.Label("已拒绝职位数: " + rejectedCount);
            rejectedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(rejectedCountLabel);
            
            // 设置对话框内容
            jobStatsDialog.getDialogPane().setContent(statsContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            jobStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) jobStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            jobStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button applicationStatsBtn = new javafx.scene.control.Button("申请统计");
        applicationStatsBtn.setPrefWidth(200);
        applicationStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        applicationStatsBtn.setOnMouseEntered(mouseEvent -> applicationStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        applicationStatsBtn.setOnMouseExited(mouseEvent -> applicationStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        applicationStatsBtn.setOnAction(e -> {
            // 实现申请统计功能
            javafx.scene.control.Dialog<Void> applicationStatsDialog = new javafx.scene.control.Dialog<>();
            applicationStatsDialog.setTitle("申请统计");
            applicationStatsDialog.setHeaderText("申请统计数据");
            applicationStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建统计内容
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
            
            // 添加统计信息
            javafx.scene.control.Label totalApplicationsLabel = new javafx.scene.control.Label("总申请数: " + totalApplications);
            totalApplicationsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(totalApplicationsLabel);
            
            javafx.scene.control.Label pendingCountLabel = new javafx.scene.control.Label("待处理申请数: " + pendingCount);
            pendingCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(pendingCountLabel);
            
            javafx.scene.control.Label acceptedCountLabel = new javafx.scene.control.Label("已接受申请数: " + acceptedCount);
            acceptedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(acceptedCountLabel);
            
            javafx.scene.control.Label rejectedCountLabel = new javafx.scene.control.Label("已拒绝申请数: " + rejectedCount);
            rejectedCountLabel.setStyle("-fx-font-size: 14px;");
            statsContent.getChildren().add(rejectedCountLabel);
            
            // 设置对话框内容
            applicationStatsDialog.getDialogPane().setContent(statsContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            applicationStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) applicationStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            applicationStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button recruitmentStatsBtn = new javafx.scene.control.Button("招聘效果分析");
        recruitmentStatsBtn.setPrefWidth(200);
        recruitmentStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        recruitmentStatsBtn.setOnMouseEntered(mouseEvent -> recruitmentStatsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        recruitmentStatsBtn.setOnMouseExited(mouseEvent -> recruitmentStatsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        recruitmentStatsBtn.setOnAction(e -> {
            // 实现招聘效果分析功能
            javafx.scene.control.Dialog<Void> recruitmentStatsDialog = new javafx.scene.control.Dialog<>();
            recruitmentStatsDialog.setTitle("招聘效果分析");
            recruitmentStatsDialog.setHeaderText("招聘效果分析");
            recruitmentStatsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建统计内容
            javafx.scene.layout.VBox statsContent = new javafx.scene.layout.VBox(15);
            statsContent.setPadding(new javafx.geometry.Insets(20));
            statsContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 获取数据
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
            
            // 添加统计信息
            javafx.scene.control.Label avgApplicationsLabel = new javafx.scene.control.Label("平均每个职位的申请数: " + String.format("%.2f", avgApplicationsPerJob));
            avgApplicationsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(avgApplicationsLabel);
            
            javafx.scene.control.Label acceptanceRateLabel = new javafx.scene.control.Label("申请通过率: " + String.format("%.2f%%", acceptanceRate));
            acceptanceRateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            statsContent.getChildren().add(acceptanceRateLabel);
            
            // 设置对话框内容
            recruitmentStatsDialog.getDialogPane().setContent(statsContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            recruitmentStatsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) recruitmentStatsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            recruitmentStatsDialog.showAndWait();
        });
        
        javafx.scene.control.Button dataExportBtn = new javafx.scene.control.Button("数据导出");
        dataExportBtn.setPrefWidth(200);
        dataExportBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        dataExportBtn.setOnMouseEntered(mouseEvent -> dataExportBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        dataExportBtn.setOnMouseExited(mouseEvent -> dataExportBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        dataExportBtn.setOnAction(e -> {
            // 实现数据导出功能
            javafx.scene.control.Dialog<Void> dataExportDialog = new javafx.scene.control.Dialog<>();
            dataExportDialog.setTitle("数据导出");
            dataExportDialog.setHeaderText("导出数据");
            dataExportDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // 创建导出选项
            javafx.scene.layout.VBox exportContent = new javafx.scene.layout.VBox(15);
            exportContent.setPadding(new javafx.geometry.Insets(20));
            exportContent.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // 添加导出选项
            javafx.scene.control.CheckBox usersCheckBox = new javafx.scene.control.CheckBox("导出用户数据");
            usersCheckBox.setStyle("-fx-font-size: 14px;");
            
            javafx.scene.control.CheckBox jobsCheckBox = new javafx.scene.control.CheckBox("导出职位数据");
            jobsCheckBox.setStyle("-fx-font-size: 14px;");
            
            javafx.scene.control.CheckBox applicationsCheckBox = new javafx.scene.control.CheckBox("导出申请数据");
            applicationsCheckBox.setStyle("-fx-font-size: 14px;");
            
            // 添加到VBox
            exportContent.getChildren().addAll(usersCheckBox, jobsCheckBox, applicationsCheckBox);
            
            // 创建导出按钮
            javafx.scene.control.Button exportBtn = new javafx.scene.control.Button("开始导出");
            exportBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            exportBtn.setOnMouseEntered(mouseEvent -> exportBtn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            exportBtn.setOnMouseExited(mouseEvent -> exportBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            exportBtn.setOnAction(exportEvent -> {
                // 模拟导出过程
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText("导出成功");
                successAlert.setContentText("数据已成功导出到指定位置");
                successAlert.showAndWait();
            });
            
            // 添加导出按钮
            exportContent.getChildren().add(exportBtn);
            
            // 设置对话框内容
            dataExportDialog.getDialogPane().setContent(exportContent);
            
            // 添加确定按钮
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("确定", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dataExportDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // 美化确定按钮
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) dataExportDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // 显示对话框
            dataExportDialog.showAndWait();
        });
        
        // 添加按钮到VBox
        vbox.getChildren().addAll(userStatsBtn, jobStatsBtn, applicationStatsBtn, recruitmentStatsBtn, dataExportBtn);

        // 设置对话框内容
        dialog.getDialogPane().setContent(vbox);

        // 添加确定按钮
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);

        // 显示对话框
        dialog.showAndWait();
    }

    // 查看所有职位需求
    @FXML
    private void handleViewAllJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(user, model.UserRole.ADMIN);

            // 获取当前舞台
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
            stage.setTitle("BUPT国际学校TA招聘系统 - 职位需求");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("职位列表页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // 发布新职位
    @FXML
    private void handleCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setAdminUser(user);

            // 获取当前舞台
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
            stage.setTitle("BUPT国际学校TA招聘系统 - 发布职位");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("发布职位页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}