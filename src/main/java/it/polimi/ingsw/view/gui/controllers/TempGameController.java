package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.Trio;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;

import java.util.*;


public class TempGameController {
    public Pair<Integer, Integer> screenResolution;
    public double screenRatio;

    public Pair<Double, Double> rawCellDimension;
    public Pair<Double, Double> rawCardDimension;
    public double targetCellWidth;
    public Pair<Double, Double> adjustedCellDimensions;
    public Pair<Double, Double> adjustedCardDimensions;
    public double cardCompressionFactor;    // target = 100 --> 0.1292, target = 200 -> 0.2584

    public Integer DEFAULT_OBJECTIVE_CARD_BACK = 86;

    public List<UserInfo> players;
    public Map<UserInfo, PlayerToken> userInfoToToken;
    public SlimGameModel slimGameModel;

    public List<Integer> commonObjectives;

    public ImageView firstObjectiveSlot;
    public ImageView secondObjectiveSlot;
    public ImageView secretObjectiveSlot;

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
    public GridPane playerBoardGridPane;

    @FXML
    public TabPane leftTabPane;

    @FXML
    public TabPane rightTabPane;

    @FXML
    public Button leftPanelButton;

    @FXML
    public Button rightPanelButton;

    @FXML
    public ImageView resourceDeckImageView;

    @FXML
    public ImageView firstResourceImageView;

    @FXML
    public ImageView secondResourceImageView;

    @FXML
    public ImageView goldDeckImageView;

    @FXML
    public ImageView firstGoldImageView;

    @FXML
    public ImageView secondGoldImageView;

    @FXML
    public VBox playersList;

    public PlayerToken currentToken;

    @FXML
    public HBox currentHandPanel;

    @FXML
    public HBox currentTopPanel;

    @FXML
    public ScrollPane currentScrollPane;

    public Map<PlayerToken, ScrollPane> tokenToScrollPane;
    public Map<PlayerToken, ScrollPane> tokenToHandPanel;
    public Map<PlayerToken, ScrollPane> tokenToTopPanel;

    private Pair<Integer, Integer> gridCellsCount;

    public void initialize(GUI gui) {
        screenResolution = new Pair<>(1440, 900);
        screenRatio =  0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);

        UserInfo firstUserInfo = new UserInfo(new User("Andrea"));
        UserInfo secondUserInfo = new UserInfo(new User("Maradona"));
        UserInfo thirdUserInfo = new UserInfo(new User("John"));
        players = new ArrayList<>(Arrays.asList(firstUserInfo, secondUserInfo, thirdUserInfo));

        userInfoToToken = new HashMap<>() {{
            put(firstUserInfo, PlayerToken.RED);
            put(secondUserInfo, PlayerToken.BLUE);
            put(thirdUserInfo, PlayerToken.YELLOW);
        }};

        slimGameModel = new SlimGameModel(
                new HashMap<>() {{
                    put(PlayerToken.RED, new HashMap<>() {{
                        put(0, new Trio<>(80, CardSide.FRONT, new Coords(0,0)));
                        }});
                    put(PlayerToken.BLUE, new HashMap<>() {{
                        put(0, new Trio<>(81, CardSide.BACK, new Coords(0,0)));
                    }});
                    put(PlayerToken.YELLOW, new HashMap<>() {{
                        put(0, new Trio<>(82, CardSide.FRONT, new Coords(0,0)));
                    }});
                }},
                new HashMap<>() {{
                    put(PlayerToken.RED, new Trio<>(0,1,7));
                    put(PlayerToken.BLUE, new Trio<>(10,11,17));
                    put(PlayerToken.YELLOW, new Trio<>(20,21,27));
                }},
                new HashMap<>() {{
                    put(PlayerToken.RED, new HashMap<>() {{
                        put(Resources.PLANT, 0);
                        put(Resources.ANIMAL, 0);
                        put(Resources.FUNGI, 0);
                        put(Resources.INSECT, 0);
                        put(Items.QUILL, 0);
                        put(Items.INKWELL, 0);
                        put(Items.MANUSCRIPT, 0);
                    }});
                    put(PlayerToken.BLUE, new HashMap<>() {{
                        put(Resources.PLANT, 0);
                        put(Resources.ANIMAL, 0);
                        put(Resources.FUNGI, 0);
                        put(Resources.INSECT, 0);
                        put(Items.QUILL, 0);
                        put(Items.INKWELL, 0);
                        put(Items.MANUSCRIPT, 0);
                    }});
                    put(PlayerToken.YELLOW, new HashMap<>() {{
                        put(Resources.PLANT, 0);
                        put(Resources.ANIMAL, 0);
                        put(Resources.FUNGI, 0);
                        put(Resources.INSECT, 0);
                        put(Items.QUILL, 0);
                        put(Items.INKWELL, 0);
                        put(Items.MANUSCRIPT, 0);
                    }});
                }},
                new HashMap<>() {{
                    put(PlayerToken.RED, 86);
                    put(PlayerToken.BLUE, 87);
                    put(PlayerToken.YELLOW, 88);
                }},
                new ArrayList<>(Arrays.asList(89, 101)),
                new ArrayList<>(Arrays.asList(new Pair<>(false, Resources.PLANT), new Pair<>(false, Resources.FUNGI), new Pair<>(false, null), new Pair<>(false, null))),
                new Pair<>(30, 31),
                new Pair<>(8, 18),
                new HashMap<>() {{
                    put(PlayerToken.RED, 0);
                    put(PlayerToken.BLUE, 0);
                    put(PlayerToken.YELLOW, 0);
                }}
        );

