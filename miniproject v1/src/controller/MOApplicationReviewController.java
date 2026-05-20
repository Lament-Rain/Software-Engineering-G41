package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ApplicantReviewViewModel;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.MO;
import model.MOJobReviewViewModel;
import model.TA;
import service.ApplicationService;
import service.JobService;
import service.UserService;

import java.util.List;
import java.util.Optional;

public class MOApplicationReviewController {
<<<<<<< Updated upstream
=======

    private static final String SORT_AI = "AI Ranking (Recommendation Score)";
    private static final String SORT_TIME = "Application Time (Newest First)";

>>>>>>> Stashed changes
    @FXML
    private ComboBox<MOJobReviewViewModel> jobSelector;
    @FXML
    private ComboBox<String> sortModeSelector;
    @FXML
    private Label applicantCountLabel;
    @FXML
    private Label acceptedCountLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private TableView<ApplicantReviewViewModel> applicantsTable;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> nameColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> departmentColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> submittedAtColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> statusColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> matchScoreColumn;
    @FXML
    private TableColumn<ApplicantReviewViewModel, String> resumeStatusColumn;
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

    private MO user;
    private Stage stage;
    private Job selectedJob;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        matchScoreColumn.setCellValueFactory(new PropertyValueFactory<>("matchScore"));
        resumeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("resumeStatus"));

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showApplicantDetails(newVal));
        jobSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedJob = JobService.getJobById(newVal.getJobId());
                loadApplicants();
            }
        });

        sortModeSelector.setItems(FXCollections.observableArrayList(SORT_AI, SORT_TIME));
        sortModeSelector.getSelectionModel().select(SORT_AI);
        sortModeSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldMode, newMode) -> {
            if (newMode != null && selectedJob != null) {
                loadApplicants();
            }
        });
    }

    public void setUser(MO user) {
        this.user = user;
        loadJobs();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
<<<<<<< Updated upstream
=======
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("escape", this::handleBackAction);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);

        if (stage.getScene() != null) {
            Parent root = stage.getScene().getRoot();
            shortcutService.setupEnterKeyNavigation(root);
        }
    }
    
    private void handleSearchAction() {
        showSearchBar();
    }

    private void showSearchBar() {
        try {
            // Create list of MO features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("Create Job", () -> handleCreateJob(new ActionEvent())));
            features.add(new SearchBarController.Feature("Application Review", () -> handleBackAction()));
            features.add(new SearchBarController.Feature("My Jobs", () -> handleViewMyJobs(new ActionEvent())));
            features.add(new SearchBarController.Feature("Job List", () -> handleViewAllJobs(new ActionEvent())));
            features.add(new SearchBarController.Feature("Personal Center", () -> handlePersonalCenter(new ActionEvent())));

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.initOwner(stage);
            searchStage.setTitle("Search Features");

            // Set up controller
            controller.setStage(searchStage);
            controller.setFeatures(features);
            controller.setOnFeatureSelected(featureName -> {
                // Find and execute the selected feature
                for (SearchBarController.Feature feature : features) {
                    if (feature.getName().equals(featureName)) {
                        feature.getAction().run();
                        break;
                    }
                }
            });

            // Create scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            searchStage.setScene(scene);



            // Show search bar
            searchStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load search bar", ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            service.NavigationHistory.getInstance().addEntry("MOCreateJob", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MOApplicationReviewController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application Review");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastService.showToast(stage, "Failed to load application review page", ToastService.ToastType.ERROR);
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Create TA Position");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load create job page", ToastService.ToastType.ERROR);
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

            // Add navigation entry for back functionality
            service.NavigationHistory.getInstance().addEntry("MOMyJobs", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MOApplicationReviewController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application Review");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastService.showToast(stage, "Failed to load application review page", ToastService.ToastType.ERROR);
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - My Jobs");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load my jobs page", ToastService.ToastType.ERROR);
        }
    }

    @FXML
    private void handleViewAllJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.MO);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            service.NavigationHistory.getInstance().addEntry("JobList", () -> {
                try {
                    FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();
                    MOApplicationReviewController dashboardController = dashboardLoader.getController();
                    dashboardController.setUser(user);
                    dashboardController.setStage(stage);
                    
                    Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Application Review");
                    
                    // Force layout update to ensure components resize properly
                    dashboardRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastService.showToast(stage, "Failed to load application review page", ToastService.ToastType.ERROR);
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load job list page", ToastService.ToastType.ERROR);
        }
    }

    private void updateSelectedCount() {
        ObservableList<ApplicantReviewViewModel> selected = applicantsTable.getSelectionModel().getSelectedItems();
        if (selected != null && !selected.isEmpty()) {
            selectedCountLabel.setText(selected.size() + " selected");
            selectedCountLabel.setVisible(true);
        } else {
            selectedCountLabel.setVisible(false);
        }
>>>>>>> Stashed changes
    }

    private void loadJobs() {
        if (user == null) {
            return;
        }

        ObservableList<MOJobReviewViewModel> items = FXCollections.observableArrayList();
        for (Job job : JobService.getAllJobsByMO(user.getId())) {
            int count = ApplicationService.getApplicationsByJob(job.getId()).size();
            items.add(new MOJobReviewViewModel(
                    job.getId(),
                    job.getTitle(),
                    job.getDepartment(),
                    formatDate(job.getDeadline()),
                    String.valueOf(count)
            ));
        }
        jobSelector.setItems(items);
        if (!items.isEmpty()) {
            jobSelector.getSelectionModel().selectFirst();
        }
    }

    private void loadApplicants() {
        if (selectedJob == null) {
            return;
        }

        List<Application> applications = ApplicationService.getApplicationsByJob(selectedJob.getId());
        ObservableList<ApplicantReviewViewModel> rows = FXCollections.observableArrayList();

<<<<<<< Updated upstream
        int accepted = 0;
        int pending = 0;
        for (Application app : applications) {
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta == null) {
                continue;
=======
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                currentPageData = ApplicationService.getApplicationsByJobPaged(selectedJob.getId(), currentPage, pageSize);

                List<ApplicantReviewViewModel> rows = currentPageData.getContent().stream()
                        .map(app -> {
                            TA ta = UserService.getTAProfile(app.getTaId());
                            if (ta == null) {
                                return null;
                            }
                            return new ApplicantReviewViewModel(
                                    app.getId(),
                                    ta.getId(),
                                    ta.getName() != null && !ta.getName().isEmpty() ? ta.getName() : ta.getUsername(),
                                    ta.getDepartment() != null ? ta.getDepartment() : "-",
                                    formatDate(app.getCreatedAt()),
                                    app.getStatus() != null ? app.getStatus().name() : "-",
                                    String.format("%.2f%%", ApplicationService.calculateMatchScoreForReview(app.getTaId(), app.getJobId())),
                                    ta.getResumePath() != null && !ta.getResumePath().isEmpty() ? "Uploaded" : "Not uploaded",
                                    ta.getExperience() != null ? ta.getExperience() : "",
                                    app.getCoverLetter() != null ? app.getCoverLetter() : "",
                                    app.getReviewComment() != null ? app.getReviewComment() : ""
                            );
                        })
                        .filter(v -> v != null)
                        .collect(Collectors.toList());

                javafx.application.Platform.runLater(() -> {
                    allApplicants.clear();
                    allApplicants.addAll(sortApplicants(rows));

                    applicantsTable.setItems(FXCollections.observableArrayList(allApplicants));
                    selectedJobLabel.setText(selectedJob.getTitle());
                    applicantCountLabel.setText(String.valueOf(currentPageData.getTotalElements()));

                    long accepted = currentPageData.getContent().stream()
                            .filter(a -> a.getStatus() == ApplicationStatus.ACCEPTED)
                            .count();
                    long pending = currentPageData.getContent().stream()
                            .filter(a -> a.getStatus() == ApplicationStatus.PENDING || a.getStatus() == ApplicationStatus.SCREENED)
                            .count();
                    acceptedCountLabel.setText(String.valueOf(accepted));
                    pendingCountLabel.setText(String.valueOf(pending));

                    if (!allApplicants.isEmpty()) {
                        applicantsTable.getSelectionModel().selectFirst();
                    } else {
                        showApplicantDetails(null);
                    }

                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                });

                return null;
>>>>>>> Stashed changes
            }
            if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                accepted++;
            }
            if (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.SCREENED) {
                pending++;
            }

            rows.add(new ApplicantReviewViewModel(
                    app.getId(),
                    ta.getId(),
                    ta.getName() != null && !ta.getName().isEmpty() ? ta.getName() : ta.getUsername(),
                    ta.getDepartment() != null ? ta.getDepartment() : "-",
                    formatDate(app.getCreatedAt()),
                    app.getStatus() != null ? app.getStatus().name() : "-",
                    String.format("%.2f%%", app.getMatchScore()),
                    ta.getResumePath() != null && !ta.getResumePath().isEmpty() ? "Uploaded" : "Not uploaded",
                    ta.getExperience() != null ? ta.getExperience() : "",
                    app.getCoverLetter() != null ? app.getCoverLetter() : "",
                    app.getReviewComment() != null ? app.getReviewComment() : ""
            ));
        }

        applicantsTable.setItems(rows);
        selectedJobLabel.setText(selectedJob.getTitle());
        applicantCountLabel.setText(String.valueOf(rows.size()));
        acceptedCountLabel.setText(String.valueOf(accepted));
        pendingCountLabel.setText(String.valueOf(pending));

        if (!rows.isEmpty()) {
            applicantsTable.getSelectionModel().selectFirst();
        } else {
            showApplicantDetails(null);
        }
    }

    private void showApplicantDetails(ApplicantReviewViewModel applicant) {
        if (applicant == null) {
            applicantNameLabel.setText("No applicant selected");
            applicantStatusLabel.setText("-");
            applicantResumeLabel.setText("-");
            experienceArea.setText("");
            coverLetterArea.setText("");
            feedbackArea.clear();
            screenButton.setDisable(true);
            acceptButton.setDisable(true);
            rejectButton.setDisable(true);
            return;
        }

        applicantNameLabel.setText(applicant.getName());
        applicantStatusLabel.setText(applicant.getStatus());
        applicantResumeLabel.setText(applicant.getResumeStatus());
        experienceArea.setText(applicant.getExperience());
        coverLetterArea.setText(applicant.getCoverLetter());
        feedbackArea.setText(applicant.getReviewComment());

        boolean finalStatus = "ACCEPTED".equals(applicant.getStatus()) || "REJECTED".equals(applicant.getStatus()) || "WITHDRAWN".equals(applicant.getStatus());
        screenButton.setDisable(finalStatus);
        acceptButton.setDisable(finalStatus);
        rejectButton.setDisable(finalStatus);
    }

    @FXML
    private void handleScreenApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        String feedback = feedbackArea.getText() != null ? feedbackArea.getText().trim() : "";
        if (feedback.isEmpty()) {
            feedback = "Shortlisted by MO";
        }

        boolean success = ApplicationService.screenApplication(selected.getApplicationId(), ApplicationStatus.SCREENED, feedback, user.getId());
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Action Failed", "Unable to mark this application as SCREENED.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Screened", "This application has been marked as SCREENED.");
        loadApplicants();
    }

    @FXML
    private void handleAcceptApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        boolean success = ApplicationService.acceptApplication(selected.getApplicationId(), user.getId());
        if (!success) {
            showAlert(Alert.AlertType.WARNING, "Acceptance Failed", "The position may have reached its acceptance limit, or the application status does not allow acceptance.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Accepted", "This application has been updated to ACCEPTED.");
        loadApplicants();
    }

    @FXML
    private void handleRejectApplicant() {
        ApplicantReviewViewModel selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "Please select an applicant first.");
            return;
        }

        String feedback = feedbackArea.getText() != null ? feedbackArea.getText().trim() : "";
        if (feedback.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reject Application");
            dialog.setHeaderText("Enter rejection reason");
            dialog.setContentText("Reason:");
            Optional<String> input = dialog.showAndWait();
            if (!input.isPresent() || input.get().trim().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Please enter a rejection reason before continuing.");
                return;
            }
            feedback = input.get().trim();
        }

        boolean success = ApplicationService.rejectApplication(selected.getApplicationId(), feedback, user.getId());
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Rejection Failed", "This application cannot be rejected.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Rejected", "This application has been updated to REJECTED.");
        loadApplicants();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            controller.setUser(user);

