package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

public class GameView {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox playerHand;

    @FXML
    private GridPane gridPane;



    private Scale scaleTransform;
    private double scaleValue = 1;
    private final double scaleIncrement = 0.1;

    @FXML
    public void initialize() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/gameView2.fxml"));

        scrollPane.prefViewportWidthProperty().bind(gridPane.widthProperty());
        scrollPane.prefViewportHeightProperty().bind(gridPane.heightProperty());
        StackPane.setAlignment(scrollPane, Pos.CENTER);
        // Initialize scale transform
        scaleTransform = new Scale(scaleValue, scaleValue);
        stackPane.getTransforms().add(scaleTransform);

        for (Node node : playerHand.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackCard = (StackPane) node;
                stackCard.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }

        for (int i = 0; i <=41; i++) {
            for (int j = 0; j <= 41; j++) {
                StackPane cell = new StackPane();
                cell.setMinHeight(331);
                cell.setPrefHeight(331);
                cell.setMaxHeight(331);
                cell.setMinHeight(496.5);
                cell.setPrefHeight(496.5);
                cell.setMaxHeight(496.5);
                ImageView imageView = new ImageView();
                imageView.setFitHeight(331);
                imageView.setFitWidth(496.5);
                imageView.setPreserveRatio(true);
                if (i==1 && j==1) {
                    Image image = new Image("/images/cards/fronts/001.png");
                    imageView.setImage(image);
                }
                cell.getChildren().add(imageView);
            }
        }

        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                node.setOnMouseEntered(mouseEvent -> {
                    try {
                        boardCellHoverEnter(mouseEvent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                node.setOnMouseExited(mouseEvent -> {
                    try {
                        boardCellHoverExit(mouseEvent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }



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

    @FXML
    public void handCardHoverEnter(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        System.out.println("Mouse entered ImageView");
        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: blue; -fx-border-width: 10;");
        sourceImageView.applyCss();
    }

    @FXML
    public void handCardHoverExit(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        System.out.println("Mouse exited ImageView");
        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        sourceImageView.applyCss();
    }

    @FXML
    public void boardCellHoverEnter(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add((ImageView) mouseEvent.getSource());
        stackPane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
        stackPane.applyCss();
    }

    @FXML
    public void boardCellHoverExit(javafx.scene.input.MouseEvent mouseEvent) throws IOException {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add((ImageView) mouseEvent.getSource());
        stackPane.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        stackPane.applyCss();
    }


}
