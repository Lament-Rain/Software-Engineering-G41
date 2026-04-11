import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.DataStorage;

public class MainGUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 初始化数据存储
        service.DataStorage.initialize();
        
        // 加载登录页面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        // 获取控制器并设置stage
        controller.LoginController loginController = loader.getController();
        loginController.setStage(primaryStage);
        
        Scene scene = new Scene(root, 800, 600);
        // 添加样式表
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("BUPT国际学校TA招聘系统");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}