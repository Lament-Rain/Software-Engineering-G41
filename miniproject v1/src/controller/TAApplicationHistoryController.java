package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Application;
import model.ApplicationHistoryViewModel;
import model.ApplicationStatus;
import model.Job;
import model.TA;
import service.ApplicationService;
import service.JobService;

import java.util.Comparator;
import java.util.List;

public class TAApplicationHistoryController {
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label activeApplicationsLabel;
    @FXML
    private Label resultSummaryLabel;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<String> departmentFilter;
    @FXML
    private TableView<ApplicationHistoryViewModel> applicationsTable;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> jobTitleColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> departmentColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> appliedAtColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> statusColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> reviewTimeColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> reviewCommentColumn;
    @FXML
    private TableColumn<ApplicationHistoryViewModel, String> matchScoreColumn;
    @FXML
    private Label detailTitleLabel;
    @FXML
    private Label detailStatusLabel;
    @FXML
    private Label detailAppliedAtLabel;
    @FXML
    private Label detailReviewTimeLabel;
    @FXML
    private Label detailMatchScoreLabel;
    @FXML
    private Label detailCommentLabel;
    @FXML
    private Button withdrawButton;

    private TA user;
    private Stage stage;

    @FXML
    private void initialize() {
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        appliedAtColumn.setCellValueFactory(new PropertyValueFactory<>("appliedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reviewTimeColumn.setCellValueFactory(new PropertyValueFactory<>("reviewTime"));
        reviewCommentColumn.setCellValueFactory(new PropertyValueFactory<>("reviewComment"));
        matchScoreColumn.setCellValueFactory(new PropertyValueFactory<>("matchScore"));

        statusFilter.getItems().addAll("ALL", "PENDING", "SCREENED", "ACCEPTED", "REJECTED", "WITHDRAWN");
        statusFilter.setValue("ALL");
        departmentFilter.getItems().add("ALL");
        departmentFilter.setValue("ALL");

        applicationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showDetails(newValue));
    }

    public void setUser(TA user) {
        this.user = user;
        refreshApplications();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void refreshApplications() {
        if (user == null) {
            return;
        }

        List<Application> applications = ApplicationService.getApplicationsByTA(user.getId());
        applications.sort(Comparator.comparing(Application::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed());

        departmentFilter.getItems().setAll("ALL");

        ObservableList<ApplicationHistoryViewModel> rows = FXCollections.observableArrayList();
        int activeCount = 0;
        int acceptedCount = 0;
        int rejectedCount = 0;

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            String jobTitle = job != null ? job.getTitle() : "Unknown Job";
            String department = job != null ? job.getDepartment() : "-";
            if (job != null && job.getDepartment() != null && !departmentFilter.getItems().contains(job.getDepartment())) {
                departmentFilter.getItems().add(job.getDepartment());
            }
            boolean withdrawable = isWithdrawable(app, job);
            if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                activeCount++;
            }
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                acceptedCount++;
            }
            if (app.getStatus() == ApplicationStatus.REJECTED) {
                rejectedCount++;
            }

            rows.add(new ApplicationHistoryViewModel(
                    app.getId(),
                    jobTitle,
                    department,
                    formatDateTime(app.getCreatedAt()),
                    app.getStatus() != null ? app.getStatus().name() : "-",
                    formatDateTime(app.getReviewTime()),
                    safeText(app.getReviewComment()),
                    String.format("%.2f%%", app.getMatchScore()),
                    withdrawable
            ));
        }

        applicationsTable.setItems(filterRows(rows));
        totalApplicationsLabel.setText(String.valueOf(applications.size()));
        activeApplicationsLabel.setText(String.valueOf(activeCount));
        resultSummaryLabel.setText("Accepted: " + acceptedCount + "  |  Rejected: " + rejectedCount);

        if (!rows.isEmpty()) {
            applicationsTable.getSelectionModel().selectFirst();
        } else {
            showDetails(null);
        }
    }

    @FXML
    private void handleFilterChange() {
        refreshApplications();
    }

    private ObservableList<ApplicationHistoryViewModel> filterRows(ObservableList<ApplicationHistoryViewModel> rows) {
        String status = statusFilter.getValue();
        String department = departmentFilter.getValue();
        ObservableList<ApplicationHistoryViewModel> filtered = FXCollections.observableArrayList();
        for (ApplicationHistoryViewModel row : rows) {
            boolean matchesStatus = status == null || "ALL".equals(status) || status.equals(row.getStatus());
            boolean matchesDepartment = department == null || "ALL".equals(department) || department.equals(row.getDepartment());
            if (matchesStatus && matchesDepartment) {
                filtered.add(row);
            }
        }
        return filtered;
    }

    private void showDetails(ApplicationHistoryViewModel selected) {
        if (selected == null) {
            detailTitleLabel.setText("No application selected");
            detailStatusLabel.setText("-");
            detailAppliedAtLabel.setText("-");
            detailReviewTimeLabel.setText("-");
            detailMatchScoreLabel.setText("-");
            detailCommentLabel.setText("Select one application to view details.");
            withdrawButton.setDisable(true);
            return;
        }

        detailTitleLabel.setText(selected.getJobTitle());
        detailStatusLabel.setText(selected.getStatus());
        detailAppliedAtLabel.setText(selected.getAppliedAt());
        detailReviewTimeLabel.setText(selected.getReviewTime());
        detailMatchScoreLabel.setText(selected.getMatchScore());
        detailCommentLabel.setText(selected.getReviewComment());
        withdrawButton.setDisable(!selected.isWithdrawable());
    }

    private boolean isWithdrawable(Application app, Job job) {
        if (app == null || job == null) {
            return false;
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return !ApplicationService.isDeadlinePassed(job.getDeadline());
    }

    private String formatDateTime(String value) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return "-";
        }
        return value.replace('T', ' ');
    }

    private String safeText(String value) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return "No feedback yet";
        }
        return value;
    }

    @FXML
    private void handleWithdrawApplication() {
        ApplicationHistoryViewModel selected = applicationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an application record first.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Withdrawal");
        confirmAlert.setHeaderText("Withdraw: " + selected.getJobTitle());
        confirmAlert.setContentText("This application will be marked as WITHDRAWN and can only be withdrawn before review. Continue?");
        confirmAlert.initModality(Modality.APPLICATION_MODAL);

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean success = ApplicationService.withdrawApplication(selected.getApplicationId(), user.getId());
        if (!success) {
            showAlert(Alert.AlertType.WARNING, "Cannot Withdraw", "Only applications that are still PENDING and before the deadline can be withdrawn.");
            refreshApplications();
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Withdrawn", "This application has been withdrawn. The status is now WITHDRAWN.");
        refreshApplications();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Logout failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
