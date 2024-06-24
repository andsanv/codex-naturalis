package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.Trio;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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


public class TempGameController {
    public Pair<Integer, Integer> screenResolution;
    public double screenRatio;

    public Pair<Double, Double> rawCellDimension;
    public Pair<Double, Double> rawCardDimension;
    public double targetCellWidth;
    public Pair<Double, Double> adjustedCellDimensions;
    public Pair<Double, Double> adjustedCardDimensions;
    public double cardCompressionFactor;    // target = 100 --> 0.1292, target = 200 -> 0.2584

    public Integer DEFAULT_OBJECTIVE_CARD_ID = 86;
    public Integer DEFAULT_STARTER_CARD_ID = 86; // TODO image of empty starter card back

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

    private Integer selectedCard;

    @FXML
    public AnchorPane mainAnchorPane;

    @FXML
    public StackPane mainStackPane;

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

    public PlayerToken currentPlayerToken;

    @FXML
    public ScrollPane currentScrollPane;

    @FXML
    public GridPane currentGridPane;

    @FXML
    public HBox currentHandHBox;

    public Text animalResourcesCounter;
    public Text plantResourcesCounter;
    public Text fungiResourcesCounter;
    public Text insectResourcesCounter;

    public Text manuscriptItemsCounter;
    public Text inkwellItemsCounter;
    public Text quillItemsCounter;

    public PlayerToken selfPlayerToken;

    public Trio<CardSide, CardSide, CardSide> visibleHandCardsSides;
    public List<Coords> cardsPlayability;

    public Timer cardClickTimer;
    public boolean cardClickTimerExpired;
    public long cardClickDelay = 100;

    public Map<PlayerToken, ScrollPane> tokenToScrollPane = new HashMap<>();
    public Map<PlayerToken, HBox> tokenToHandHBox = new HashMap<>();

    private Pair<Integer, Integer> gridCellsCount;

    public void initialize(GUI gui) throws InterruptedException {
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
                new ArrayList<>(Arrays.asList(new Pair<>(false, 17), new Pair<>(false, 5), new Pair<>(false, null), new Pair<>(false, null))),
                new Pair<>(30, 31),
                new Pair<>(8, 18),
                new HashMap<>() {{
                    put(PlayerToken.RED, 0);
                    put(PlayerToken.BLUE, 0);
                    put(PlayerToken.YELLOW, 0);
                }}
        );

        rawCellDimension = new Pair<>(774.0, 397.0);
        rawCardDimension = new Pair<>(993.0, 662.0);

        targetCellWidth = 100;
        cardCompressionFactor = targetCellWidth / rawCellDimension.first;

        adjustedCellDimensions = new Pair<>(rawCellDimension.first * cardCompressionFactor, rawCellDimension.second * cardCompressionFactor);
        adjustedCardDimensions = new Pair<>(rawCardDimension.first * cardCompressionFactor, rawCardDimension.second * cardCompressionFactor);

        gridCellsCount = new Pair<>(81, 81);

        zoomScale = 1;
        zoomIncrement = 0.1;

        commonObjectives = Arrays.asList(DEFAULT_OBJECTIVE_CARD_ID, DEFAULT_OBJECTIVE_CARD_ID);

        firstObjectiveSlot.setImage(new Image("images/cards/backs/" + commonObjectives.getFirst() + ".png"));
        secondObjectiveSlot.setImage(new Image("images/cards/backs/" + commonObjectives.get(1) + ".png"));

        secretObjectiveSlot.setImage(new Image("images/cards/backs/" + DEFAULT_OBJECTIVE_CARD_ID + ".png"));

        initializeGridPane(currentGridPane);
        initializeScrollPane(currentScrollPane);
        initializePlayersList(players);
        initializeDecks();

        String path = "images/miscellaneous/default_starter_card.png";

        ImageView defaultStarterCardView = new ImageView(new Image(path));
        defaultStarterCardView.setEffect(new DropShadow());

