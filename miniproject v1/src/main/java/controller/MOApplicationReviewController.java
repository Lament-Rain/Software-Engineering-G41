package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.User;
import model.TA;
import service.DataStorage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MOApplicationReviewController {

    @FXML
    private ComboBox<String> jobSelector;
    @FXML
    private Label applicantCountLabel;
    @FXML
    private Label acceptedCountLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private TableView<ObservableList<String>> applicantsTable;
    @FXML
    private TableColumn<ObservableList<String>, String> nameColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> departmentColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> submittedAtColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> statusColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> matchScoreColumn;
    @FXML
    private TableColumn<ObservableList<String>, String> resumeStatusColumn;
    @FXML
    private Label selectedJobLabel;
    @FXML
    private Label applicantNameLabel;
    @FXML
    private Label applicantStatusLabel;
    @FXML
    private Label applicantResumeLabel;
    @FXML
    private TextArea experienceArea;
    @FXML
    private TextArea coverLetterArea;
    @FXML
    private TextArea feedbackArea;
    @FXML
    private Button screenButton;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;
    
    private model.MO currentUser;

    @FXML
    public void initialize() {
        initTableColumns();

        jobSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (currentUser != null) {
                loadApplications();
            }
            if (newVal != null && !newVal.equals("All Jobs")) {
                selectedJobLabel.setText(newVal);
                System.out.println("Selected job: " + newVal);
            } else {
                selectedJobLabel.setText("All Jobs");
            }
        });

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Only show basic info here, details in original data
                applicantNameLabel.setText(newVal.get(0));
                applicantStatusLabel.setText(newVal.get(3));
                applicantResumeLabel.setText(newVal.get(5));
                experienceArea.setText("Click application to view details");
                coverLetterArea.setText("Click application to view details");
                System.out.println("Selected applicant: " + newVal.get(0));
            } else {
                clearApplicantDetail();
            }
        });
    }
    
    public void setUser(model.MO user) {
        this.currentUser = user;
        if (user != null) {
            initJobSelector();
            loadApplications();
        }
    }
    
    private void initJobSelector() {
        ObservableList<String> jobList = FXCollections.observableArrayList();
        jobList.add("All Jobs");
        
        // Load all jobs published by current MO
        List<Job> allJobs = service.DataStorage.getJobs();
        for (Job job : allJobs) {
            if (currentUser != null && currentUser.getId().equals(job.getMoId())) {
                jobList.add(job.getTitle());
            }
        }
        
        jobSelector.setItems(jobList);
        jobSelector.setValue("All Jobs");
        selectedJobLabel.setText("All Jobs");
    }
    
    private void updateStatistics(List<Application> applications) {
        int total = applications.size();
        int accepted = 0;
        int pending = 0;
        
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                accepted++;
            } else if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                pending++;
            }
        }
        
        applicantCountLabel.setText(String.valueOf(total));
        acceptedCountLabel.setText(String.valueOf(accepted));
        pendingCountLabel.setText(String.valueOf(pending));
    }
    
    private void loadApplications() {
        String selectedJobTitle = jobSelector.getValue();
        List<Job> allJobs = service.DataStorage.getJobs();
        List<Application> allApplications = service.DataStorage.getApplications();
        
        // Filter applications for current MO
        List<Application> myApplications = new ArrayList<>();
        
        for (Application app : allApplications) {
            // Find corresponding job
            for (Job job : allJobs) {
                if (job.getId().equals(app.getJobId())) {
                    // Check if this job belongs to current MO
                    if (currentUser != null && currentUser.getId().equals(job.getMoId())) {
                        // Filter by selected job
                        if ("All Jobs".equals(selectedJobTitle) || job.getTitle().equals(selectedJobTitle)) {
                            myApplications.add(app);
                        }
                    }
                    break;
                }
            }
        }
        
        // Update statistics
        updateStatistics(myApplications);
        
        // Load table data
        ObservableList<ObservableList<String>> applicationList = FXCollections.observableArrayList();
        
        for (Application app : myApplications) {
            // Find TA info
            String taName = "Unknown";
            String taDepartment = "";
            TA ta = null;
            List<User> users = service.DataStorage.getUsers();
            for (User u : users) {
                if (u.getId().equals(app.getTaId()) && u instanceof TA) {
                    ta = (TA) u;
                    taName = ta.getName() != null ? ta.getName() : ta.getUsername();
                    taDepartment = ta.getDepartment() != null ? ta.getDepartment() : "";
                    break;
                }
            }
            
            String statusStr = getStatusName(app.getStatus());
            String resumeStr = (ta != null && ta.getResumePath() != null && !ta.getResumePath().isEmpty()) 
                    ? "Uploaded" : "Not Uploaded";
            String scoreStr = app.getMatchScore() > 0 ? String.format("%.0f", app.getMatchScore()) : "-";
            
            applicationList.add(FXCollections.observableArrayList(
                    taName,
                    taDepartment,
                    app.getCreatedAt() != null ? app.getCreatedAt() : "-",
                    statusStr,
                    scoreStr,
                    resumeStr
            ));
        }
        
        if (applicationList.isEmpty()) {
            applicationList.add(FXCollections.observableArrayList("No Applications", "", "", "", "", ""));
        }
        
        applicantsTable.setItems(applicationList);
    }
    
    private String getStatusName(ApplicationStatus status) {
        if (status == null) return "Pending";
        switch (status) {
            case PENDING: return "Pending";
            case SCREENED: return "Screened";
            case ACCEPTED: return "Accepted";
            case REJECTED: return "Rejected";
            default: return "Pending";
        }
    }

    private void initTableColumns() {
        nameColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(0));
                    }
                });

        departmentColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(1));
                    }
                });

        submittedAtColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(2));
                    }
                });

        statusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(3));
                    }
                });

        matchScoreColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(4));
                    }
                });

        resumeStatusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, javafx.beans.value.ObservableValue<String>>() {
                    @Override
                    public javafx.beans.value.ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                        return new javafx.beans.property.SimpleStringProperty(param.getValue().get(5));
                    }
                });
    }

    private void clearApplicantDetail() {
        applicantNameLabel.setText("No applicant selected");
        applicantStatusLabel.setText("-");
        applicantResumeLabel.setText("-");
        experienceArea.clear();
        coverLetterArea.clear();
        feedbackArea.clear();
    }

    @FXML
    private void handleBack() {
        System.out.println("Back to MO Dashboard");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            
            // Pass current MO user to refresh data
            if (currentUser != null) {
                controller.setUser(currentUser);
            }
            
            Stage stage = (Stage) jobSelector.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("Module Organizer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to return to dashboard");
        }
    }

    @FXML
    private void handleScreenApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Info", "Please select an applicant first");
            return;
        }
        
        // Find corresponding Application object
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("Error", "Application not found");
            return;
        }
        
        // Update status
        app.setStatus(ApplicationStatus.SCREENED);
        app.setReviewComment(feedbackArea.getText());
        app.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        // Save to data storage
        saveApplications();
        loadApplications();
        
        System.out.println("Mark applicant [" + selectedName + "] as Screened");
        showAlert("Success", "Marked as Screened");
    }

    @FXML
    private void handleAcceptApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Info", "Please select an applicant first");
            return;
        }
        
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("Error", "Application not found");
            return;
        }
        
        String feedback = feedbackArea.getText();
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setReviewComment(feedback);
        app.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        saveApplications();
        loadApplications();
        
        System.out.println("Accept applicant [" + selectedName + "], note: " + feedback);
        showAlert("Success", "Applicant Accepted");
    }

    @FXML
    private void handleRejectApplicant() {
        int selectedIndex = applicantsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Info", "Please select an applicant first");
            return;
        }
        
        String feedback = feedbackArea.getText();
        if (feedback == null || feedback.trim().isEmpty()) {
            showAlert("Info", "Please provide a reason for rejection");
            return;
        }
        
        String selectedName = applicantsTable.getItems().get(selectedIndex).get(0);
        Application app = findApplicationByName(selectedName);
        if (app == null) {
            showAlert("Error", "Application not found");
            return;
        }
        
        app.setStatus(ApplicationStatus.REJECTED);
        app.setReviewComment(feedback);
        app.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        saveApplications();
        loadApplications();
        
        System.out.println("Reject applicant [" + selectedName + "], reason: " + feedback);
        showAlert("Success", "Applicant Rejected");
    }
    
    // Find Application object by TA name
    private Application findApplicationByName(String taName) {
        List<Application> allApps = service.DataStorage.getApplications();
        List<User> allUsers = service.DataStorage.getUsers();
        
        for (Application app : allApps) {
            for (User u : allUsers) {
                if (u.getId().equals(app.getTaId()) && u instanceof TA) {
                    TA ta = (TA) u;
                    String name = ta.getName() != null ? ta.getName() : ta.getUsername();
                    if (name.equals(taName)) {
                        return app;
                    }
                }
            }
        }
        return null;
    }
    
    private void saveApplications() {
        List<Application> applications = service.DataStorage.getApplications();
        service.DataStorage.saveApplications(applications);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
