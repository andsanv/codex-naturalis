package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;


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
        cardsDimension = new Pair<>(200.0, 100.0);

        zoomScale = 1;
        zoomIncrement = 0.1;

        initializePlayerBoardGrid();
        initializeScrollPane();
    }

    public void initializePlayerBoardGrid() {
        playerBoardGridPane.setPadding(new Insets(cardsDimension.second, cardsDimension.second, cardsDimension.second, cardsDimension.second));

        for(int i = 0; i < 81; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight((double) cardsDimension.second);
            row.setMinHeight((double) cardsDimension.second);
            playerBoardGridPane.getRowConstraints().add(row);

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth((double) cardsDimension.first);
            column.setMinWidth((double) cardsDimension.first);
            playerBoardGridPane.getColumnConstraints().add(column);

            StackPane stackPane = new StackPane();

            playerBoardGridPane.add(stackPane, 0, i);
        }
    }

    public void initializeScrollPane() {
        mainScrollPane.setHvalue(0.5);
        mainScrollPane.setVvalue(0.5);
    }

    /**
     * Updates start coordinates of an eventual drag action.
     *
     * @param event press MouseEvent
     */
    @FXML
    private void handleOnMousePressed(MouseEvent event) {
        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
    }

    /**
     * Handles dragging mouse on the player board grid.
     * Calculates translation values, applies them, and updates last drag coordinates.
     *
     * @param event drag MouseEvent
     */
    @FXML
    private void handleMouseDragged(MouseEvent event) {
        double newViewportX = mainScrollPane.getHvalue() - (event.getSceneX() - dragStartX) / playerBoardGridPane.getWidth();
        double newViewportY = mainScrollPane.getVvalue() - (event.getSceneY() - dragStartY) / playerBoardGridPane.getHeight();

        mainScrollPane.setHvalue(newViewportX);
        mainScrollPane.setVvalue(newViewportY);

        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
    }

    @FXML
    private void handleKeyPressedEvent(KeyEvent event) {
        if(!event.isControlDown()) return;

        switch(event.getCode()) {
            case PLUS, EQUALS -> zoom(zoomIncrement);
            case MINUS -> zoom(-zoomIncrement);
        }
    }

    private void zoom(double zoomIncrement) {
        if(zoomScale + zoomIncrement < 0.6 || zoomScale + zoomIncrement > 1.4) return;

        zoomScale += zoomIncrement;

        double previousX = mainScrollPane.getHvalue();
        double previousY = mainScrollPane.getVvalue();

        playerBoardGridPane.setScaleX(zoomScale);
        playerBoardGridPane.setScaleY(zoomScale);

        mainScrollPane.setHvalue(previousX);
        mainScrollPane.setVvalue(previousY);
    }
}