        StackPane stackPane = new StackPane(defaultStarterCardView);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);

        defaultStarterCardView.setFitWidth(rawCardDimension.first * cardCompressionFactor);
        defaultStarterCardView.setFitHeight(rawCardDimension.second * cardCompressionFactor);

        currentGridPane.add(stackPane, 40, 40);


        initializeAllPlayers();

        selfPlayerToken = PlayerToken.RED;
        currentPlayerToken = selfPlayerToken;

        handlePlayedCardEvent(PlayerToken.RED, 45, CardSide.FRONT, new Coords(1,1));
        handlePlayedCardEvent(PlayerToken.BLUE, 45, CardSide.FRONT, new Coords(-1,1));

        visibleHandCardsSides = new Trio<>(CardSide.BACK, CardSide.FRONT, CardSide.BACK);
        cardsPlayability = new ArrayList<>(Arrays.asList(new Coords(1,1), new Coords(-1,-1)));

        switchPlayerView(PlayerToken.RED);
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
                slimGameModel.decks.get(0).first ? DEFAULT_OBJECTIVE_CARD_ID : slimGameModel.decks.get(0).second, CardSide.BACK
        ));
        resourceDeckImageView.setOnMouseClicked(this::handleDeckMouseClicked);

        // gold deck
        goldDeckImageView.setImage(getCardImage(
                slimGameModel.decks.get(1).first ? DEFAULT_OBJECTIVE_CARD_ID : slimGameModel.decks.get(1).second, CardSide.BACK
        ));
        goldDeckImageView.setOnMouseClicked(this::handleDeckMouseClicked);

        // resource visible
        firstResourceImageView.setImage(getCardImage(slimGameModel.visibleResourceCardsList.first, CardSide.FRONT));
        secondResourceImageView.setImage(getCardImage(slimGameModel.visibleResourceCardsList.second, CardSide.FRONT));

        // gold visible
        firstGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.first, CardSide.FRONT));
        secondGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.second, CardSide.FRONT));
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

    public void firstGuiUpdate() {
        // update hand
        List<Integer> cards = new ArrayList<>(Arrays.asList(
                slimGameModel.tokenToHand.get(currentPlayerToken).first,
                slimGameModel.tokenToHand.get(currentPlayerToken).second,
                slimGameModel.tokenToHand.get(currentPlayerToken).third
        ));

        for(int i = 0; i < currentHandHBox.getChildren().size(); i++) {
            ((ImageView) (currentHandHBox.getChildren().get(i))).setImage(
                    new Image("images/cards/fronts/" + cards.get(i) + ".png")
            );
        }

        // update grid

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
            gridPane.setGridLinesVisible(true);

            scrollPane.setContent(gridPane);

            tokenToScrollPane.put(token, scrollPane);

            placeCard(token, slimGameModel.tokenToPlayedCards.get(token).get(0).first, slimGameModel.tokenToPlayedCards.get(token).get(0).second, slimGameModel.tokenToPlayedCards.get(token).get(0).third.x, slimGameModel.tokenToPlayedCards.get(token).get(0).third.y);

            // set up hand hbox
            HBox hbox = new HBox();

            AnchorPane.setLeftAnchor(hbox, 407.5);
            AnchorPane.setRightAnchor(hbox, 407.5);
            AnchorPane.setBottomAnchor(hbox, 20.0);

            Integer firstCardId = slimGameModel.tokenToHand.get(token).first;
            Integer secondCardId = slimGameModel.tokenToHand.get(token).second;
            Integer thirdCardId = slimGameModel.tokenToHand.get(token).third;

            ImageView firstCardImageView = new ImageView(new Image("images/cards/backs/" + firstCardId + ".png"));
            firstCardImageView.setOnDragDetected(this::handleHandDragDetected);
            firstCardImageView.setOnMousePressed(this::handleHandMousePressed);
            firstCardImageView.setOnMouseReleased(this::handleHandMouseReleased);
            firstCardImageView.setPreserveRatio(true);
            firstCardImageView.setFitWidth(195);
            firstCardImageView.setFitHeight(130);

            ImageView secondCardImageView = new ImageView(new Image("images/cards/backs/" + secondCardId + ".png"));
            secondCardImageView.setOnDragDetected(this::handleHandDragDetected);
            secondCardImageView.setOnMousePressed(this::handleHandMousePressed);
            secondCardImageView.setOnMouseReleased(this::handleHandMouseReleased);
            secondCardImageView.setPreserveRatio(true);
            secondCardImageView.setFitWidth(195);
            secondCardImageView.setFitHeight(130);

            ImageView thirdCardImageView = new ImageView(new Image("images/cards/backs/" + thirdCardId + ".png"));
            thirdCardImageView.setOnDragDetected(this::handleHandDragDetected);
            thirdCardImageView.setOnMousePressed(this::handleHandMousePressed);
            thirdCardImageView.setOnMouseReleased(this::handleHandMouseReleased);
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
        if(zoomScale + zoomIncrement < 0 || zoomScale + zoomIncrement > 2) return;

        zoomScale += zoomIncrement;

        currentGridPane.setScaleX(zoomScale);
        currentGridPane.setScaleY(zoomScale);
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

    public void handleHandMouseReleased(MouseEvent event) {
        if(cardClickTimerExpired) {
            switchCardsPlayabilityVisibility();
            return;
        }

        cardClickTimer.cancel();

        ImageView targetCard = (ImageView) event.getTarget();
        int index = currentHandHBox.getChildren().indexOf(targetCard);

        int cardId;
        CardSide oppositeCardSide;
        switch (index) {
            case 0 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).first;
                oppositeCardSide = (visibleHandCardsSides.first == CardSide.FRONT ? CardSide.BACK : CardSide.FRONT);
                visibleHandCardsSides = new Trio<>(oppositeCardSide, visibleHandCardsSides.second, visibleHandCardsSides.third);
            }
            case 1 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).second;
                oppositeCardSide = (visibleHandCardsSides.second == CardSide.FRONT ? CardSide.BACK : CardSide.FRONT);
                visibleHandCardsSides = new Trio<>(visibleHandCardsSides.first, oppositeCardSide, visibleHandCardsSides.third);
            }
            case 2 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).third;
                oppositeCardSide = (visibleHandCardsSides.third == CardSide.FRONT ? CardSide.BACK : CardSide.FRONT);
                visibleHandCardsSides = new Trio<>(visibleHandCardsSides.first, visibleHandCardsSides.second, oppositeCardSide);
            }
            default -> throw new RuntimeException("Illegal index: " + index);
        }

        ((ImageView) currentHandHBox.getChildren().get(index)).setImage(new Image("images/cards/" + (oppositeCardSide == CardSide.FRONT ? "fronts" : "backs")  + "/" + cardId + ".png"));
    }

    public void handleHandMousePressed(MouseEvent event) {
        TimerTask showCardsPlayabilityTask = new TimerTask() {
            @Override
            public void run() {
                cardClickTimerExpired = true;
                switchCardsPlayabilityVisibility();
            }
        };

        cardClickTimerExpired = false;
        cardClickTimer = new Timer();
        cardClickTimer.schedule(showCardsPlayabilityTask, cardClickDelay);
    }

    public void handleHandDragDetected(MouseEvent event) {
        ImageView targetCard = (ImageView) event.getTarget();
        int index = currentHandHBox.getChildren().indexOf(targetCard);

        Dragboard dragboard = targetCard.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();

        CardSide cardSide;
        Image draggedCardImage;

        switch (index) {
            case 0 -> {
                cardSide = visibleHandCardsSides.first;
                draggedCardImage = getCardImage(slimGameModel.tokenToHand.get(currentPlayerToken).first, cardSide);
                content.putString("0");
            }
            case 1 -> {
                cardSide = visibleHandCardsSides.second;
                draggedCardImage = getCardImage(slimGameModel.tokenToHand.get(currentPlayerToken).second, cardSide);
                content.putString("1");
            }
            case 2 -> {
                cardSide = visibleHandCardsSides.third;
                draggedCardImage = getCardImage(slimGameModel.tokenToHand.get(currentPlayerToken).third, cardSide);
                content.putString("2");
            }
            default -> throw new RuntimeException("Unexpected value: " + index);
        }

        PixelReader reader = draggedCardImage.getPixelReader();
        WritableImage resizedImage = new WritableImage((int) (double) (zoomScale * adjustedCardDimensions.first), (int) (double) (zoomScale * adjustedCardDimensions.second));
        PixelWriter writer = resizedImage.getPixelWriter();

        for(int y = 0; y < (int) (double) (zoomScale * adjustedCardDimensions.second); y++)
            for(int x = 0; x < (int) (double) (zoomScale * adjustedCardDimensions.first); x++)
                writer.setArgb(x, y, reader.getArgb((int) (x / (cardCompressionFactor * zoomScale)), (int) (y / (cardCompressionFactor * zoomScale))));

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

        switchCardsPlayabilityVisibility();

        int cardId;
        CardSide cardSide;
        int handIndex = Integer.parseInt(dragboard.getString());
        switch (handIndex) {
            case 0 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).first;
                cardSide = visibleHandCardsSides.first;
            }
            case 1 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).second;
                cardSide = visibleHandCardsSides.second;
            }
            case 2 -> {
                cardId = slimGameModel.tokenToHand.get(currentPlayerToken).third;
                cardSide = visibleHandCardsSides.third;
            }
            default -> throw new RuntimeException("Unexpected value: " + dragboard.getString());
        }

        placeCard(currentPlayerToken, cardId, cardSide, cellIndexes.first, cellIndexes.second);
    }

    public void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != currentGridPane) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private Pair<Integer, Integer> getCellIndexes(ScrollPane scrollPane, GridPane gridPane, double x, double y) {
        int col = (int) (x / (currentGridPane.getWidth() / gridCellsCount.first));
        int row = (int) (y / (currentGridPane.getHeight() / gridCellsCount.second));

        if (row < 0 || row > gridPane.getRowConstraints().size() || col < 0 || col > gridPane.getColumnConstraints().size())
            return null;

        return new Pair<>(row, col);
    }

    public Image getCardImage(int id, CardSide cardSide) {
        return new Image("images/cards/" + (cardSide == CardSide.FRONT ? "fronts/" : "backs/") + id + ".png");
    }

    public void switchCardsPlayabilityVisibility() {
        System.out.println("switched");
    }

    public void placeCard(PlayerToken playerToken, int cardId, CardSide cardSide, int x, int y) {
        int handIndex = slimGameModel.tokenToHand.get(playerToken).getIndexOf(cardId);

        ImageView cardImage = new ImageView(getCardImage(cardId, cardSide));
        cardImage.setEffect(new DropShadow());

        StackPane stackPane = new StackPane(cardImage);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);

        cardImage.setFitWidth(rawCardDimension.first * cardCompressionFactor);
        cardImage.setFitHeight(rawCardDimension.second * cardCompressionFactor);

        ((GridPane) tokenToScrollPane.get(playerToken).getContent()).add(stackPane, y, x);
        if(handIndex != -1) {
            ImageView cardImageView = ((ImageView) tokenToHandHBox.get(playerToken).getChildren().get(handIndex));
            cardImageView.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
            cardImageView.setDisable(true);

            switch(handIndex) {
                case 0 ->  {
                    slimGameModel.tokenToHand.put(playerToken, new Trio<>(null, slimGameModel.tokenToHand.get(playerToken).second, slimGameModel.tokenToHand.get(playerToken).third));
                }
                case 1 -> {
                    slimGameModel.tokenToHand.put(playerToken, new Trio<>(slimGameModel.tokenToHand.get(playerToken).first, null, slimGameModel.tokenToHand.get(playerToken).third));
                }
                case 2 -> {
                    slimGameModel.tokenToHand.put(playerToken, new Trio<>(slimGameModel.tokenToHand.get(playerToken).first, slimGameModel.tokenToHand.get(playerToken).second, null));
                }
            }
        }
    }

    public void bringCardToHand(int cardId) {
        int handIndex = slimGameModel.tokenToHand.get(selfPlayerToken).getNullIndex();

        if(handIndex == -1) throw new RuntimeException("Hand is full");

        switch (handIndex) {
            case 0 -> {
                slimGameModel.tokenToHand.put(selfPlayerToken, new Trio<>(cardId, slimGameModel.tokenToHand.get(selfPlayerToken).second, slimGameModel.tokenToHand.get(selfPlayerToken).third));
                visibleHandCardsSides = new Trio<>(CardSide.BACK, visibleHandCardsSides.second, visibleHandCardsSides.third);
            }
            case 1 -> {
                slimGameModel.tokenToHand.put(selfPlayerToken, new Trio<>(slimGameModel.tokenToHand.get(selfPlayerToken).first, cardId, slimGameModel.tokenToHand.get(selfPlayerToken).third));
                visibleHandCardsSides = new Trio<>(visibleHandCardsSides.first, CardSide.BACK, visibleHandCardsSides.third);
            }
            case 2 -> {
                slimGameModel.tokenToHand.put(selfPlayerToken, new Trio<>(slimGameModel.tokenToHand.get(selfPlayerToken).first, slimGameModel.tokenToHand.get(selfPlayerToken).second, cardId));
                visibleHandCardsSides = new Trio<>(visibleHandCardsSides.first, visibleHandCardsSides.second, CardSide.BACK);
            }
        }

        ImageView cardImageView = (ImageView) tokenToHandHBox.get(selfPlayerToken).getChildren().get(handIndex);
        cardImageView.setImage(getCardImage(cardId, CardSide.BACK));
        cardImageView.setDisable(false);
    }

    public void handleDeckMouseClicked(MouseEvent mouseEvent) {
        Pair<ImageView, Integer> deckClicked;

        if (Objects.equals(mouseEvent.getTarget(), resourceDeckImageView)) deckClicked = new Pair<>(resourceDeckImageView, 0);
        else if (Objects.equals(mouseEvent.getTarget(), goldDeckImageView)) deckClicked = new Pair<>(goldDeckImageView, 1);
        else throw new RuntimeException("Unsupported deck");

        if (slimGameModel.decks.get(deckClicked.second).first) throw new RuntimeException("Deck is empty");

        deckClicked.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
        bringCardToHand(slimGameModel.decks.get(deckClicked.second).second);
    }
}