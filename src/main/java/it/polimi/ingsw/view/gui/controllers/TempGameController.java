package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public Pair<Double, Double> rawCardsDimension;
    public double cardsRatio;
    public double targetCardWidth;
    public Pair<Double, Double> adjustedCardsDimension;
    public double cardCompressionFactor;

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
    private double initialGridPaneWidth;
    private double initialGridPaneHeight;
    private double currentGridPaneWidth;
    private double currentGridPaneHeight;
    private Pair<Integer, Integer> gridCellsCount;

    public void initialize(GUI gui) {
        screenResolution = new Pair<>(1440, 900);
        screenRatio =  0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);

        rawCardsDimension = new Pair<>(774.0, 397.0);
        targetCardWidth = 200;
        cardsRatio = rawCardsDimension.first / rawCardsDimension.second;
        cardCompressionFactor = targetCardWidth / rawCardsDimension.first;
        adjustedCardsDimension = new Pair<>(targetCardWidth, targetCardWidth / cardsRatio);

        gridCellsCount = new Pair<>(2, 2);

        zoomScale = 1;
        zoomIncrement = 0.1;

        initializePlayerBoardGrid();
        initializeScrollPane();

        handlePlayedCardEvent(PlayerToken.RED, 52, CardSide.FRONT, new Coords(0, 0));
    }

    public void initializePlayerBoardGrid() {
        playerBoardGridPane.setPadding(new Insets(adjustedCardsDimension.second, adjustedCardsDimension.second, adjustedCardsDimension.second, adjustedCardsDimension.second));

        for(int i = 0; i < gridCellsCount.first; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight((double) adjustedCardsDimension.second);
            row.setMinHeight((double) adjustedCardsDimension.second);
            playerBoardGridPane.getRowConstraints().add(row);

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth((double) adjustedCardsDimension.first);
            column.setMinWidth((double) adjustedCardsDimension.first);
            playerBoardGridPane.getColumnConstraints().add(column);
        }

        initialGridPaneWidth = currentGridPaneWidth = gridCellsCount.first * adjustedCardsDimension.first;
        initialGridPaneHeight = currentGridPaneHeight = gridCellsCount.second * adjustedCardsDimension.second;
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

        double zoomFactor = 0;

        switch(event.getCode()) {
            case PLUS, EQUALS -> zoom(zoomIncrement);
            case MINUS -> zoom(-zoomIncrement);
        }

        // applyZoom(zoomFactor, mainScrollPane.getViewportBounds().getWidth() / 2, mainScrollPane.getViewportBounds().getHeight() / 2);
    }

    /**
     * Allows to zoom the selected board.
     *
     * @param zoomIncrement zoom factor
     */
    private void zoom(double zoomIncrement) {
        // TODO fix zoom
        if(zoomScale + zoomIncrement < 0 || zoomScale + zoomIncrement > 2) return;

        zoomScale += zoomIncrement;

        double previousX = mainScrollPane.getHvalue();
        double previousY = mainScrollPane.getVvalue();

        playerBoardGridPane.setScaleX(zoomScale);
        playerBoardGridPane.setScaleY(zoomScale);

        double previousGridPaneWidth = currentGridPaneWidth;
        double previousGridPaneHeight = currentGridPaneHeight;


    }

    public void handlePlayedCardEvent(PlayerToken playerToken, Integer cardId, CardSide cardSide, Coords coords) {
        String path = "images/cards/" + (cardSide == CardSide.FRONT ? "fronts" : "backs")  + "/" + cardId + ".png";

        ImageView card1 = new ImageView();
        card1.setImage(new Image(path));
        card1.setScaleX(cardCompressionFactor);
        card1.setScaleY(cardCompressionFactor);

        playerBoardGridPane.add(card1, 0, 0);

        playerBoardGridPane.getChildren().forEach(x -> System.out.println(x.getClass()));
    }
}