<<<<<<< Updated upstream
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back: " + e.getMessage());
=======
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOProfileView.fxml"));
            Parent root = loader.load();
            MOProfileViewController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - MO Personal Center");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to load personal center page", ToastService.ToastType.ERROR);
>>>>>>> Stashed changes
        }
    }

    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
            Parent root = loader.load();
            MODashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            root.requestLayout();
            stage.sizeToScene();
            stage.setTitle("BUPT International School TA Recruitment System - MO Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Failed to go back: " + e.getMessage(), ToastService.ToastType.ERROR);
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
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            ToastService.showToast(stage, "Logout failed: " + e.getMessage(), ToastService.ToastType.ERROR);
        }
    }

    private List<ApplicantReviewViewModel> sortApplicants(List<ApplicantReviewViewModel> rows) {
        if (rows == null || rows.isEmpty()) {
            return rows;
        }

        String mode = sortModeSelector == null ? SORT_AI : sortModeSelector.getValue();
        java.util.Comparator<ApplicantReviewViewModel> comparator;

        if (SORT_TIME.equals(mode)) {
            comparator = java.util.Comparator.comparing(
                    (ApplicantReviewViewModel v) -> parseSubmittedAt(v.getSubmittedAt()),
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())
            ).reversed();
        } else {
            comparator = java.util.Comparator.comparingDouble(this::parseMatchScore).reversed();
        }

        return rows.stream().sorted(comparator).collect(Collectors.toList());
    }

    private double parseMatchScore(ApplicantReviewViewModel applicant) {
        if (applicant == null || applicant.getMatchScore() == null) {
            return -1.0;
        }
        String raw = applicant.getMatchScore().replace("%", "").trim();
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    private java.time.LocalDateTime parseSubmittedAt(String submittedAt) {
        if (submittedAt == null || submittedAt.isBlank() || "-".equals(submittedAt)) {
            return null;
        }
        String normalized = submittedAt.trim().replace('T', ' ');
        try {
            return java.time.LocalDateTime.parse(normalized.replace(' ', 'T'));
        } catch (Exception ignored) {
        }
        try {
            return java.time.LocalDate.parse(normalized).atStartOfDay();
        } catch (Exception ignored) {
        }
        return null;
    }

    private String formatDate(String value) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return "-";
        }
        String normalized = value.replace('T', ' ').trim();
        int dotIndex = normalized.indexOf('.');
        if (dotIndex > 0) {
            normalized = normalized.substring(0, dotIndex);
        }
        return normalized;
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
