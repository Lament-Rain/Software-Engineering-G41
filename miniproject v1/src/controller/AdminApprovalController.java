package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Admin;
import model.ProfileStatus;
import model.TA;
import service.CacheService;
import service.PaginationUtil;
import service.UserService;
import service.KeyboardShortcutService;
import service.NavigationHistory;
import controller.SearchBarController;
import java.util.List;

public class AdminApprovalController {
    @FXML
    private Label pendingCountLabel;
    @FXML
    private Label reviewedCountLabel;
    @FXML
    private TableView<TA> pendingTable;
    @FXML
    private TableColumn<TA, String> pendingUsernameCol;
    @FXML
    private TableColumn<TA, String> pendingNameCol;
    @FXML
    private TableColumn<TA, String> pendingDepartmentCol;
    @FXML
    private TableColumn<TA, String> pendingStudentIdCol;
    @FXML
    private TableColumn<TA, String> pendingResumeCol;
    @FXML
    private TableColumn<TA, ProfileStatus> pendingStatusCol;
    @FXML
    private TableColumn<TA, String> pendingUpdatedAtCol;
    @FXML
    private TextArea reviewCommentArea;
    @FXML
    private Label selectedTaLabel;
    @FXML
    private Label selectedProfileSummaryLabel;
    @FXML
    private TableView<TA> reviewedTable;
    @FXML
    private TableColumn<TA, String> reviewedUsernameCol;
    @FXML
    private TableColumn<TA, String> reviewedNameCol;
    @FXML
    private TableColumn<TA, String> reviewedDepartmentCol;
    @FXML
    private TableColumn<TA, String> reviewedStudentIdCol;
    @FXML
    private TableColumn<TA, ProfileStatus> reviewedStatusCol;
    @FXML
    private TableColumn<TA, String> reviewedCommentCol;
    @FXML
    private TableColumn<TA, String> reviewedUpdatedAtCol;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final ObservableList<TA> pendingTAs = FXCollections.observableArrayList();
    private final ObservableList<TA> reviewedTAs = FXCollections.observableArrayList();

    private Admin user;
    private Stage stage;
    private int pendingPage = 0;
    private int reviewedPage = 0;
    private static final int PAGE_SIZE = 20;
    private PaginationUtil.Page<TA> pendingPageData;
    private PaginationUtil.Page<TA> reviewedPageData;
    private KeyboardShortcutService shortcutService;

