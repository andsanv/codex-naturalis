package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.gui.controllers.tempController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneTester extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/tempView.fxml"));
        Parent root = null;
        try { root = fxmlLoader.load(); } catch (Exception e) { e.printStackTrace(); }

        tempController controller = fxmlLoader.getController();
        // controller.initialize();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/tempView.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
