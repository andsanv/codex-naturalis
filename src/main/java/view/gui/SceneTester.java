package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.gui.controllers.GameController;

public class SceneTester extends Application {

    @Override
    public void start(Stage primaryStage) {
        String sceneToLoad = "gameView";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/" + sceneToLoad + ".fxml"));
        Parent root = null;
        try { root = fxmlLoader.load(); } catch (Exception e) { e.printStackTrace(); }

        if (sceneToLoad.equals("gameView")) {
            GameController controller = fxmlLoader.getController();
            controller.initializeTest();
            System.out.println("initialized");
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/" + sceneToLoad + ".css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Codex Naturalis");

        primaryStage.setResizable(false);
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
