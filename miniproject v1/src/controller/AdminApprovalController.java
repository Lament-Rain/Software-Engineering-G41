package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Admin;
import model.ProfileStatus;
import model.TA;
import model.User;
import service.UserService;

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

    private final ObservableList<TA> pendingTAs = FXCollections.observableArrayList();
    private final ObservableList<TA> reviewedTAs = FXCollections.observableArrayList();

    private Admin user;
    private Stage stage;

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
        pendingTAs.clear();
        reviewedTAs.clear();

        for (User currentUser : UserService.getAllUsers()) {
            if (!(currentUser instanceof TA)) {
                continue;
            }
            TA ta = (TA) currentUser;
            if (ta.getProfileStatus() == ProfileStatus.PENDING) {
                pendingTAs.add(ta);
            } else if (ta.getProfileStatus() == ProfileStatus.APPROVED || ta.getProfileStatus() == ProfileStatus.REJECTED) {
                reviewedTAs.add(ta);
            }
        }

        pendingCountLabel.setText(String.valueOf(pendingTAs.size()));
        reviewedCountLabel.setText(String.valueOf(reviewedTAs.size()));

        if (pendingTAs.isEmpty()) {
            updateSelectionSummary(null);
        } else if (pendingTable.getSelectionModel().getSelectedItem() == null) {
            pendingTable.getSelectionModel().selectFirst();
            updateSelectionSummary(pendingTable.getSelectionModel().getSelectedItem());
        }
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

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            showError("登录页面加载失败");
        }
    }

    private void reviewSelected(ProfileStatus targetStatus) {
        TA selectedTa = pendingTable.getSelectionModel().getSelectedItem();
        if (selectedTa == null) {
            showWarning("请先选择要审核的TA资料");
            return;
        }

        String comment = reviewCommentArea.getText() == null ? "" : reviewCommentArea.getText().trim();
        if (UserService.reviewTAProfile(selectedTa.getId(), targetStatus, comment)) {
            reviewCommentArea.clear();
            refreshTables();
        } else {
            showError("审核失败，请稍后重试");
        }
    }

    private void updateSelectionSummary(TA ta) {
        if (ta == null) {
            selectedTaLabel.setText("当前未选择待审核TA");
            selectedProfileSummaryLabel.setText("请选择左侧列表中的待审核资料，右侧可填写意见并完成审核。\n支持查看简历上传情况、学号和更新时间。\n已审核记录会显示在下方列表中。");
            return;
        }

        selectedTaLabel.setText("当前审核：" + safeText(ta.getName()) + "（" + safeText(ta.getUsername()) + "）");
        selectedProfileSummaryLabel.setText(
                "学院：" + safeText(ta.getDepartment()) +
                "\n学号：" + safeText(ta.getStudentId()) +
                "\n简历：" + formatResumeStatus(ta) +
                "\n状态：" + ta.getProfileStatus() +
                "\n更新时间：" + safeText(ta.getProfileUpdatedAt())
        );
    }

    private String formatResumeStatus(TA ta) {
        return ta.getResumePath() == null || ta.getResumePath().isBlank() ? "未上传" : "已上传";
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void openAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.setUser(user);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 管理员控制台");
        } catch (Exception e) {
            e.printStackTrace();
            showError("无法加载管理员首页");
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText("操作提醒");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("操作失败");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
