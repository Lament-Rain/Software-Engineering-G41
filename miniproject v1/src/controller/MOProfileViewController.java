package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.MO;
import service.UserService;

import java.util.Optional;

public class MOProfileViewController {
    @FXML private Label nameLabel;
    @FXML private Label departmentLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;

    private MO user;
    private Stage stage;

    public void setUser(MO user) {
        this.user = user;
        loadUserData();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loadUserData() {
        if (user == null) return;
        nameLabel.setText(safe(user.getName()));
        departmentLabel.setText(safe(user.getDepartment()));
        usernameLabel.setText(safe(user.getUsername()));
        roleLabel.setText(user.getRole() == null ? "-" : user.getRole().name());
        emailLabel.setText(safe(user.getEmail()));
        phoneLabel.setText(safe(user.getPhone()));
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        if (user == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Personal Basic Information");
        dialog.setHeaderText("Update your profile");

        TextField nameField = new TextField(safeEditable(user.getName()));
        TextField departmentField = new TextField(safeEditable(user.getDepartment()));
        TextField emailField = new TextField(safeEditable(user.getEmail()));
        TextField phoneField = new TextField(safeEditable(user.getPhone()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department"), 0, 1);
        grid.add(departmentField, 1, 1);
        grid.add(new Label("Email"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone"), 0, 3);
        grid.add(phoneField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveBtn) {
            user.setName(nameField.getText().trim());
            user.setDepartment(departmentField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            UserService.updateUser(user);
            loadUserData();
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        openDashboard(event);
    }

    @FXML
    private void handleJobManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewMyJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOMyJobs.fxml"));
            Parent root = loader.load();
            MOMyJobsController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
            Parent root = loader.load();
            MOApplicationReviewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application Review");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePersonalCenter(ActionEvent event) {
    }

    @FXML
    private void handleLogout(ActionEvent event) {
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
            e.printStackTrace();
        }
    }

    private void openDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safe(String v) {
        return v == null || v.isBlank() ? "-" : v;
    }

    private String safeEditable(String v) {
        return v == null || "-".equals(v) ? "" : v;
    }
}
