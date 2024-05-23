package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

import java.awt.event.MouseEvent;

public class GameView {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private GridPane gridPane;

    private Scale scaleTransform;
    private double scaleValue = 1;
    private final double scaleIncrement = 0.1;

    @FXML
    public void initialize() {

        scrollPane.prefViewportWidthProperty().bind(gridPane.widthProperty());
        scrollPane.prefViewportHeightProperty().bind(gridPane.heightProperty());
        StackPane.setAlignment(scrollPane, Pos.CENTER);
        // Initialize scale transform
        scaleTransform = new Scale(scaleValue, scaleValue);
        stackPane.getTransforms().add(scaleTransform);

        // Add key event handling
        Scene scene = scrollPane.getScene();
        if (scene != null) {
            setupKeyEvents(scene);
        } else {
            scrollPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    setupKeyEvents(newScene);
                }
            });
        }

    }

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case PLUS:
                case EQUALS: // Handle both + and = (shift + = is + on some keyboards)
                    //zoomIn();
                    break;
                case MINUS:
                    //zoomOut();
                    break;
                default:
                    break;
            }
        });
    }

    private void zoomIn() {
        scaleValue += scaleIncrement;
        applyScale();
    }

    private void zoomOut() {
        scaleValue -= scaleIncrement;
        applyScale();
    }

    private void applyScale() {
        double oldHValue = scrollPane.getHvalue();
        double oldVValue = scrollPane.getVvalue();

        scaleTransform.setX(scaleValue);
        scaleTransform.setY(scaleValue);

        double contentWidth = gridPane.getBoundsInLocal().getWidth();
        double contentHeight = gridPane.getBoundsInLocal().getHeight();

        // Calculate new scroll positions to keep the viewport centered
        double newHValue = (oldHValue + 0.5 * scrollPane.getViewportBounds().getWidth() / contentWidth) * (contentWidth - scrollPane.getViewportBounds().getWidth()) / (contentWidth - scaleValue * scrollPane.getViewportBounds().getWidth());
        double newVValue = (oldVValue + 0.5 * scrollPane.getViewportBounds().getHeight() / contentHeight) * (contentHeight - scrollPane.getViewportBounds().getHeight()) / (contentHeight - scaleValue * scrollPane.getViewportBounds().getHeight());

        scrollPane.setHvalue(newHValue);
        scrollPane.setVvalue(newVValue);
    }
}
