package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;

import java.util.Map;


public class TempGameController {
    public Pair<Integer, Integer> screenResolution;
    public double screenRatio;

    public Pair<Double, Double> rawCellDimension;
    public Pair<Double, Double> rawCardDimension;
    public double targetCellWidth;
    public Pair<Double, Double> adjustedCellDimensions;
    public Pair<Double, Double> adjustedCardDimensions;
    public double cardCompressionFactor;

    public Map<PlayerToken, GridPane> tokenToGridBoard;
    public PlayerToken selfPlayerToken;

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

    private Pair<Integer, Integer> gridCellsCount;

    public void initialize(GUI gui) {
        screenResolution = new Pair<>(1440, 900);
        screenRatio =  0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);

        rawCellDimension = new Pair<>(774.0, 397.0);
        rawCardDimension = new Pair<>(993.0, 662.0);

        targetCellWidth = 100;
        cardCompressionFactor = targetCellWidth / rawCellDimension.first;   // target = 100 --> 0.1292, target = 200 -> 0.2584

        adjustedCellDimensions = new Pair<>(rawCellDimension.first * cardCompressionFactor, rawCellDimension.second * cardCompressionFactor);
        adjustedCardDimensions = new Pair<>(rawCardDimension.first * cardCompressionFactor, rawCardDimension.second * cardCompressionFactor);

        gridCellsCount = new Pair<>(81, 81);

        zoomScale = 1;
        zoomIncrement = 0.1;

        initializePlayerBoardGrid();
        initializeScrollPane();

        handlePlayedCardEvent(PlayerToken.RED, 85, CardSide.BACK, new Coords(0, 0));
        handlePlayedCardEvent(PlayerToken.RED, 52, CardSide.FRONT, new Coords(-1, -1));
    }

    public void initializePlayerBoardGrid() {
        playerBoardGridPane.setPadding(new Insets(adjustedCellDimensions.second, adjustedCellDimensions.second, adjustedCellDimensions.second, adjustedCellDimensions.second));

        for(int i = 0; i < gridCellsCount.first; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight((double) adjustedCellDimensions.second);
            row.setMinHeight((double) adjustedCellDimensions.second);
            row.setVgrow(javafx.scene.layout.Priority.NEVER);
            playerBoardGridPane.getRowConstraints().add(row);

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth((double) adjustedCellDimensions.first);
            column.setMinWidth((double) adjustedCellDimensions.first);
            column.setHgrow(javafx.scene.layout.Priority.NEVER);
            playerBoardGridPane.getColumnConstraints().add(column);
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

    /**
     * Handles zooming with "CTRL +" and "CTRL -" commands.
     *
     * @param event key pressed KeyEvent
     */
    @FXML
    private void handleKeyPressedEvent(KeyEvent event) {
        if(!event.isControlDown()) return;

        switch(event.getCode()) {
            case PLUS, EQUALS -> zoom(zoomIncrement);
            case MINUS -> zoom(-zoomIncrement);
        }
    }

    /**
     * Allows to zoom the selected board.
     *
     * @param zoomIncrement zoom factor
     */
    private void zoom(double zoomIncrement) {
        // TODO
        if(zoomScale + zoomIncrement < 0 || zoomScale + zoomIncrement > 2) return;

        zoomScale += zoomIncrement;

        playerBoardGridPane.setScaleX(zoomScale);
        playerBoardGridPane.setScaleY(zoomScale);
    }

    public void handlePlayedCardEvent(PlayerToken playerToken, Integer cardId, CardSide cardSide, Coords coords) {
        String path = "images/cards/" + (cardSide == CardSide.FRONT ? "fronts" : "backs")  + "/" + cardId + ".png";

        ImageView cardImage = new ImageView(new Image(path));
        cardImage.setEffect(new DropShadow());

        cardImage.setFitWidth(rawCardDimension.first * cardCompressionFactor);
        cardImage.setFitHeight(rawCardDimension.second * cardCompressionFactor);

        StackPane stackPane = new StackPane(cardImage);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);

        playerBoardGridPane.add(stackPane, coords.x + (gridCellsCount.first - 1)/ 2, coords.y + (gridCellsCount.second - 1)/ 2);
    }

    /**
     * Allows to see other player's board, by switching second to last element of the mainStackPane.
     *
     * @param playerToken token of the player whose board the user is interested to see
     */
    public void switchGrid(PlayerToken playerToken) {
        if(!tokenToGridBoard.containsKey(playerToken)) throw new RuntimeException("player not found");

        GridPane newGridPane = tokenToGridBoard.get(playerToken);
        mainStackPane.getChildren().set(mainStackPane.getChildren().size() - 2, tokenToGridBoard.get(playerToken));
    }
}