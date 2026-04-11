package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Admin;
import model.Job;
import model.JobType;
import model.MO;
import model.UserRole;
import service.JobService;

import java.util.List;

public class MOCreateJobController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private TextField workTimeField;
    @FXML
    private TextField recruitNumField;
    @FXML
    private TextField deadlineField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea skillsArea;
    @FXML
    private Label errorMessage;

    private MO moUser;
    private Admin adminUser;
    private UserRole userRole;
    private Stage stage;

    public void setUser(MO user) {
        this.moUser = user;
        this.userRole = UserRole.MO;
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName());
        }
    }

    public void setAdminUser(Admin admin) {
        this.adminUser = admin;
        this.userRole = UserRole.ADMIN;
        if (admin != null) {
            welcomeLabel.setText("Welcome, Admin " + admin.getUsername());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("MODULE_ASSISTANT", "INVIGILATION", "OTHER");
        departmentComboBox.getItems().addAll(
                "Computer Science School",
                "Science School",
                "School of Foreign Languages",
                "School of Humanities",
                "School of Economics and Management",
                "School of Engineering"
        );
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();
        String workTime = workTimeField.getText();
        String recruitNumStr = recruitNumField.getText();
        String deadline = deadlineField.getText();
        String description = descriptionArea.getText();
        String skills = skillsArea.getText();

        if (title.isEmpty() || type == null || department == null || workTime.isEmpty() || recruitNumStr.isEmpty() || deadline.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            errorMessage.setText("Please fill in all required fields.");
            return;
        }

        int recruitNum;
        try {
            recruitNum = Integer.parseInt(recruitNumStr);
            if (recruitNum <= 0) {
                errorMessage.setText("The number of openings must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            errorMessage.setText("The number of openings must be numeric.");
            return;
        }

        List<String> skillsList = java.util.Arrays.asList(skills.split(","));
        String salary = "";
        String location = "";
        String extraRequirements = "";

        Job job = null;
        if (userRole == UserRole.MO && moUser != null) {
            job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, moUser.getId(), "MO", moUser.getName());
        } else if (userRole == UserRole.ADMIN && adminUser != null) {
            job = JobService.createJob(title, JobType.valueOf(type), department, description, skillsList, workTime, recruitNum, deadline, salary, location, extraRequirements, adminUser.getId(), "ADMIN", adminUser.getUsername());
        }

        if (job != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Published Successfully");
            alert.setHeaderText("Job published successfully");
            alert.setContentText("The position has been published.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
            handleHome();
        } else {
            errorMessage.setText("Failed to publish the job. Please try again later.");
        }
    }

    @FXML
    private void handleCancel() {
        handleHome();
    }

    @FXML
    private void handleHome() {
        try {
            if (userRole == UserRole.MO && moUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MODashboard.fxml"));
                Parent root = loader.load();
                MODashboardController controller = loader.getController();
                controller.setUser(moUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Module Organizer Dashboard");
            } else if (userRole == UserRole.ADMIN && adminUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent root = loader.load();
                AdminDashboardController controller = loader.getController();
                controller.setUser(adminUser);

                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("BUPT International School TA Recruitment System - Admin Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("Failed to load the page. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load page");
            alert.setContentText("Failed to load the login page. Please try again later.");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}
