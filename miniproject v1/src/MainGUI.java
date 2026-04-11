import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.DataStorage;

public class MainGUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data storage
        service.DataStorage.initialize();
        
        // Load login page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        // Get controller and set stage
        controller.LoginController loginController = loader.getController();
        loginController.setStage(primaryStage);
        
        Scene scene = new Scene(root, 800, 600);
        // Add stylesheet
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("BUPT International School TA Recruitment System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}