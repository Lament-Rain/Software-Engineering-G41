package service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ToastService {

    public enum ToastType {
        SUCCESS,
        ERROR,
        WARNING,
        INFO
    }

    private static final int TOAST_DURATION = 3000;
    private static final int FADE_DURATION = 300;

    public static void showToast(Stage owner, String message, ToastType type) {
        showToast(owner, message, type, TOAST_DURATION);
    }

    public static void showToast(Stage owner, String message, ToastType type, int duration) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(8);
        root.setPadding(new Insets(12, 20, 12, 20));
        root.setMaxWidth(400);

        String bgColor;
        String icon;
        switch (type) {
            case SUCCESS:
                bgColor = "#28a745";
                icon = "\u2713 ";
                break;
            case ERROR:
                bgColor = "#dc3545";
                icon = "\u2717 ";
                break;
            case WARNING:
                bgColor = "#ffc107";
                icon = "\u26A0 ";
                break;
            case INFO:
            default:
                bgColor = "#4a6fa5";
                icon = "\u2139 ";
                break;
        }

        root.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 4);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: white;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-text-fill: white;" +
            "-fx-wrap-text: true;"
        );

        if (type == ToastType.WARNING) {
            messageLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-text-fill: #212121;" +
                "-fx-wrap-text: true;"
            );
        }

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, messageLabel);
        root.getChildren().add(content);

        Scene scene = new Scene(root);
        scene.setFill(null);

        toastStage.setScene(scene);

        toastStage.setX(getToastX(toastStage));
        toastStage.setY(getToastY(toastStage));

        toastStage.show();

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(duration), event -> {
                Timeline fadeOut = new Timeline();
                fadeOut.getKeyFrames().add(
                    new KeyFrame(Duration.millis(FADE_DURATION), event2 -> {
                        toastStage.close();
                    })
                );
                fadeOut.play();
            })
        );
        timeline.play();
    }

    private static double getToastX(Stage toastStage) {
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return screenBounds.getMaxX() - 420;
    }

    private static double getToastY(Stage toastStage) {
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return screenBounds.getMinY() + 20;
    }

    public static class HBox extends javafx.scene.layout.HBox {
        public HBox(int spacing) {
            super(spacing);
        }
    }
}