        currentToken = PlayerToken.RED;

        rawCellDimension = new Pair<>(774.0, 397.0);
        rawCardDimension = new Pair<>(993.0, 662.0);

        targetCellWidth = 100;
        cardCompressionFactor = targetCellWidth / rawCellDimension.first;

        adjustedCellDimensions = new Pair<>(rawCellDimension.first * cardCompressionFactor, rawCellDimension.second * cardCompressionFactor);
        adjustedCardDimensions = new Pair<>(rawCardDimension.first * cardCompressionFactor, rawCardDimension.second * cardCompressionFactor);

        gridCellsCount = new Pair<>(81, 81);

        zoomScale = 1;
        zoomIncrement = 0.1;

        commonObjectives = Arrays.asList(DEFAULT_OBJECTIVE_CARD_BACK, DEFAULT_OBJECTIVE_CARD_BACK);

        firstObjectiveSlot.setImage(new Image("images/cards/backs/" + commonObjectives.getFirst() + ".png"));
        secondObjectiveSlot.setImage(new Image("images/cards/backs/" + commonObjectives.get(1) + ".png"));

        secretObjectiveSlot.setImage(new Image("images/cards/backs/" + DEFAULT_OBJECTIVE_CARD_BACK + ".png"));

        initializePlayerBoardGrid();
        initializeScrollPane();
        initializeSidePanels();
        initializePlayersList(players);

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

    public void initializeSidePanels() {

    }

    public void initializePlayersList(List<UserInfo> players) {
        int playersCount = players.size();

        double vBoxWidth = 269;

        Pair<Double, Double> playerImageViewDimensions;
        switch (playersCount) {
            case 2 -> playerImageViewDimensions = new Pair<>(vBoxWidth, 90.0);
            case 3 -> playerImageViewDimensions = new Pair<>(vBoxWidth, 58.33);
            case 4 -> playerImageViewDimensions = new Pair<>(vBoxWidth, 42.5);
            default -> throw new RuntimeException("Unsupported number of players");
        }

        boolean first = true;
        for(int i = 0; i < playersCount; i++) {
            ImageView playerEntry = new ImageView(new Image("images/cards/backs/" + DEFAULT_OBJECTIVE_CARD_BACK + ".png"));
            playerEntry.setPreserveRatio(false);

            playerEntry.setFitWidth(playerImageViewDimensions.first);
            playerEntry.setFitHeight(playerImageViewDimensions.second);

            playersList.getChildren().add(playerEntry);
            if(!first) VBox.setMargin(playerEntry, new Insets(5.0, 0.0, 0.0, 0.0));
            first = false;
        }
    }

    public void firstGuiUpdate() {

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
        if(!tokenToScrollPane.containsKey(playerToken)) throw new RuntimeException("player not found");

        ScrollPane newScrollPane = tokenToScrollPane.get(playerToken);
        mainStackPane.getChildren().set(mainStackPane.getChildren().size() - 2, tokenToScrollPane.get(playerToken));
    }

    public void handleOnMouseClickedLeftPanelButton(MouseEvent event) {
        leftTabPane.setVisible(!leftTabPane.isVisible());
        leftTabPane.setMouseTransparent(!leftTabPane.isMouseTransparent());
        event.consume();
    }

    public void handleOnMouseClickedRightPanelButton(MouseEvent event) {
        rightTabPane.setVisible(!rightTabPane.isVisible());
        rightTabPane.setMouseTransparent(!rightTabPane.isMouseTransparent());
        event.consume();
    }
}