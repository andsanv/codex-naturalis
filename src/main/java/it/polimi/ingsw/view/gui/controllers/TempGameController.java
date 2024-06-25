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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TempGameController {
    /* DIMENSIONS AND CONSTANTS */
    // screen
    public final Pair<Integer, Integer> screenResolution = new Pair<>(1440, 900);
    public final double screenRatio = 0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);;

    // zoom
    private double currentZoomScale = 1;
    private final double zoomIncrement = 0.1;

    // cells and cards
    public final Pair<Double, Double> rawCellDimensions = new Pair<>(774.0, 397.0);;
    public final Pair<Double, Double> rawCardDimensions = new Pair<>(993.0, 662.0);;
    public final double targetCellWidth = 100;
    public final double cardCompressionFactor = targetCellWidth / rawCellDimensions.first;    // target = 100 --> 0.1292, target = 200 -> 0.2584
    public final Pair<Double, Double> adjustedCellDimensions = new Pair<>(rawCellDimensions.first * cardCompressionFactor, rawCellDimensions.second * cardCompressionFactor);
    public final Pair<Double, Double> adjustedCardDimensions = new Pair<>(rawCardDimensions.first * cardCompressionFactor, rawCardDimensions.second * cardCompressionFactor);
    public final Pair<Integer, Integer> gridCellsCount = new Pair<>(81, 81);

    public final Integer DEFAULT_OBJECTIVE_CARD_ID = 86;


    /* GRAPHIC STRUCTURE */
    // fixed
    @FXML public AnchorPane mainAnchorPane;
    @FXML public StackPane mainStackPane;
    @FXML public TabPane leftTabPane;
    @FXML public TabPane rightTabPane;
    @FXML public Button leftPanelButton;
    @FXML public Button rightPanelButton;

    // dynamic
    @FXML public ScrollPane currentScrollPane;
    @FXML public GridPane currentGridPane;
    @FXML public HBox currentHandHBox;

    // in-game objects
    @FXML public VBox playersList;

    @FXML public ImageView resourceDeckImageView;
    @FXML public ImageView firstResourceImageView;
    @FXML public ImageView secondResourceImageView;
    @FXML public ImageView goldDeckImageView;
    @FXML public ImageView firstGoldImageView;
    @FXML public ImageView secondGoldImageView;

    @FXML public ImageView firstObjectiveSlot;
    @FXML public ImageView secondObjectiveSlot;
    @FXML public ImageView secretObjectiveSlot;

    // in-game elements
    @FXML public Text animalResourcesCounter;
    @FXML public Text plantResourcesCounter;
    @FXML public Text fungiResourcesCounter;
    @FXML public Text insectResourcesCounter;
    @FXML public Text manuscriptItemsCounter;
    @FXML public Text inkwellItemsCounter;
    @FXML public Text quillItemsCounter;


    /* NETWORK */
    public List<UserInfo> players;
    public Map<UserInfo, PlayerToken> userInfoToToken;
    public SlimGameModel slimGameModel;


    /* GAME FLOW */
    public PlayerToken selfPlayerToken;
    public PlayerToken currentPlayerToken;
    public Map<PlayerToken, ScrollPane> tokenToScrollPane = new HashMap<>();
    public Map<PlayerToken, HBox> tokenToHandHBox = new HashMap<>();


    /* HELPERS */
    // card click
    public Timer cardClickTimer;
    public boolean cardClickTimerExpired;
    public long cardClickDelay = 500;

    // mouse drag
    private double dragStartX;
    private double dragStartY;

    // cards playability
    private boolean clearList = false;
    public Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability;
    public List<Coords> availableSlots;

    // miscellaneous structures
    public Pair<ImageView, List<Integer>> resourceDeck;
    public Pair<ImageView, List<Integer>> goldDeck;
    public Pair<Pair<ImageView, AtomicInteger>, Pair<ImageView, AtomicInteger>> resourceVisibleList;
    public Pair<Pair<ImageView, AtomicInteger>, Pair<ImageView, AtomicInteger>> goldVisibleList;

    Map<ImageView, Pair<ImageView, List<Integer>>> deckViewToDeck;
    Map<ImageView, Supplier<Integer>> visibleSlotToCardId;
    Map<ImageView, Pair<ImageView, List<Integer>>> visibleSlotToDeck;
    Map<ImageView, Consumer<Integer>> visibleSlotToVisibleListSetter;

    public List<CardSide> visibleHandCardsSides;



    /**
     * Method to call to initialize the controller and the scene.
     * Sets up all needed structures, panes, and objects.
     *
     * @param gui the main application
     * @throws InterruptedException if initialization is interrupted
     */
    public void initialize(GUI gui) throws InterruptedException {
        // build default view


        // simulating game setup phase
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
                    put(PlayerToken.RED, new ArrayList<>(Arrays.asList(0,1,7)));
                    put(PlayerToken.BLUE, new ArrayList<>(Arrays.asList(10,11,17)));
                    put(PlayerToken.YELLOW, new ArrayList<>(Arrays.asList(20,21,27)));
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
                        put(Resources.INSECT, 7);
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
                new ArrayList<>(Arrays.asList(12, 34, 7, 5, 10, 23, 24)),
                new ArrayList<>(Arrays.asList(52, 44, 67, 75, 40, 63, 64)),
                new ArrayList<>(Arrays.asList(30, 31)),
                new ArrayList<>(Arrays.asList(8, 18)),
                new HashMap<>() {{
                    put(PlayerToken.RED, 0);
                    put(PlayerToken.BLUE, 0);
                    put(PlayerToken.YELLOW, 0);
                }}
        );

        resourceDeck = new Pair<>(resourceDeckImageView, slimGameModel.resourceDeck);
        goldDeck = new Pair<>(goldDeckImageView, slimGameModel.goldDeck);

        deckViewToDeck = new HashMap<>() {{
            put(resourceDeckImageView, resourceDeck);
            put(goldDeckImageView, goldDeck);
        }};

        visibleSlotToCardId = new HashMap<>() {{
            put(firstResourceImageView, () -> slimGameModel.visibleResourceCardsList.get(0));
            put(secondResourceImageView, () -> slimGameModel.visibleResourceCardsList.get(1));
            put(firstGoldImageView, () -> slimGameModel.visibleGoldCardsList.get(0));
            put(secondGoldImageView, () -> slimGameModel.visibleGoldCardsList.get(1));
        }};

        visibleSlotToVisibleListSetter = new HashMap<>() {{
            put(firstResourceImageView, x -> slimGameModel.visibleResourceCardsList.set(0, x));
            put(secondResourceImageView, x -> slimGameModel.visibleResourceCardsList.set(1, x));
            put(firstGoldImageView, x -> slimGameModel.visibleGoldCardsList.set(0, x));
            put(secondGoldImageView, x -> slimGameModel.visibleGoldCardsList.set(1, x));
        }};

        visibleSlotToDeck = new HashMap<>() {{
            put(firstResourceImageView, resourceDeck);
            put(secondResourceImageView, resourceDeck);
            put(firstGoldImageView, goldDeck);
            put(secondGoldImageView, goldDeck);
        }};

        firstObjectiveSlot.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
        secondObjectiveSlot.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
        secretObjectiveSlot.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));

        initializeGridPane(currentGridPane);
        initializeScrollPane(currentScrollPane);
        initializePlayersList(players);
        initializeDecks();


        ImageView defaultStarterCardView = new ImageView(new Image("images/miscellaneous/default_starter_card.png"));
        defaultStarterCardView.setEffect(new DropShadow());

        StackPane stackPane = new StackPane(defaultStarterCardView);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);

        defaultStarterCardView.setFitWidth(rawCardDimensions.first * cardCompressionFactor);
        defaultStarterCardView.setFitHeight(rawCardDimensions.second * cardCompressionFactor);

        currentGridPane.add(stackPane, 40, 40);

        initializeAllPlayers();

        selfPlayerToken = PlayerToken.RED;

        // simulate some turns

        cardsPlayability = new HashMap<>() {{
            put(0, new ArrayList<>(Arrays.asList(new Pair<>(CardSide.FRONT, true), new Pair<>(CardSide.BACK, false))));
            put(1, new ArrayList<>(Arrays.asList(new Pair<>(CardSide.FRONT, false), new Pair<>(CardSide.BACK, true))));
            put(7, new ArrayList<>(Arrays.asList(new Pair<>(CardSide.FRONT, false), new Pair<>(CardSide.BACK, false))));
        }};

        handlePlayedCardEvent(PlayerToken.RED, 45, CardSide.FRONT, new Coords(1,1));
        handlePlayedCardEvent(PlayerToken.BLUE, 45, CardSide.FRONT, new Coords(-1,1));

        visibleHandCardsSides = new ArrayList<>(Arrays.asList(CardSide.BACK, CardSide.BACK, CardSide.BACK));
        availableSlots = new ArrayList<>(Arrays.asList(new Coords(1,1), new Coords(-1,-1)));

        switchPlayerView(PlayerToken.RED);

        System.out.println(slimGameModel.tokenToHand.get(currentPlayerToken));
    }

    public void initializeGridPane(GridPane gridPane) {
        // gridPane.setPadding(new Insets(adjustedCellDimensions.second, adjustedCellDimensions.second, adjustedCellDimensions.second, adjustedCellDimensions.second));

        for(int i = 0; i < gridCellsCount.first; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(adjustedCellDimensions.second);
            row.setMinHeight(adjustedCellDimensions.second);
            row.setMaxHeight(adjustedCellDimensions.second);
            row.setVgrow(javafx.scene.layout.Priority.NEVER);
            gridPane.getRowConstraints().add(row);

            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(adjustedCellDimensions.first);
            column.setMinWidth(adjustedCellDimensions.first);
            column.setMaxWidth(adjustedCellDimensions.first);
            column.setHgrow(javafx.scene.layout.Priority.NEVER);
            gridPane.getColumnConstraints().add(column);
        }
    }

    public void initializeScrollPane(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }

    public void initializeDecks() {
        int cardId;

        // resource deck
        resourceDeckImageView.setImage(getCardImage(
                !slimGameModel.resourceDeck.isEmpty() ? slimGameModel.resourceDeck.getLast() : DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK
        ));
        resourceDeckImageView.setOnMouseClicked(this::handleDeckMouseClicked);

        // gold deck
        goldDeckImageView.setImage(getCardImage(
                !slimGameModel.goldDeck.isEmpty() ? slimGameModel.goldDeck.getLast() : DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK
        ));
        goldDeckImageView.setOnMouseClicked(this::handleDeckMouseClicked);

        // resource visible
        firstResourceImageView.setImage(getCardImage(slimGameModel.visibleResourceCardsList.get(0), CardSide.FRONT));
        firstResourceImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
        secondResourceImageView.setImage(getCardImage(slimGameModel.visibleResourceCardsList.get(1), CardSide.FRONT));
        secondResourceImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);

        // gold visible
        firstGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.get(0), CardSide.FRONT));
        firstGoldImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
        secondGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.get(1), CardSide.FRONT));
        secondGoldImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
    }

    public void initializePlayersList(List<UserInfo> players) {
        int playersCount = players.size();

        List<Image> playersImages = new ArrayList<>(Arrays.asList(
                new Image("images/players/first_player.png"),
                new Image("images/players/second_player.png"),
                new Image("images/players/third_player.png"),
                new Image("images/players/fourth_player.png")
        ));

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
            HBox playerEntry = new HBox();

            ImageView playerAvatar = new ImageView(playersImages.get(i));
            playerAvatar.setPreserveRatio(true);
            playerAvatar.setFitHeight(playerImageViewDimensions.second - 10);
            HBox.setMargin(playerAvatar, new Insets(5, 0, 0, 5));

            Label playerName = new Label(players.get(i).name);
            HBox.setMargin(playerName, new Insets(5, 0, 0, 5));

            playerEntry.getChildren().add(playerAvatar);
            playerEntry.getChildren().add(playerName);

            playersList.getChildren().add(playerEntry);
            if(!first) VBox.setMargin(playerEntry, new Insets(5.0, 0.0, 0.0, 0.0));
            first = false;
        }
    }

    public void initializeAllPlayers() {
        userInfoToToken.values().forEach(token -> {
            // set up scroll pane and grid pane
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setOnKeyPressed(this::handleKeyPressedEvent);
            initializeScrollPane(scrollPane);

            GridPane gridPane = new GridPane();
            gridPane.setOnMouseDragged(this::handleMouseDragged);
            gridPane.setOnMousePressed(this::handleOnMousePressed);
            initializeGridPane(gridPane);
            gridPane.setOnDragOver(this::handleDragOver);
            gridPane.setOnDragDropped(this::handleHandDragDropped);
            gridPane.setGridLinesVisible(false);

            scrollPane.setContent(gridPane);

            tokenToScrollPane.put(token, scrollPane);

            placeCard(token, slimGameModel.tokenToPlayedCards.get(token).get(0).first, slimGameModel.tokenToPlayedCards.get(token).get(0).second, slimGameModel.tokenToPlayedCards.get(token).get(0).third.x, slimGameModel.tokenToPlayedCards.get(token).get(0).third.y);

            // set up hand hbox
            HBox hbox = new HBox();

            AnchorPane.setLeftAnchor(hbox, 407.5);
            AnchorPane.setRightAnchor(hbox, 407.5);
            AnchorPane.setBottomAnchor(hbox, 20.0);

            Integer firstCardId = slimGameModel.tokenToHand.get(token).get(0);
            Integer secondCardId = slimGameModel.tokenToHand.get(token).get(1);
            Integer thirdCardId = slimGameModel.tokenToHand.get(token).get(2);

            ImageView firstCardImageView = new ImageView(new Image("images/cards/backs/" + firstCardId + ".png"));
            firstCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            firstCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            firstCardImageView.setOnMouseExited(this::handleHandMouseExited);
            firstCardImageView.setOnDragDetected(this::handleHandDragDetected);
            firstCardImageView.setOnDragDone(this::handleDragDone);
            firstCardImageView.setPreserveRatio(true);
            firstCardImageView.setFitWidth(195);
            firstCardImageView.setFitHeight(130);

            ImageView secondCardImageView = new ImageView(new Image("images/cards/backs/" + secondCardId + ".png"));
            secondCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            secondCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            secondCardImageView.setOnMouseExited(this::handleHandMouseExited);
            secondCardImageView.setOnDragDetected(this::handleHandDragDetected);
            secondCardImageView.setOnDragDone(this::handleDragDone);
            secondCardImageView.setPreserveRatio(true);
            secondCardImageView.setFitWidth(195);
            secondCardImageView.setFitHeight(130);

            ImageView thirdCardImageView = new ImageView(new Image("images/cards/backs/" + thirdCardId + ".png"));
            thirdCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            thirdCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            thirdCardImageView.setOnMouseExited(this::handleHandMouseExited);
            thirdCardImageView.setOnDragDetected(this::handleHandDragDetected);
            thirdCardImageView.setOnDragDone(this::handleDragDone);
            thirdCardImageView.setPreserveRatio(true);
            thirdCardImageView.setFitWidth(195);
            thirdCardImageView.setFitHeight(130);

            hbox.getChildren().addAll(firstCardImageView, secondCardImageView, thirdCardImageView);
            HBox.setMargin(firstCardImageView, new Insets(10.0, 0.0, 10.0, 10.0));
            HBox.setMargin(secondCardImageView, new Insets(10.0, 0.0, 10.0, 10.0));
            HBox.setMargin(thirdCardImageView, new Insets(10.0, 10.0, 10.0, 10.0));

            tokenToHandHBox.put(token, hbox);
        });
    }

    public Coords getIndexesFromCoords(Coords coords) {
        int x = coords.x + (gridCellsCount.first - 1) / 2;
        int y = coords.y + (gridCellsCount.first - 1) / 2;

        if(x < 0 || x > gridCellsCount.first || y < 0 || y > gridCellsCount.second) return null;

        return new Coords(x, y);
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
        double newViewportX = currentScrollPane.getHvalue() - (event.getSceneX() - dragStartX) / currentGridPane.getWidth();
        double newViewportY = currentScrollPane.getVvalue() - (event.getSceneY() - dragStartY) / currentGridPane.getHeight();

        currentScrollPane.setHvalue(newViewportX);
        currentScrollPane.setVvalue(newViewportY);

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
        if(currentZoomScale + zoomIncrement < 0 || currentZoomScale + zoomIncrement > 2) return;

        currentZoomScale += zoomIncrement;

        currentGridPane.setScaleX(currentZoomScale);
        currentGridPane.setScaleY(currentZoomScale);
    }

    public void handlePlayedCardEvent(PlayerToken playerToken, Integer cardId, CardSide cardSide, Coords coords) {
        if(playerToken != selfPlayerToken)
            placeCard(playerToken, cardId, cardSide, coords.x + (gridCellsCount.first - 1) / 2, - coords.y + (gridCellsCount.second - 1) /  2);

        // update data structures

    }


    /**
     * Allows to see other player's board, by switching the second to last element of the mainStackPane.
     *
     * @param playerToken token of the player whose board the user is interested to see
     */
    public void switchPlayerView(PlayerToken playerToken) {
        // switch grid pane
        if(!tokenToScrollPane.containsKey(playerToken)) throw new RuntimeException("player not found");

        ScrollPane nextScrollPane = tokenToScrollPane.get(playerToken);

        mainStackPane.getChildren().set(0, nextScrollPane);

        currentScrollPane = nextScrollPane;
        currentGridPane = (GridPane) currentScrollPane.getContent();

        mainAnchorPane.getChildren().set(2, tokenToHandHBox.get(playerToken));

        // switch hand
        currentHandHBox = tokenToHandHBox.get(playerToken);

        // update elements
        animalResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.ANIMAL).toString());
        plantResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.PLANT).toString());
        fungiResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.FUNGI).toString());
        insectResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.INSECT).toString());
        quillItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.QUILL).toString());
        inkwellItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.INKWELL).toString());
        manuscriptItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.MANUSCRIPT).toString());

        // switch token
        currentPlayerToken = playerToken;
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

    public void handleHandMouseClicked(MouseEvent event) {
        int handIndex = currentHandHBox.getChildren().indexOf((ImageView) event.getTarget());
        if (handIndex < 0 || handIndex > 2) throw new RuntimeException("Illegal index: " + handIndex);

        int cardId = slimGameModel.tokenToHand.get(currentPlayerToken).get(handIndex);
        CardSide oppositeCardSide = visibleHandCardsSides.get(handIndex) == CardSide.FRONT ? CardSide.BACK : CardSide.FRONT;
        visibleHandCardsSides.set(handIndex, oppositeCardSide);

        ((ImageView) currentHandHBox.getChildren().get(handIndex)).setImage(getCardImage(cardId, oppositeCardSide));

        if (isPlayable(slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex))) showCardsPlayability();
        else hideCardsPlayability();
    }

    public void handleHandMouseEntered(MouseEvent event) {
        int handIndex = currentHandHBox.getChildren().indexOf((ImageView) event.getTarget());

        if (isPlayable(slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex))) showCardsPlayability();
    }

    public void handleHandMouseExited(MouseEvent event) {
        int handIndex = currentHandHBox.getChildren().indexOf((ImageView) event.getTarget());

        if (isPlayable(slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex))) hideCardsPlayability();
    }

    public boolean isPlayable(int cardId, CardSide cardSide) {
         Pair<CardSide, Boolean> result =
                 cardsPlayability.get(cardId).stream()
                 .filter(x -> x.first == cardSide)
                 .findFirst()
                 .orElseThrow(() -> new RuntimeException("No such card found in cards playability"));

         return result.second;
    }

    public void handleHandDragDetected(MouseEvent event) {
        ImageView targetCard = (ImageView) event.getTarget();
        int handIndex = currentHandHBox.getChildren().indexOf(targetCard);
        if (handIndex < 0 || handIndex > 2) throw new RuntimeException("Illegal index: " + handIndex);

        Dragboard dragboard = targetCard.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();

        Image draggedCardImage = getCardImage(slimGameModel.tokenToHand.get(currentPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex));
        content.putString(String.valueOf(handIndex));

        PixelReader reader = draggedCardImage.getPixelReader();
        WritableImage resizedImage = new WritableImage((int) (currentZoomScale * adjustedCardDimensions.first), (int) (currentZoomScale * adjustedCardDimensions.second));
        PixelWriter writer = resizedImage.getPixelWriter();

        for(int y = 0; y < (int) (currentZoomScale * adjustedCardDimensions.second); y++)
            for(int x = 0; x < (int) (currentZoomScale * adjustedCardDimensions.first); x++)
                writer.setArgb(x, y, reader.getArgb((int) (x / (cardCompressionFactor * currentZoomScale)), (int) (y / (cardCompressionFactor * currentZoomScale))));

        if (isPlayable(slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex))) showCardsPlayability();

        content.putImage(resizedImage);
        dragboard.setContent(content);
        event.consume();
    }

    public void handleHandDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        double dropX = event.getX();
        double dropY = event.getY();

        Pair<Integer, Integer> cellIndexes = getCellIndexes(currentScrollPane, currentGridPane, dropX, dropY);
        if(cellIndexes == null) return;

        int handIndex = Integer.parseInt(dragboard.getString());
        if (handIndex < 0 || handIndex > 2) throw new RuntimeException("Illegal index: " + handIndex);

        int cardId = slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex);
        CardSide cardSide = visibleHandCardsSides.get(handIndex);

        placeCard(selfPlayerToken, cardId, cardSide, cellIndexes.first, cellIndexes.second);

        clearList = true;
    }

    public void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != currentGridPane) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    public void handleDragDone(DragEvent event) {
        hideCardsPlayability();
    }

    public void showCardsPlayability() {
        clearList = false;

        availableSlots.stream()
                .map(this::getIndexesFromCoords)
                .filter(Objects::nonNull)
                .forEach(cellIndexes -> {
                    Pane pane = new Pane();
                    pane.setPrefWidth(adjustedCardDimensions.first * currentZoomScale);
                    pane.setPrefHeight(adjustedCardDimensions.second * currentZoomScale);
                    pane.getStyleClass().add("highlightPlayable");

                    Platform.runLater(() -> {
                        if (getCell(currentGridPane, cellIndexes.x, cellIndexes.y).isPresent()) return;

                        currentGridPane.add(pane, cellIndexes.x, cellIndexes.y);
                    });
                });
    }

    public void hideCardsPlayability() {
        availableSlots.stream()
                .map(this::getIndexesFromCoords)
                .filter(Objects::nonNull)
                .forEach(cellIndexes -> {
                    getCell(currentGridPane, cellIndexes.x, cellIndexes.y).ifPresent(value -> currentGridPane.getChildren().remove(value));
                });

        if(clearList) availableSlots.clear();
    }

    private Pair<Integer, Integer> getCellIndexes(ScrollPane scrollPane, GridPane gridPane, double x, double y) {
        int col = (int) (x / (currentGridPane.getWidth() / gridCellsCount.first));
        int row = (int) (y / (currentGridPane.getHeight() / gridCellsCount.second));

        if (row < 0 || row > gridPane.getRowConstraints().size() || col < 0 || col > gridPane.getColumnConstraints().size())
            return null;

        return new Pair<>(row, col);
    }

    public Optional<Node> getCell(GridPane gridPane, int x, int y) {
        return gridPane.getChildren().stream()
                .filter(cell -> GridPane.getRowIndex(cell) == x && GridPane.getColumnIndex(cell) == y)
                .findFirst();
    }

    public Image getCardImage(int id, CardSide cardSide) {
        return new Image("images/cards/" + (cardSide == CardSide.FRONT ? "fronts/" : "backs/") + id + ".png");
    }

    /**
     * Allows to place a card into a player's board.
     *
     * @param playerToken token of the player
     * @param cardId id of the card to place
     * @param cardSide side of the card to place
     * @param x x coordinate of the grid
     * @param y y coordinate of the grid
     */
    public void placeCard(PlayerToken playerToken, int cardId, CardSide cardSide, int x, int y) {
        // get hand index
        int handIndex = slimGameModel.tokenToHand.get(playerToken).indexOf(cardId);
        if (handIndex == -1) return;

        // setup up the grid for the card placement
        ImageView cardImage = new ImageView(getCardImage(cardId, cardSide));
        cardImage.setEffect(new DropShadow());

        StackPane stackPane = new StackPane(cardImage);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);

        cardImage.setFitWidth(rawCardDimensions.first * cardCompressionFactor);
        cardImage.setFitHeight(rawCardDimensions.second * cardCompressionFactor);

        // place the card
        ((GridPane) tokenToScrollPane.get(playerToken).getContent()).add(stackPane, y, x);

        // put a placeholder in the hand
        ImageView cardImageView = ((ImageView) tokenToHandHBox.get(playerToken).getChildren().get(handIndex));
        cardImageView.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
        cardImageView.setDisable(true);

        // update model
        slimGameModel.tokenToHand.get(playerToken).set(handIndex, null);
        visibleHandCardsSides.set(handIndex, null);

        // update view
        enableDecks();
    }

    /**
     * Allows to place a card into the player's hand.
     *
     * @param cardId id of the card to add to the hand
     */
    public void addCardToHand(int cardId) {
        // get first free index
        int handIndex = slimGameModel.tokenToHand.get(selfPlayerToken).indexOf(null);
        if(handIndex == -1) throw new RuntimeException("Hand is full");

        // update model
        slimGameModel.tokenToHand.get(selfPlayerToken).set(handIndex, cardId);
        visibleHandCardsSides.set(handIndex, CardSide.FRONT);

        // update view
        ImageView cardImageView = (ImageView) tokenToHandHBox.get(selfPlayerToken).getChildren().get(handIndex);
        cardImageView.setImage(getCardImage(cardId, CardSide.FRONT));
        cardImageView.setDisable(false);
        if (!slimGameModel.tokenToHand.get(selfPlayerToken).contains(null)) disableDecks(); // disable decks if hand is full
    }

    /**
     * Handles the click on a deck.
     * Removes the card from the deck and adds the card to the hand, updating their corresponding ImageView
     * Requires correct turn, deck not empty and an available slot in the hand.
     *
     * @param mouseEvent click MouseEvent
     */
    public void handleDeckMouseClicked(MouseEvent mouseEvent) {
        ImageView deckImageViewClicked = (ImageView) mouseEvent.getTarget();
        Pair<ImageView, List<Integer>> deck = deckViewToDeck.get(deckImageViewClicked);

        if (deck == null) throw new RuntimeException("Deck clicked not found");
        if (deck.second.isEmpty()) throw new RuntimeException("Deck is empty: should be disabled");

        Integer drawnCardId = popCardFromDeck(deck);
        if (drawnCardId == null) throw new RuntimeException("No card in deck found");

        addCardToHand(drawnCardId);
    }

    /**
     * Handles the click on a visible card.
     * Puts the card in the player's hand, and takes a new card if the corresponding deck is not empty.
     * Requires correct turn and a non-null slot.
     *
     * @param mouseEvent the clicked MouseEvent
     */
    public void handleVisibleListMouseClicked(MouseEvent mouseEvent) {
        ImageView visibleSlotImageView = (ImageView) mouseEvent.getTarget();

        if (visibleSlotToCardId.get(visibleSlotImageView).get() == null) throw new RuntimeException("No card");
        if (!visibleSlotToDeck.containsKey(visibleSlotImageView)) throw new RuntimeException("Cannot find corresponding deck");

        Pair<ImageView, List<Integer>> deck = visibleSlotToDeck.get(visibleSlotImageView);

        addCardToHand(visibleSlotToCardId.get(visibleSlotImageView).get());

        if (deck.second.isEmpty()) {
            visibleSlotToVisibleListSetter.get(visibleSlotImageView).accept(null);
            visibleSlotImageView.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
            visibleSlotImageView.setDisable(true);

            return;
        }

        Integer nextCard = popCardFromDeck(deck);
        if (nextCard == null) throw new RuntimeException("Deck is empty: should be disabled");
        visibleSlotImageView.setImage(getCardImage(nextCard, CardSide.FRONT));
        visibleSlotToVisibleListSetter.get(visibleSlotImageView).accept(nextCard);
    }

    /**
     * Allows to pop a card from the deck structure.
     * Updates all corresponding objects.
     *
     * @param deck deck from which to draw
     * @return the card drawn
     */
    public Integer popCardFromDeck(Pair<ImageView, List<Integer>> deck) {
        if (deck.second.isEmpty()) return null;

        int drawnCardId = deck.second.removeLast();
        if (deck.second.isEmpty()) {
            deck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
            deck.first.setDisable(true);
        }
        else deck.first.setImage(getCardImage(deck.second.getLast(), CardSide.BACK));

        return drawnCardId;
    }

    /**
     * Disables decks when needed.
     */
    public void disableDecks() {
        resourceDeckImageView.setDisable(true);
        goldDeckImageView.setDisable(true);
        firstResourceImageView.setDisable(true);
        secondResourceImageView.setDisable(true);
        firstGoldImageView.setDisable(true);
        secondGoldImageView.setDisable(true);
    }

    /**
     * Re-enables decks when needed.
     * Only enables decks which are not permanently disabled (empty decks)
     */
    public void enableDecks() {
        if(!resourceDeck.second.isEmpty()) resourceDeckImageView.setDisable(false);
        if(!goldDeck.second.isEmpty()) goldDeckImageView.setDisable(false);

        if(visibleSlotToCardId.get(firstResourceImageView).get() != null) firstResourceImageView.setDisable(false);
        if(visibleSlotToCardId.get(secondResourceImageView).get() != null) secondResourceImageView.setDisable(false);
        if(visibleSlotToCardId.get(firstGoldImageView).get() != null) firstGoldImageView.setDisable(false);
        if(visibleSlotToCardId.get(secondGoldImageView).get() != null) secondGoldImageView.setDisable(false);
    }
}