package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.usermanagement.Lobby;
import it.polimi.ingsw.view.gui.controllers.Controller;
import it.polimi.ingsw.view.gui.controllers.LobbyController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneTester extends Application {
    @Override
    public void start(Stage primaryStage) {
        String sceneName = "lobby";

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/" + sceneName + "View.fxml"));

        Parent root = null;
        try { root = fxmlLoader.load(); } catch (Exception e) { e.printStackTrace(); }

        Controller controller = fxmlLoader.getController();
        controller.initialize();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/" + sceneName + "View.css");

        primaryStage.setTitle(sceneName);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
