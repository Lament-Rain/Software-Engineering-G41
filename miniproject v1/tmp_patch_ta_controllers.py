from pathlib import Path

# Patch TAProfileEditController
path = Path(r"c:\Users\Lenovo\Desktop\MiniProject\Software-Engineering-G41\miniproject v1\src\controller\TAProfileEditController.java")
text = path.read_text(encoding="utf-8")
old = '''    @FXML
    private void handleHome(ActionEvent event) {
        handleBack(event);
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
'''
new = '''    @FXML
    private void handleHome(ActionEvent event) {
        handleBack(event);
    }

    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.TA);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load page");
        }
    }

    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
            Parent root = loader.load();
            TAApplicationHistoryController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application History");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load page");
        }
    }

    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        // Already on personal center edit page
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
'''
if old not in text:
    raise SystemExit('TAProfileEditController target not found')
text = text.replace(old, new, 1)
path.write_text(text, encoding='utf-8')

# Patch TAProfileViewController
path = Path(r"c:\Users\Lenovo\Desktop\MiniProject\Software-Engineering-G41\miniproject v1\src\controller\TAProfileViewController.java")
text = path.read_text(encoding="utf-8")
old = '''    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user, false);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
            
            // Call application management function
            controller.handleMyApplicationsClick(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
'''
new = '''    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TADashboard.fxml"));
            Parent root = loader.load();
            TADashboardController controller = loader.getController();
            controller.setUser(user, false);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - TA Dashboard");
            
            // Call application management function
            controller.handleMyApplicationsClick(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, model.UserRole.TA);
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Requirements");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
'''
if old not in text:
    raise SystemExit('TAProfileViewController target not found')
text = text.replace(old, new, 1)
path.write_text(text, encoding='utf-8')
