package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {
    
    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 768;

    public static void navigateTo(Stage stage, String fxmlPath) throws IOException {
        navigateTo(stage, fxmlPath, null);
    }

    public static void navigateTo(Stage stage, String fxmlPath, ControllerSetupCallback controllerSetup) throws IOException {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }

        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();

        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        
        double newWidth = Math.max(currentWidth, MIN_WIDTH);
        double newHeight = Math.max(currentHeight, MIN_HEIGHT);

        Scene scene = new Scene(root, newWidth, newHeight);
        scene.getStylesheets().add(NavigationUtil.class.getResource("/css/styles.css").toExternalForm());

        Object controller = loader.getController();
        
        if (controllerSetup != null) {
            controllerSetup.setup(controller);
        }

        stage.setScene(scene);
        
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        
        stage.sizeToScene();
        
        stage.show();
    }

    public static void navigateToWithNode(javafx.scene.Node sourceNode, String fxmlPath) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        navigateTo(stage, fxmlPath);
    }

    public static void navigateToWithNode(javafx.scene.Node sourceNode, String fxmlPath, ControllerSetupCallback controllerSetup) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        navigateTo(stage, fxmlPath, controllerSetup);
    }

    @FunctionalInterface
    public interface ControllerSetupCallback {
        void setup(Object controller);
    }
}