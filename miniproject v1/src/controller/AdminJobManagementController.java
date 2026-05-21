package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Admin;
import model.Job;
import model.JobType;
import model.JobStatus;
import service.DataStorage;
import service.JobService;

import java.util.List;
import java.util.Optional;

public class AdminJobManagementController {
    @FXML private TableView<Job> jobTable;
    @FXML private TableColumn<Job, String> titleCol;
    @FXML private TableColumn<Job, String> deptCol;
    @FXML private TableColumn<Job, JobStatus> statusCol;
    @FXML private TableColumn<Job, String> deadlineCol;

    private Admin user;
    private Stage stage;

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        jobTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Job selected = jobTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showJobDetailDialog(selected);
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
    private void handleUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminUserManagement.fxml"));
            Parent root = loader.load();
            AdminUserManagementController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Failed to open user management: " + e.getMessage());
        }
    }

    @FXML
    private void handleJobManagement() { refreshTable(); }

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
        } catch (Exception e) {
            showError("Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    @FXML
    private void handleReviewSelected() {
        Job selected = jobTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a job first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getReviewComment() == null ? "" : selected.getReviewComment());
        dialog.setTitle("Review Selected Job");
        dialog.setHeaderText("Review: " + selected.getTitle());
        dialog.setContentText("Review comment:");

        Optional<String> commentInput = dialog.showAndWait();
        if (!commentInput.isPresent()) {
            return;
        }

        String comment = commentInput.get().trim().isEmpty() ? "Reviewed in Admin Job Management" : commentInput.get().trim();
        JobStatus next = selected.getStatus() == JobStatus.PUBLISHED ? JobStatus.REJECTED : JobStatus.PUBLISHED;
        boolean ok = JobService.reviewJob(selected.getId(), next, comment, user == null ? "admin" : user.getId());
        if (!ok) {
            showError("Failed to review selected job.");
            return;
        }
        refreshTable();
    }

    @FXML
    private void handleEditSelected() {
        Job selected = jobTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a job first.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Selected Job");
        dialog.setHeaderText("Edit: " + selected.getTitle());

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField titleField = new TextField(safeValue(selected.getTitle()));
        TextField deptField = new TextField(safeValue(selected.getDepartment()));
        ComboBox<JobType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(JobType.values());
        typeBox.setValue(selected.getType() == null ? JobType.OTHER : selected.getType());
        TextField workTimeField = new TextField(safeValue(selected.getWorkTime()));
        TextField recruitNumField = new TextField(String.valueOf(selected.getRecruitNum()));
        TextField deadlineField = new TextField(safeValue(selected.getDeadline()));
        TextField salaryField = new TextField(safeValue(selected.getSalary()));
        TextField locationField = new TextField(safeValue(selected.getLocation()));
        TextArea skillsArea = new TextArea(selected.getSkills() == null ? "" : String.join(",", selected.getSkills()));
        skillsArea.setPrefRowCount(2);
        TextArea descriptionArea = new TextArea(safeValue(selected.getDescription()));
        descriptionArea.setPrefRowCount(4);
        TextArea extraReqArea = new TextArea(safeValue(selected.getExtraRequirements()));
        extraReqArea.setPrefRowCount(3);

        int r = 0;
        grid.add(new Label("Title:"), 0, r); grid.add(titleField, 1, r++);
        grid.add(new Label("Department:"), 0, r); grid.add(deptField, 1, r++);
        grid.add(new Label("Type:"), 0, r); grid.add(typeBox, 1, r++);
        grid.add(new Label("Work Time:"), 0, r); grid.add(workTimeField, 1, r++);
        grid.add(new Label("Recruit Number:"), 0, r); grid.add(recruitNumField, 1, r++);
        grid.add(new Label("Deadline:"), 0, r); grid.add(deadlineField, 1, r++);
        grid.add(new Label("Salary:"), 0, r); grid.add(salaryField, 1, r++);
        grid.add(new Label("Location:"), 0, r); grid.add(locationField, 1, r++);
        grid.add(new Label("Skills (comma-separated):"), 0, r); grid.add(skillsArea, 1, r++);
        grid.add(new Label("Description:"), 0, r); grid.add(descriptionArea, 1, r++);
        grid.add(new Label("Extra Requirements:"), 0, r); grid.add(extraReqArea, 1, r++);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (!result.isPresent() || result.get() != saveType) {
            return;
        }

        int recruitNum;
        try {
            recruitNum = Integer.parseInt(recruitNumField.getText().trim());
            if (recruitNum < 0) {
                showError("Recruit number must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Recruit number must be a valid integer.");
            return;
        }

        selected.setTitle(titleField.getText().trim());
        selected.setDepartment(deptField.getText().trim());
        selected.setType(typeBox.getValue());
        selected.setWorkTime(workTimeField.getText().trim());
        selected.setRecruitNum(recruitNum);
        selected.setDeadline(deadlineField.getText().trim());
        selected.setSalary(salaryField.getText().trim());
        selected.setLocation(locationField.getText().trim());
        selected.setDescription(descriptionArea.getText().trim());
        selected.setExtraRequirements(extraReqArea.getText().trim());

        String skillsRaw = skillsArea.getText() == null ? "" : skillsArea.getText().trim();
        if (skillsRaw.isEmpty()) {
            selected.setSkills(new java.util.ArrayList<>());
        } else {
            java.util.List<String> skills = java.util.Arrays.stream(skillsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
            selected.setSkills(skills);
        }

        boolean ok = JobService.updateJob(selected);
        if (!ok) {
            showError("Failed to save job changes.");
            return;
        }
        refreshTable();
    }

    @FXML
    private void handleToggleSelected() {
        Job selected = jobTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        JobStatus next = selected.getStatus() == JobStatus.CLOSED ? JobStatus.PUBLISHED : JobStatus.CLOSED;

        selected.setStatus(next);
        boolean ok = JobService.updateJob(selected);
        if (!ok) {
            ok = JobService.reviewJob(selected.getId(), next, "Status toggled in Admin Job Management", user == null ? "admin" : user.getId());
        }
        if (!ok) {
            showError("Failed to update job status.");
            return;
        }
        refreshTable();
        jobTable.refresh();
    }

    @FXML
    private void handleDeleteSelected() {
        Job selected = jobTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        List<Job> jobs = DataStorage.getJobs();
        if (jobs.removeIf(j -> j.getId().equals(selected.getId()))) {
            DataStorage.saveJobs(jobs);
            refreshTable();
        }
    }

    private void showJobDetailDialog(Job selected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Job Details");
        dialog.setHeaderText(selected.getTitle());

        TextArea details = new TextArea();
        details.setEditable(false);
        details.setWrapText(true);
        details.setPrefHeight(320);
        details.setText(
                "Department: " + safe(selected.getDepartment()) + "\n" +
                "Type: " + (selected.getType() == null ? "-" : selected.getType()) + "\n" +
                "Status: " + (selected.getStatus() == null ? "-" : selected.getStatus()) + "\n" +
                "Deadline: " + safe(selected.getDeadline()) + "\n" +
                "Work Time: " + safe(selected.getWorkTime()) + "\n" +
                "Recruit Number: " + selected.getRecruitNum() + "\n" +
                "Salary: " + safe(selected.getSalary()) + "\n" +
                "Location: " + safe(selected.getLocation()) + "\n" +
                "Skills: " + (selected.getSkills() == null ? "-" : String.join(", ", selected.getSkills())) + "\n\n" +
                "Description:\n" + safe(selected.getDescription()) + "\n\n" +
                "Extra Requirements:\n" + safe(selected.getExtraRequirements())
        );

        dialog.getDialogPane().setContent(details);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }

    private String safeValue(String value) {
        return value == null ? "" : value;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void refreshTable() {
        jobTable.setItems(FXCollections.observableArrayList(JobService.getAllJobs()));
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

