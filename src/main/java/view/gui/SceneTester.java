package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneTester extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/gameView.fxml"));
        Parent root = null;
        try { root = fxmlLoader.load(); } catch (Exception e) { e.printStackTrace(); }


        // tempController controller = fxmlLoader.getController();
        // controller.initialize();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/gameView.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Application");

        primaryStage.setResizable(false);
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