    @FXML
    private void initialize() {
        setupTables();
        pendingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> updateSelectionSummary(newSelection));
    }

    public void setUser(Admin user) {
        this.user = user;
        refreshTables();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        shortcutService = new KeyboardShortcutService(stage);
        shortcutService.registerShortcut("ctrl+f", this::handleSearchAction);
        shortcutService.registerShortcut("escape", this::handleBackAction);

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
            // Create list of Admin features
            List<SearchBarController.Feature> features = new java.util.ArrayList<>();
            features.add(new SearchBarController.Feature("Dashboard", () -> handleHome(new ActionEvent())));
            features.add(new SearchBarController.Feature("Approval Center", () -> handleApprovalCenter(new ActionEvent())));
            features.add(new SearchBarController.Feature("User Management", () -> handleUserManagement(new ActionEvent())));
            features.add(new SearchBarController.Feature("Create Job", () -> handleCreateJob()));

            // Load search bar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
            Parent root = loader.load();
            SearchBarController controller = loader.getController();

            // Create stage for search bar
            Stage searchStage = new Stage();
            searchStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
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
            showError("Failed to load search bar: " + e.getMessage());
        }
    }

    private void handleCreateJob() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setAdminUser(user);
            controller.setStage(stage);

            // Add navigation entry for back functionality
            NavigationHistory.getInstance().addEntry("MOCreateJob", () -> {
                try {
                    FXMLLoader approvalLoader = new FXMLLoader(getClass().getResource("/fxml/AdminApproval.fxml"));
                    Parent approvalRoot = approvalLoader.load();
                    AdminApprovalController approvalController = approvalLoader.getController();
                    approvalController.setUser(user);
                    approvalController.setStage(stage);
                    
                    Scene scene = new Scene(approvalRoot, stage.getWidth(), stage.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("BUPT International School TA Recruitment System - Admin Approval");
                    
                    // Force layout update to ensure components resize properly
                    approvalRoot.requestLayout();
                    stage.sizeToScene();
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Failed to load admin approval page: " + e.getMessage());
                }
            });

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Create Job");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load create job page: " + e.getMessage());
        }
    }

    private void handleBackAction() {
        handleHome(new ActionEvent());
    }

    private void setupTables() {
        pendingUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        pendingNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingDepartmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        pendingStudentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        pendingResumeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(formatResumeStatus(cell.getValue())));
        pendingStatusCol.setCellValueFactory(new PropertyValueFactory<>("profileStatus"));
        pendingUpdatedAtCol.setCellValueFactory(new PropertyValueFactory<>("profileUpdatedAt"));
        pendingTable.setItems(pendingTAs);

        reviewedUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        reviewedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        reviewedDepartmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        reviewedStudentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        reviewedStatusCol.setCellValueFactory(new PropertyValueFactory<>("profileStatus"));
        reviewedCommentCol.setCellValueFactory(new PropertyValueFactory<>("profileReviewComment"));
        reviewedUpdatedAtCol.setCellValueFactory(new PropertyValueFactory<>("profileUpdatedAt"));
        reviewedTable.setItems(reviewedTAs);
    }

    private void refreshTables() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }

        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                pendingPageData = UserService.getPendingTAsPaged(pendingPage, PAGE_SIZE);
                PaginationUtil.Page<TA> reviewedData = UserService.getPendingTAsPaged(reviewedPage, PAGE_SIZE);

                javafx.application.Platform.runLater(() -> {
                    pendingTAs.clear();
                    pendingTAs.addAll(pendingPageData.getContent());

                    reviewedTAs.clear();
                    reviewedData.getContent().stream()
                            .filter(ta -> ta.getProfileStatus() == ProfileStatus.APPROVED ||
                                         ta.getProfileStatus() == ProfileStatus.REJECTED)
                            .forEach(reviewedTAs::add);

                    pendingCountLabel.setText(String.valueOf(pendingPageData.getTotalElements()));
                    reviewedCountLabel.setText(String.valueOf(reviewedData.getTotalElements()));

                    if (pendingTAs.isEmpty()) {
                        updateSelectionSummary(null);
                    } else if (pendingTable.getSelectionModel().getSelectedItem() == null) {
                        pendingTable.getSelectionModel().selectFirst();
                        updateSelectionSummary(pendingTable.getSelectionModel().getSelectedItem());
                    }

                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                });

                return null;
            }
        };

        new Thread(refreshTask).start();
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        reviewSelected(ProfileStatus.APPROVED);
    }

    @FXML
    private void handleReject(ActionEvent event) {
        reviewSelected(ProfileStatus.REJECTED);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        pendingPage = 0;
        reviewedPage = 0;
        CacheService.invalidate(CacheService.pendingTAsKey());
        refreshTables();
    }

    @FXML
    private void handleHome(ActionEvent event) {
        openAdminDashboard();
    }

    @FXML
    private void handleUserManagement(ActionEvent event) {
        openAdminDashboard();
    }

    @FXML
    private void handleApprovalCenter(ActionEvent event) {
        refreshTables();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage(stage);

            // Save current window state
            boolean isFullScreen = stage.isFullScreen();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Create new scene without hardcoded size
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Apply saved window state
            stage.setScene(scene);
            if (isFullScreen) {
                stage.setFullScreen(true);
            } else if (currentWidth > 0 && currentHeight > 0) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            } else {
                // Default size if no previous size
                stage.setWidth(800);
                stage.setHeight(600);
            }
            stage.setTitle("BUPT International School TA Recruitment System - Login");
                
                // Force layout update to ensure components resize properly
                root.requestLayout();
                stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load login page");
        }
    }

    private void reviewSelected(ProfileStatus targetStatus) {
        TA selectedTa = pendingTable.getSelectionModel().getSelectedItem();
        if (selectedTa == null) {
            showWarning("Please select a TA profile to review first");
            return;
        }

        String comment = reviewCommentArea.getText() == null ? "" : reviewCommentArea.getText().trim();
        if (UserService.reviewTAProfile(selectedTa.getId(), targetStatus, comment)) {
            reviewCommentArea.clear();
            refreshTables();
        } else {
            showError("Review failed. Please try again later.");
        }
    }

    private void updateSelectionSummary(TA ta) {
        if (ta == null) {
            selectedTaLabel.setText("No TA selected for review");
            selectedProfileSummaryLabel.setText("Select a pending profile on the left, then add comments and complete the review on the right.\nYou can check the resume upload status, student ID, and last update time.\nReviewed records will appear in the list below.");
            return;
        }

        selectedTaLabel.setText("Reviewing: " + safeText(ta.getName()) + " (" + safeText(ta.getUsername()) + ")");
        selectedProfileSummaryLabel.setText(
                "Department: " + safeText(ta.getDepartment()) +
                "\nStudent ID: " + safeText(ta.getStudentId()) +
                "\nResume: " + formatResumeStatus(ta) +
                "\nStatus: " + ta.getProfileStatus() +
                "\nUpdated At: " + safeText(ta.getProfileUpdatedAt())
        );
    }

    private String formatResumeStatus(TA ta) {
        return ta.getResumePath() == null || ta.getResumePath().isBlank() ? "Not Uploaded" : "Uploaded";
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void openAdminDashboard() {
        // If we can go back, do so; otherwise, go to dashboard
        if (NavigationHistory.getInstance().canGoBack()) {
            NavigationHistory.getInstance().goBack();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser(user);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Unable to load admin home page");
            }
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Action Required");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
