package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;


import java.awt.*;
import java.sql.SQLOutput;

public class TempGameController {
    public Pair<Integer, Integer> screenResolution;
    public double screenRatio;
    public Pair<Double, Double> cardsDimension;

    private double zoomScale;
    private double zoomIncrement;

    // to handle mouse drag
    private double dragStartX;
    private double dragStartY;

    @FXML
    public StackPane mainStackPane;

    @FXML
    public ScrollPane mainScrollPane;

    @FXML
    private GridPane playerBoardGridPane;

    public void initialize(GUI gui) {
        screenResolution = new Pair<>(1440, 900);
        screenRatio =  0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);
        cardsDimension = new Pair<>(50.0, 100.0);

        zoomScale = 1;
        zoomIncrement = 0.1;

        initializePlayerBoardGrid();
    }

    public void initializePlayerBoardGrid() {
        playerBoardGridPane.setPadding(new Insets(cardsDimension.first, cardsDimension.second, cardsDimension.first, cardsDimension.second));

        for(int i = 0; i < 80; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight((double) 50);
            row.setMinHeight((double) 50);
            playerBoardGridPane.getRowConstraints().add(row);

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth((double) 100);
            column.setMinWidth((double) 100);
            playerBoardGridPane.getColumnConstraints().add(column);

            StackPane stackPane = new StackPane();

            playerBoardGridPane.add(stackPane, 0, i);
        }
    }



    @FXML
    private void handleOnMousePressed(MouseEvent event) {
        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        double offsetX = event.getSceneX() - dragStartX;
        double offsetY = event.getSceneY() - dragStartY;

        double newViewportX = mainScrollPane.getHvalue() - offsetX / playerBoardGridPane.getWidth();
        double newViewportY = mainScrollPane.getVvalue() - offsetY / playerBoardGridPane.getHeight();

        // Pair<Double, Double> offset = new Pair<>(event.getSceneX() - dragStartCoords.first, event.getSceneY() - dragStartCoords.second);
        // Pair<Double, Double> newViewportPosition = new Pair<>(mainScrollPane.getHvalue() - offset.first / playerBoardGridPane.getWidth(), mainScrollPane.getVvalue() - offset.second / playerBoardGridPane.getHeight());
        // TODO CHECK IF USEFUL Pair<Double, Double> maxCoords = new Pair<>(1 - mainScrollPane.getViewportBounds().getWidth() / playerBoardGridPane.getWidth(), 1 - mainScrollPane.getViewportBounds().getHeight() / playerBoardGridPane.getHeight());

        // if(0 <= newViewportPosition.first && newViewportPosition.first < maxCoords.first + 2000)
        mainScrollPane.setHvalue(newViewportX);

        // if(0 <= newViewportPosition.second && newViewportPosition.second < maxCoords.second + 2000)
        mainScrollPane.setVvalue(newViewportY);

        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();}
}