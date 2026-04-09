package cs151.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.flashmaster.MainLayout;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainLayout mainLayout = new MainLayout();

        StackPane root = new StackPane(mainLayout);
        root.getStyleClass().add("app-root");
        root.setPadding(new javafx.geometry.Insets(24));

        Scene scene = new Scene(root, 1100, 720);

        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("FlashMaster");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
