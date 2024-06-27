package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.gui.controllers.LobbyController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneTester extends Application {
    @Override
    public void start(Stage primaryStage) {
        String sceneName = "lobbyView";

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/" + sceneName + ".fxml"));

        Parent root = null;
        try { root = fxmlLoader.load(); } catch (Exception e) { e.printStackTrace(); }

        LobbyController controller = fxmlLoader.getController();
        controller.initialize();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/" + sceneName + ".css");

        primaryStage.setTitle(sceneName);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
