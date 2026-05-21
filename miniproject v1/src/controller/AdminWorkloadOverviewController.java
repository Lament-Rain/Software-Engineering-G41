package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Admin;
import model.TA;
import model.WorkloadOverviewRow;
import service.AdminConfigService;
import service.UserService;
import service.WorkloadNotificationService;
import service.WorkloadService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminWorkloadOverviewController {
    @FXML private TableView<WorkloadOverviewRow> workloadTable;
    @FXML private TableColumn<WorkloadOverviewRow, String> taNameCol;
    @FXML private TableColumn<WorkloadOverviewRow, String> departmentCol;
    @FXML private TableColumn<WorkloadOverviewRow, Integer> hoursCol;
    @FXML private TableColumn<WorkloadOverviewRow, String> bandCol;
    @FXML private TableColumn<WorkloadOverviewRow, String> commentCol;
    @FXML private TextArea pushMessageArea;
    @FXML private Label statusLabel;
    @FXML private CheckBox overloadOnlyCheckBox;

    private Admin user;
    private Stage stage;
    private List<WorkloadOverviewRow> allRows = new ArrayList<>();

    public void setUser(Admin user) { this.user = user; }
    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    private void initialize() {
        taNameCol.setCellValueFactory(new PropertyValueFactory<>("taName"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("workloadHours"));
        bandCol.setCellValueFactory(new PropertyValueFactory<>("workloadBand"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("aiComment"));

        workloadTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(WorkloadOverviewRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    return;
                }
                if ("OVERLOAD".equals(item.getWorkloadBand())) {
                    setStyle("-fx-background-color: #fee2e2;");
                } else {
                    setStyle("");
                }
            }
        });

        refreshData();
    }

    @FXML
    private void handleRefresh() {
        refreshData();
    }

    @FXML
    private void handleFilterChanged() {
        applyFilter();
    }

    @FXML
    private void handleUseAiComment() {
        WorkloadOverviewRow row = workloadTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            statusLabel.setText("Please select a TA row first.");
            return;
        }
        pushMessageArea.setText(row.getAiComment());
        statusLabel.setText("AI comment copied.");
    }

    @FXML
    private void handlePushToTa() {
        WorkloadOverviewRow row = workloadTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            statusLabel.setText("Please select a TA row first.");
            return;
        }
        if (!"OVERLOAD".equals(row.getWorkloadBand())) {
            statusLabel.setText("Push is allowed only for OVERLOAD TAs.");
            return;
        }

        String msg = pushMessageArea.getText() == null ? "" : pushMessageArea.getText().trim();
        if (msg.isEmpty()) {
            statusLabel.setText("Message cannot be empty.");
            return;
        }

        WorkloadNotificationService.pushNotification(row.getTaId(), msg);
        statusLabel.setText("Pushed to TA. It will appear on next login.");
    }

    @FXML
    private void handleHome() {
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
            showError("Failed to open admin dashboard: " + e.getMessage());
        }
    }

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
        } catch (Exception e) {
            showError("Failed to open job management: " + e.getMessage());
        }
    }

    @FXML
    private void handleSystemConfiguration() {
        handleHome();
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
        } catch (Exception e) {
            showError("Failed to open approval center: " + e.getMessage());
        }
    }

    @FXML
    private void handleStatisticsAnalysis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminStatisticsAnalysis.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Failed to open analytics: " + e.getMessage());
        }
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

    private void refreshData() {
        var cfg = AdminConfigService.loadConfig();

        allRows = UserService.getAllUsers().stream()
                .filter(u -> u instanceof TA)
                .map(u -> (TA) u)
                .map(ta -> {
                    int hours = WorkloadService.getCurrentWorkload(ta);
                    String band;
                    if (hours >= cfg.getHighThreshold() && hours < cfg.getMaxThreshold()) {
                        band = "OVERLOAD";
                    } else if (hours >= cfg.getMaxThreshold()) {
                        band = "MAX";
                    } else if (hours >= cfg.getMidThreshold()) {
                        band = "MEDIUM";
                    } else {
                        band = "LOW";
                    }
                    String comment = buildAiComment(hours, band, cfg.getHighThreshold(), cfg.getMaxThreshold());
                    return new WorkloadOverviewRow(
                            ta.getId(),
                            safe(ta.getName(), ta.getUsername()),
                            safe(ta.getDepartment(), "-"),
                            hours,
                            band,
                            comment
                    );
                })
                .sorted((a, b) -> Integer.compare(b.getWorkloadHours(), a.getWorkloadHours()))
                .collect(Collectors.toList());

        applyFilter();
        statusLabel.setText("Loaded " + allRows.size() + " TA records.");
    }

    private void applyFilter() {
        boolean overloadOnly = overloadOnlyCheckBox != null && overloadOnlyCheckBox.isSelected();
        List<WorkloadOverviewRow> filtered = allRows.stream()
                .filter(r -> !overloadOnly || "OVERLOAD".equals(r.getWorkloadBand()))
                .collect(Collectors.toList());
        workloadTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private String buildAiComment(int hours, String band, int high, int max) {
        if ("OVERLOAD".equals(band)) {
            return "Workload is high (" + hours + "h). This TA is above HIGH threshold (" + high + "h) and close to MAX (" + max + "h). Recommend reducing new commitments and prioritizing current accepted jobs.";
        }
        if ("MAX".equals(band)) {
            return "Workload reached/exceeded MAX threshold (" + max + "h). Strongly recommend pausing new assignments immediately.";
        }
        if ("MEDIUM".equals(band)) {
            return "Workload is moderate (" + hours + "h). Monitor weekly and avoid accepting too many concurrent tasks.";
        }
        return "Workload is healthy (" + hours + "h). This TA can take additional tasks if matching quality is strong.";
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
