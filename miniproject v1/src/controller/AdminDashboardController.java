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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("Admin Configuration");
        alert.setContentText("Admin configuration feature is not implemented yet");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("Bulk Operations");
        alert.setContentText("Bulk operations feature is not implemented yet");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // AI workload balancing
    @FXML
    private void handleAIWorkloadBalancing(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("AI Workload Balancing");
        alert.setContentText("AI workload balancing feature is not implemented yet");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
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
        // Create user management dialog
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("User Management");
        dialog.setHeaderText("User management");
        
        // Create button layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // Add feature buttons
        javafx.scene.control.Button viewUsersBtn = new javafx.scene.control.Button("View All Users");
        viewUsersBtn.setPrefWidth(200);
        viewUsersBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        viewUsersBtn.setOnMouseEntered(mouseEvent -> viewUsersBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        viewUsersBtn.setOnMouseExited(mouseEvent -> viewUsersBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        viewUsersBtn.setOnAction(e -> {
            // Implement view all users feature
            javafx.scene.control.Dialog<Void> viewUsersDialog = new javafx.scene.control.Dialog<>();
            viewUsersDialog.setTitle("View All Users");
            viewUsersDialog.setHeaderText("All Users List");
            viewUsersDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("Username");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("Email");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> phoneCol = new javafx.scene.control.TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
            phoneCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserRole> roleCol = new javafx.scene.control.TableColumn<>("Role");
            roleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("role"));
            roleCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserStatus> statusCol = new javafx.scene.control.TableColumn<>("Status");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            userTable.getColumns().addAll(usernameCol, emailCol, phoneCol, roleCol, statusCol);
            
            // Load user data
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // Set table to resizable columns
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create scroll pane
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(userTable);
            scrollPane.setPrefHeight(400);
            scrollPane.setPrefWidth(600);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Set dialog content
            viewUsersDialog.getDialogPane().setContent(scrollPane);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            viewUsersDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) viewUsersDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            viewUsersDialog.showAndWait();
        });
        
        javafx.scene.control.Button addUserBtn = new javafx.scene.control.Button("Add New User");
        addUserBtn.setPrefWidth(200);
        addUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        addUserBtn.setOnMouseEntered(mouseEvent -> addUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        addUserBtn.setOnMouseExited(mouseEvent -> addUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        addUserBtn.setOnAction(e -> {
            // Implement add new user feature
            javafx.scene.control.Dialog<java.util.Map<String, Object>> addUserDialog = new javafx.scene.control.Dialog<>();
addUserDialog.setTitle("Add New User");
addUserDialog.setHeaderText("Add New User");
            addUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create form
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            // Add form elements
            javafx.scene.control.Label usernameLabel = new javafx.scene.control.Label("Username:");
            usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField usernameField = new javafx.scene.control.TextField();
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label passwordLabel = new javafx.scene.control.Label("Password:");
            passwordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("Email:");
            emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
            emailField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label phoneLabel = new javafx.scene.control.Label("Phone:");
            phoneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
            phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("Role:");
            roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.ComboBox<model.UserRole> roleComboBox = new javafx.scene.control.ComboBox<>();
            roleComboBox.getItems().addAll(model.UserRole.TA, model.UserRole.MO, model.UserRole.ADMIN);
            roleComboBox.setValue(model.UserRole.TA);
            roleComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            javafx.scene.control.Label departmentLabel = new javafx.scene.control.Label("Department:");
            departmentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            javafx.scene.control.TextField departmentField = new javafx.scene.control.TextField();
            departmentField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Add to grid
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
            
            // Set dialog content
            addUserDialog.getDialogPane().setContent(grid);
            
            // Add buttons
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            addUserDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            
            // Style buttons
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) addUserDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) addUserDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Set result converter
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
            
            // Show dialog and handle result
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
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("User added successfully");
                    successAlert.setContentText("User " + user.getUsername() + " was added successfully");
                    successAlert.showAndWait();
                } else {
                    javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to add user");
                    errorAlert.setContentText("Please check whether the input information is correct");
                    errorAlert.showAndWait();
                }
            });
        });
        
        javafx.scene.control.Button editUserBtn = new javafx.scene.control.Button("Edit User Information");
        editUserBtn.setPrefWidth(200);
        editUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        editUserBtn.setOnMouseEntered(mouseEvent -> editUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        editUserBtn.setOnMouseExited(mouseEvent -> editUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        editUserBtn.setOnAction(e -> {
            // Implement edit user information feature
            javafx.scene.control.Dialog<Void> editUserDialog = new javafx.scene.control.Dialog<>();
            editUserDialog.setTitle("Edit User Information");
            editUserDialog.setHeaderText("Select a user to edit");
            editUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #90caf9; -fx-selection-bar-text: #000000; -fx-cell-focus-inner-border: #90caf9; -fx-focus-color: #90caf9;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("Username");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("Email");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> phoneCol = new javafx.scene.control.TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
            phoneCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            userTable.getColumns().addAll(usernameCol, emailCol, phoneCol);
            
            // Load user data
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // Set table to resizable columns
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Set selection mode to single row selection
            userTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Create edit button
            javafx.scene.control.Button editBtn = new javafx.scene.control.Button("Edit Selected User");
            editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            editBtn.setOnMouseEntered(mouseEvent -> editBtn.setStyle("-fx-background-color: #e68a00; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnMouseExited(mouseEvent -> editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnAction(editEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // Create edit form
                    javafx.scene.control.Dialog<java.util.Map<String, Object>> editFormDialog = new javafx.scene.control.Dialog<>();
                    editFormDialog.setTitle("Edit User Information");
                    editFormDialog.setHeaderText("Edit User: " + selectedUser.getUsername());
                    editFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // Create form
                    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                    grid.setHgap(15);
                    grid.setVgap(15);
                    grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
                    grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // Add form elements
                    javafx.scene.control.Label usernameLabel = new javafx.scene.control.Label("Username:");
                    usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField usernameField = new javafx.scene.control.TextField(selectedUser.getUsername());
                    usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("Email:");
                    emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField emailField = new javafx.scene.control.TextField(selectedUser.getEmail());
                    emailField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label phoneLabel = new javafx.scene.control.Label("Phone:");
                    phoneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField(selectedUser.getPhone());
                    phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // Add to grid
                    grid.add(usernameLabel, 0, 0);
                    grid.add(usernameField, 1, 0);
                    grid.add(emailLabel, 0, 1);
                    grid.add(emailField, 1, 1);
                    grid.add(phoneLabel, 0, 2);
                    grid.add(phoneField, 1, 2);
                    
                    // Set dialog content
                    editFormDialog.getDialogPane().setContent(grid);
                    
                    // Add buttons
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    editFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // Style buttons
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // Set result converter
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
                    
                    // Show dialog and handle result
                    java.util.Optional<java.util.Map<String, Object>> result = editFormDialog.showAndWait();
                    result.ifPresent(data -> {
                        selectedUser.setUsername((String) data.get("username"));
                        selectedUser.setEmail((String) data.get("email"));
                        selectedUser.setPhone((String) data.get("phone"));
                        
                        boolean success = service.UserService.updateUser(selectedUser);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("User information updated successfully");
                            successAlert.setContentText("User information has been updated successfully");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Failed to edit user");
                            errorAlert.setContentText("Failed to update user information");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No user selected");
                    alert.setContentText("Please select a user to edit first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, editBtn);
            
            // Set dialog content
            editUserDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            editUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            editUserDialog.showAndWait();
        });
        
        javafx.scene.control.Button disableUserBtn = new javafx.scene.control.Button("Enable/Disable User");
        disableUserBtn.setPrefWidth(200);
        disableUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        disableUserBtn.setOnMouseEntered(mouseEvent -> disableUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        disableUserBtn.setOnMouseExited(mouseEvent -> disableUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        disableUserBtn.setOnAction(e -> {
            // Implement enable/disable user feature
            javafx.scene.control.Dialog<Void> toggleUserDialog = new javafx.scene.control.Dialog<>();
            toggleUserDialog.setTitle("Enable/Disable User");
            toggleUserDialog.setHeaderText("Select a user to manage");
            toggleUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("Username");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, model.UserStatus> statusCol = new javafx.scene.control.TableColumn<>("Status");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            userTable.getColumns().addAll(usernameCol, statusCol);
            
            // Load user data
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // Set table to resizable columns
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create toggle status button
            javafx.scene.control.Button toggleBtn = new javafx.scene.control.Button("Toggle Selected User Status");
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
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Operation successful");
                        successAlert.setContentText("User status has been switched to " + newStatus);
                        successAlert.showAndWait();
                        
                        // Refresh table
                        userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                    } else {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Operation failed");
                        errorAlert.setContentText("Failed to switch user status");
                        errorAlert.showAndWait();
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No user selected");
                    alert.setContentText("Please select a user to operate on first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, toggleBtn);
            
            // Set dialog content
            toggleUserDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            toggleUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            toggleUserDialog.showAndWait();
        });
        
        javafx.scene.control.Button deleteUserBtn = new javafx.scene.control.Button("Delete User");
        deleteUserBtn.setPrefWidth(200);
        deleteUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        deleteUserBtn.setOnMouseEntered(mouseEvent -> deleteUserBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        deleteUserBtn.setOnMouseExited(mouseEvent -> deleteUserBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        deleteUserBtn.setOnAction(e -> {
            // Implement delete user feature
            javafx.scene.control.Dialog<Void> deleteUserDialog = new javafx.scene.control.Dialog<>();
            deleteUserDialog.setTitle("Delete User");
            deleteUserDialog.setHeaderText("Select a user to delete");
            deleteUserDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.User> userTable = new javafx.scene.control.TableView<>();
            userTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.User, String> usernameCol = new javafx.scene.control.TableColumn<>("Username");
            usernameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
            usernameCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.User, String> emailCol = new javafx.scene.control.TableColumn<>("Email");
            emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
            emailCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            userTable.getColumns().addAll(usernameCol, emailCol);
            
            // Load user data
            java.util.List<model.User> users = service.UserService.getAllUsers();
            userTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
            
            // Set table to resizable columns
            userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create delete button
            javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("Delete Selected User");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            deleteBtn.setOnMouseEntered(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnMouseExited(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnAction(deleteEvent -> {
                model.User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // Confirm deletion
                    javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Deletion");
                    confirmAlert.setHeaderText("Delete User");
                    confirmAlert.setContentText("Are you sure you want to delete user " + selectedUser.getUsername() + "?");
                    
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        // Delete user from data storage
                        java.util.List<model.User> allUsers = service.DataStorage.getUsers();
                        boolean removed = allUsers.removeIf(user -> user.getId().equals(selectedUser.getId()));
                        if (removed) {
                            service.DataStorage.saveUsers(allUsers);
                            service.DataStorage.addLog("DELETE_USER", "admin", "User deleted: " + selectedUser.getUsername());
                            
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("User deleted successfully");
                            successAlert.setContentText("The user has been deleted successfully");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            userTable.setItems(javafx.collections.FXCollections.observableArrayList(service.UserService.getAllUsers()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Failed to delete user");
                            errorAlert.setContentText("Failed to delete user");
                            errorAlert.showAndWait();
                        }
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No user selected");
                    alert.setContentText("Please select a user to delete first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(userTable, deleteBtn);
            
            // Set dialog content
            deleteUserDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            deleteUserDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            deleteUserDialog.showAndWait();
        });
        
        // Add buttons to VBox
        vbox.getChildren().addAll(viewUsersBtn, addUserBtn, editUserBtn, disableUserBtn, deleteUserBtn);
        
        // Set dialog content
        dialog.getDialogPane().setContent(vbox);
        
        // Add confirm button
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        
        // Show dialog
        dialog.showAndWait();
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
        // Create job management dialog
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Job Management");
        dialog.setHeaderText("Job Management Features");
        
        // Create button layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        
        // Add feature buttons
        javafx.scene.control.Button viewJobsBtn = new javafx.scene.control.Button("View All Jobs");
        viewJobsBtn.setPrefWidth(200);
        viewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        viewJobsBtn.setOnMouseEntered(mouseEvent -> viewJobsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        viewJobsBtn.setOnMouseExited(mouseEvent -> viewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        viewJobsBtn.setOnAction(e -> {
            // Implement view all jobs feature
            javafx.scene.control.Dialog<Void> viewJobsDialog = new javafx.scene.control.Dialog<>();
            viewJobsDialog.setTitle("View All Jobs");
            viewJobsDialog.setHeaderText("All Jobs List");
            viewJobsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("Job Title");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobType> typeCol = new javafx.scene.control.TableColumn<>("Job Type");
            typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
            typeCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("Department");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobStatus> statusCol = new javafx.scene.control.TableColumn<>("Status");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            jobTable.getColumns().addAll(titleCol, typeCol, departmentCol, statusCol);
            
            // Load job data
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // Set table to resizable columns
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create scroll pane
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setContent(jobTable);
            scrollPane.setPrefHeight(400);
            scrollPane.setPrefWidth(600);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px;");
            
            // Set dialog content
            viewJobsDialog.getDialogPane().setContent(scrollPane);
            
            // Add confirm button
            javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            viewJobsDialog.getDialogPane().getButtonTypes().add(okButtonType);
            
            // Style confirm button
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) viewJobsDialog.getDialogPane().lookupButton(okButtonType);
            okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            
            // Show dialog
            viewJobsDialog.showAndWait();
        });
        
        javafx.scene.control.Button reviewJobsBtn = new javafx.scene.control.Button("Review Job Postings");
        reviewJobsBtn.setPrefWidth(200);
        reviewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        reviewJobsBtn.setOnMouseEntered(mouseEvent -> reviewJobsBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        reviewJobsBtn.setOnMouseExited(mouseEvent -> reviewJobsBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        reviewJobsBtn.setOnAction(e -> {
            // Implement review job posting feature
            javafx.scene.control.Dialog<Void> reviewJobsDialog = new javafx.scene.control.Dialog<>();
            reviewJobsDialog.setTitle("Review Job Postings");
            reviewJobsDialog.setHeaderText("Pending Job Review List");
            reviewJobsDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("Job Title");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("Department");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
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
            
            // Set table to resizable columns
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create review button
            javafx.scene.control.Button reviewBtn = new javafx.scene.control.Button("Review Selected Job");
            reviewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            reviewBtn.setOnMouseEntered(mouseEvent -> reviewBtn.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            reviewBtn.setOnMouseExited(mouseEvent -> reviewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            reviewBtn.setOnAction(reviewEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // 创建审核表单
                    javafx.scene.control.Dialog<model.JobStatus> reviewFormDialog = new javafx.scene.control.Dialog<>();
                    reviewFormDialog.setTitle("Review Job");
                    reviewFormDialog.setHeaderText("Review Job: " + selectedJob.getTitle());
                    reviewFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // Create form
                    javafx.scene.layout.VBox vbox1 = new javafx.scene.layout.VBox(15);
                    vbox1.setPadding(new javafx.geometry.Insets(20));
                    vbox1.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // 添加审核选项
                    javafx.scene.control.Label statusLabel = new javafx.scene.control.Label("Review Result:");
                    statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.ToggleGroup toggleGroup = new javafx.scene.control.ToggleGroup();
                    
                    javafx.scene.control.RadioButton approveBtn = new javafx.scene.control.RadioButton("Approved");
                    approveBtn.setToggleGroup(toggleGroup);
                    approveBtn.setSelected(true);
                    approveBtn.setStyle("-fx-font-size: 14px;");
                    
                    javafx.scene.control.RadioButton rejectBtn = new javafx.scene.control.RadioButton("Rejected");
                    rejectBtn.setToggleGroup(toggleGroup);
                    rejectBtn.setStyle("-fx-font-size: 14px;");
                    
                    javafx.scene.control.Label commentLabel = new javafx.scene.control.Label("Review comments:");
                    commentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextArea commentField = new javafx.scene.control.TextArea();
                    commentField.setPrefHeight(100);
                    commentField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // Add to VBox
                    vbox1.getChildren().addAll(statusLabel, approveBtn, rejectBtn, commentLabel, commentField);
                    
                    // Set dialog content
                    reviewFormDialog.getDialogPane().setContent(vbox1);
                    
                    // Add buttons
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    reviewFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // Style buttons
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) reviewFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) reviewFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // Set result converter
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
                    
                    // Show dialog and handle result
                    java.util.Optional<model.JobStatus> result = reviewFormDialog.showAndWait();
                    result.ifPresent(status -> {
                        boolean success = service.JobService.reviewJob(selectedJob.getId(), status, commentField.getText(), user.getId());
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("Review successful");
                            successAlert.setContentText("The job review has been completed");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            java.util.List<model.Job> updatedPendingJobs = new java.util.ArrayList<>();
                            for (model.Job job : service.JobService.getAllJobs()) {
                                if (job.getStatus() == model.JobStatus.PENDING) {
                                    updatedPendingJobs.add(job);
                                }
                            }
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(updatedPendingJobs));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Review failed");
                            errorAlert.setContentText("The job review failed");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No job selected");
                    alert.setContentText("Please select a job first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, reviewBtn);
            
            // Set dialog content
            reviewJobsDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            reviewJobsDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            reviewJobsDialog.showAndWait();
        });
        
        javafx.scene.control.Button editJobBtn = new javafx.scene.control.Button("Edit Job Information");
        editJobBtn.setPrefWidth(200);
        editJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        editJobBtn.setOnMouseEntered(mouseEvent -> editJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        editJobBtn.setOnMouseExited(mouseEvent -> editJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        editJobBtn.setOnAction(e -> {
            // 实现编辑职位信息功能
            javafx.scene.control.Dialog<Void> editJobDialog = new javafx.scene.control.Dialog<>();
            editJobDialog.setTitle("Edit Job Information");
            editJobDialog.setHeaderText("Select a job to edit");
            editJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("Job Title");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("Department");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            jobTable.getColumns().addAll(titleCol, departmentCol);
            
            // Load job data
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // Set table to resizable columns
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create edit button
            javafx.scene.control.Button editBtn = new javafx.scene.control.Button("Edit Selected Job");
            editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            editBtn.setOnMouseEntered(mouseEvent -> editBtn.setStyle("-fx-background-color: #e68a00; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnMouseExited(mouseEvent -> editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            editBtn.setOnAction(editEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // Create edit form
                    javafx.scene.control.Dialog<java.util.Map<String, Object>> editFormDialog = new javafx.scene.control.Dialog<>();
                    editFormDialog.setTitle("Edit Job Information");
                    editFormDialog.setHeaderText("Edit Job: " + selectedJob.getTitle());
                    editFormDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
                    
                    // Create form
                    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                    grid.setHgap(15);
                    grid.setVgap(15);
                    grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
                    grid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    
                    // Add form elements
                    javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Title:");
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextField titleField = new javafx.scene.control.TextField(selectedJob.getTitle());
                    titleField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    javafx.scene.control.Label descriptionLabel = new javafx.scene.control.Label("Description:");
                    descriptionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    javafx.scene.control.TextArea descriptionField = new javafx.scene.control.TextArea(selectedJob.getDescription());
                    descriptionField.setPrefHeight(100);
                    descriptionField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");
                    
                    // Add to grid
                    grid.add(titleLabel, 0, 0);
                    grid.add(titleField, 1, 0);
                    grid.add(descriptionLabel, 0, 1);
                    grid.add(descriptionField, 1, 1);
                    
                    // Set dialog content
                    editFormDialog.getDialogPane().setContent(grid);
                    
                    // Add buttons
                    javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    editFormDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                    
                    // Style buttons
                    javafx.scene.control.Button okButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(okButtonType);
                    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    okButton.setOnMouseEntered(mouseEvent -> okButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    okButton.setOnMouseExited(mouseEvent -> okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) editFormDialog.getDialogPane().lookupButton(cancelButtonType);
                    cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
                    cancelButton.setOnMouseEntered(mouseEvent -> cancelButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    cancelButton.setOnMouseExited(mouseEvent -> cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
                    
                    // Set result converter
                    editFormDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            java.util.Map<String, Object> result = new java.util.HashMap<>();
                            result.put("title", titleField.getText());
                            result.put("description", descriptionField.getText());
                            return result;
                        }
                        return null;
                    });
                    
                    // Show dialog and handle result
                    java.util.Optional<java.util.Map<String, Object>> result = editFormDialog.showAndWait();
                    result.ifPresent(data -> {
                        selectedJob.setTitle((String) data.get("title"));
                        selectedJob.setDescription((String) data.get("description"));
                        
                        boolean success = service.JobService.updateJob(selectedJob);
                        if (success) {
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("Edit successful");
                            successAlert.setContentText("The job information has been updated successfully");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Edit failed");
                            errorAlert.setContentText("Failed to update job information");
                            errorAlert.showAndWait();
                        }
                    });
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No job selected");
                    alert.setContentText("Please select the job to edit first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, editBtn);
            
            // Set dialog content
            editJobDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            editJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            editJobDialog.showAndWait();
        });
        
        javafx.scene.control.Button disableJobBtn = new javafx.scene.control.Button("Toggle Job Availability");
        disableJobBtn.setPrefWidth(200);
        disableJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        disableJobBtn.setOnMouseEntered(mouseEvent -> disableJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        disableJobBtn.setOnMouseExited(mouseEvent -> disableJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        disableJobBtn.setOnAction(e -> {
            // 实现禁用/启用职位功能
            javafx.scene.control.Dialog<Void> toggleJobDialog = new javafx.scene.control.Dialog<>();
            toggleJobDialog.setTitle("Toggle Job Availability");
            toggleJobDialog.setHeaderText("Select the job to manage");
            toggleJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("Job Title");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, model.JobStatus> statusCol = new javafx.scene.control.TableColumn<>("Status");
            statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
            statusCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            jobTable.getColumns().addAll(titleCol, statusCol);
            
            // Load job data
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // Set table to resizable columns
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create toggle status button
            javafx.scene.control.Button toggleBtn = new javafx.scene.control.Button("Toggle Selected Job Status");
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
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Operation successful");
                        successAlert.setContentText("Job status has been updated to " + newStatus);
                        successAlert.showAndWait();
                        
                        // Refresh table
                        jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                    } else {
                        javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Operation failed");
                        errorAlert.setContentText("Failed to update job status");
                        errorAlert.showAndWait();
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No job selected");
                    alert.setContentText("Please select the job to operate on first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, toggleBtn);
            
            // Set dialog content
            toggleJobDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            toggleJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            toggleJobDialog.showAndWait();
        });
        
        javafx.scene.control.Button deleteJobBtn = new javafx.scene.control.Button("Delete Job");
        deleteJobBtn.setPrefWidth(200);
        deleteJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        deleteJobBtn.setOnMouseEntered(mouseEvent -> deleteJobBtn.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"));
        deleteJobBtn.setOnMouseExited(mouseEvent -> deleteJobBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        deleteJobBtn.setOnAction(e -> {
            // Implement delete job feature
            javafx.scene.control.Dialog<Void> deleteJobDialog = new javafx.scene.control.Dialog<>();
            deleteJobDialog.setTitle("Delete Job");
            deleteJobDialog.setHeaderText("Select a job to delete");
            deleteJobDialog.getDialogPane().setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8px;");
            
            // Create table view
            javafx.scene.control.TableView<model.Job> jobTable = new javafx.scene.control.TableView<>();
            jobTable.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-selection-bar: #e3f2fd; -fx-selection-bar-text: #000000;");
            
            // Create columns
            javafx.scene.control.TableColumn<model.Job, String> titleCol = new javafx.scene.control.TableColumn<>("Job Title");
            titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
            titleCol.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            
            javafx.scene.control.TableColumn<model.Job, String> departmentCol = new javafx.scene.control.TableColumn<>("Department");
            departmentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("department"));
            departmentCol.setStyle("-fx-background-color: #f0f0f0;");
            
            // Add columns to table
            jobTable.getColumns().addAll(titleCol, departmentCol);
            
            // Load job data
            java.util.List<model.Job> jobs = service.JobService.getAllJobs();
            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(jobs));
            
            // Set table to resizable columns
            jobTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Create delete button
            javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("Delete Selected Job");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;");
            deleteBtn.setOnMouseEntered(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnMouseExited(mouseEvent -> deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 4px;"));
            deleteBtn.setOnAction(deleteEvent -> {
                model.Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
                if (selectedJob != null) {
                    // Confirm deletion
                    javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Deletion");
                    confirmAlert.setHeaderText("Delete Job");
                    confirmAlert.setContentText("Are you sure you want to delete job " + selectedJob.getTitle() + "?");
                    
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        // Delete job from data storage
                        java.util.List<model.Job> allJobs = service.DataStorage.getJobs();
                        boolean removed = allJobs.removeIf(job -> job.getId().equals(selectedJob.getId()));
                        if (removed) {
                            service.DataStorage.saveJobs(allJobs);
                            service.DataStorage.addLog("DELETE_JOB", "admin", "Job deleted: " + selectedJob.getTitle());
                            
                            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("Delete successful");
                            successAlert.setContentText("The job has been deleted successfully");
                            successAlert.showAndWait();
                            
                            // Refresh table
                            jobTable.setItems(javafx.collections.FXCollections.observableArrayList(service.JobService.getAllJobs()));
                        } else {
                            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Delete failed");
                            errorAlert.setContentText("Delete failed");
                            errorAlert.showAndWait();
                        }
                    }
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No job selected");
                    alert.setContentText("Please select the job to delete first");
                    alert.showAndWait();
                }
            });
            
            // Create VBox containing table and button
            javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(10);
            dialogContent.setPadding(new javafx.geometry.Insets(10));
            dialogContent.getChildren().addAll(jobTable, deleteBtn);
            
            // Set dialog content
            deleteJobDialog.getDialogPane().setContent(dialogContent);
            
            // Add confirm button
            deleteJobDialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
            
            // Show dialog
            deleteJobDialog.showAndWait();
        });
        
        // Add buttons to VBox
        vbox.getChildren().addAll(viewJobsBtn, reviewJobsBtn, editJobBtn, disableJobBtn, deleteJobBtn);

        // Set dialog content
        dialog.getDialogPane().setContent(vbox);

        // Add confirm button
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);

        // Show dialog
        dialog.showAndWait();
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