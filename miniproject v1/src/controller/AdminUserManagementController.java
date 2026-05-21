package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Admin;
import model.TA;
import model.User;
import model.UserRole;
import model.UserStatus;
import service.DataStorage;
import service.UserService;

import java.util.List;
import java.util.Optional;

public class AdminUserManagementController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> phoneCol;
    @FXML private TableColumn<User, UserRole> roleCol;
    @FXML private TableColumn<User, UserStatus> statusCol;

    private Admin user;
    private Stage stage;

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        userTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selected = userTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showUserProfileDialog(selected);
                }
            }
        });

        refreshTable();
    }

    public void setUser(Admin user) {
        this.user = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleBack() { openDashboard(); }
    @FXML
    private void handleHome() { openDashboard(); }
    @FXML
    private void handleUserManagement() { refreshTable(); }

    @FXML
    private void handleJobManagement() {
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
            showError("Failed to open job management: " + e.getMessage());
        }
    }

    @FXML
    private void handleApprovalCenter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminApproval.fxml"));
            Parent root = loader.load();
            AdminApprovalController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Approval Center");
        } catch (Exception e) {
            showError("Failed to open approval center: " + e.getMessage());
        }
    }

    @FXML
    private void handleSystemSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Settings");
        alert.setHeaderText("System Settings");
        alert.setContentText("Please use the Admin Dashboard for system settings.");
        alert.showAndWait();
    }

    @FXML
    private void handleAnalytics() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Analytics");
        alert.setHeaderText("Analytics");
        alert.setContentText("Please use the Admin Dashboard for analytics.");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            showError("Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { refreshTable(); }

    @FXML
    private void handleAddUser() {
        String seed = String.valueOf(System.currentTimeMillis() % 100000);
        User created = UserService.register("user" + seed, "Abc12345", "user" + seed + "@bupt.edu.cn", "13800138000", UserRole.TA, "Computer Science");
        if (created == null) { showError("Failed to add user"); return; }
        refreshTable();
    }

    @FXML
    private void handleEditSelected() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showUserProfileDialog(selected);
    }

    @FXML
    private void handleToggleSelected() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        UserStatus next = selected.getStatus() == UserStatus.ACTIVE ? UserStatus.LOCKED : UserStatus.ACTIVE;

        selected.setStatus(next);
        boolean ok = UserService.updateUser(selected);
        if (!ok) {
            ok = UserService.toggleUserStatus(selected.getId(), next);
        }
        if (!ok) {
            showError("Failed to update user status.");
            return;
        }
        refreshTable();
        userTable.refresh();
    }

    @FXML
    private void handleDeleteSelected() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        List<User> users = DataStorage.getUsers();
        if (users.removeIf(u -> u.getId().equals(selected.getId()))) {
            DataStorage.saveUsers(users);
            refreshTable();
        }
    }

    private void showUserProfileDialog(User selected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("User Profile");
        dialog.setHeaderText("Profile: " + selected.getUsername());

        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        ComboBox<UserStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(UserStatus.ACTIVE, UserStatus.LOCKED);
        statusBox.setValue(selected.getStatus());

        Label roleLabel = new Label("Role: " + selected.getRole());
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefHeight(320);

        if (selected instanceof TA ta) {
            String skills = (ta.getSkills() == null || ta.getSkills().isEmpty()) ? "-" : String.join("; ", ta.getSkills());
            detailsArea.setText(
                "[Personal Basic Information]\n" +
                "Name: " + safe(ta.getName()) + "\n" +
                "Gender: " + safe(ta.getGender()) + "\n" +
                "Year of Birth: " + (ta.getAge() == 0 ? "-" : ta.getAge()) + "\n" +
                "Department: " + safe(ta.getDepartment()) + "\n" +
                "Grade: " + safe(ta.getGrade()) + "\n" +
                "Student ID: " + safe(ta.getStudentId()) + "\n" +
                "Email: " + safe(ta.getEmail()) + "\n" +
                "Phone: " + safe(ta.getPhone()) + "\n\n" +
                "[Work Availability]\n" +
                "Available Work Time: " + safe(ta.getAvailableTime()) + "\n" +
                "Specialized Subjects/Skills: " + skills + "\n\n" +
                "[Professional Skills and Experience]\n" +
                "Previous TA Experience: " + safe(ta.getExperience()) + "\n" +
                "Awards: " + safe(ta.getAwards()) + "\n" +
                "Language Skills: " + safe(ta.getLanguageSkills()) + "\n" +
                "Other Special Skills: " + safe(ta.getOtherSkills()) + "\n\n" +
                "[Other]\n" +
                "Resume: " + safe(ta.getResumePath()) + "\n" +
                "Profile Status: " + (ta.getProfileStatus() == null ? "-" : ta.getProfileStatus().name()) + "\n" +
                "Profile Updated At: " + safe(ta.getProfileUpdatedAt()) + "\n" +
                "Review Comment: " + safe(ta.getProfileReviewComment())
            );
        } else {
            detailsArea.setText("Name: " + safe(selected.getUsername()) + "\nRole: " + selected.getRole() + "\nEmail: " + safe(selected.getEmail()) + "\nPhone: " + safe(selected.getPhone()));
        }

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusBox, 1, 2);
        grid.add(roleLabel, 0, 3, 2, 1);
        grid.add(new Label("Profile:"), 0, 4);
        grid.add(detailsArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveBtn) {
            selected.setEmail(emailField.getText().trim());
            selected.setPhone(phoneField.getText().trim());
            selected.setStatus(statusBox.getValue());
            UserService.updateUser(selected);
            refreshTable();
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void refreshTable() {
        userTable.setItems(FXCollections.observableArrayList(UserService.getAllUsers()));
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
        } catch (Exception e) {
            showError("Failed to open dashboard: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

