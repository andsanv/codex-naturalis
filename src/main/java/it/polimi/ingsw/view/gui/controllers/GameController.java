package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.*;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.Trio;
import it.polimi.ingsw.view.gui.GUI;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;
import javafx.event.Event;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Controller for the GameScene.
 * It allows users to play the game.
 */
public class GameController extends Controller {
    /* CONTROLLER RELATED OBJECTS */
    private GUI gui = null;
    private AtomicReference<ConnectionHandler> connectionHandler = null;
    private AtomicReference<UserInfo> selfUserInfo = null;
    private AtomicReference<LobbyInfo> lobby = null;


    /* DIMENSIONS AND CONSTANTS */
    // screen
    public final Pair<Integer, Integer> screenResolution = new Pair<>(1440, 900);
    public final double screenRatio = 0.01 * ((double) (100 * screenResolution.first) / screenResolution.second);;

    // zoom
    private double currentZoomScale = 1;
    private final double zoomIncrement = 0.1;

    // cells and cards
    public final Pair<Double, Double> rawCellDimensions = new Pair<>(774.0, 397.0);
    public final Pair<Double, Double> rawCardDimensions = new Pair<>(993.0, 662.0);
    public final double targetCellWidth = 100;
    public final double cardCompressionFactor = targetCellWidth / rawCellDimensions.first;    // target = 100 --> 0.1292, target = 200 -> 0.2584
    public final Pair<Double, Double> adjustedCellDimensions = new Pair<>(rawCellDimensions.first * cardCompressionFactor, rawCellDimensions.second * cardCompressionFactor);
    public final Pair<Double, Double> adjustedCardDimensions = new Pair<>(rawCardDimensions.first * cardCompressionFactor, rawCardDimensions.second * cardCompressionFactor);
    public final Pair<Integer, Integer> gridCellsCount = new Pair<>(81, 81);

    public final Integer DEFAULT_OBJECTIVE_CARD_ID = 88;


    /* GRAPHIC STRUCTURE */
    // fixed
    @FXML public AnchorPane mainAnchorPane;
    @FXML public StackPane mainStackPane;
    @FXML public TabPane leftTabPane;
    @FXML public Tab decksTab;
    @FXML public AnchorPane decksAnchorPane;
    @FXML public TabPane rightTabPane;
    @FXML public Button leftPanelButton;
    @FXML public Button rightPanelButton;
    @FXML public ScrollPane defaultScrollPane;
    @FXML public GridPane defaultGridPane;
    @FXML public HBox defaultHandHBox;

    // dynamic
    public ScrollPane currentScrollPane;
    public GridPane currentGridPane;
    public HBox currentHandHBox;

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
    public Map<UserInfo, PlayerToken> userInfoToToken;
    public SlimGameModel slimGameModel;


    /* GAME FLOW */
    public PlayerToken selfPlayerToken;
    public PlayerToken currentPlayerToken;
    public PlayerToken currentTurn = null;
    public boolean isPlayCardState = false;
    public Map<PlayerToken, ScrollPane> tokenToScrollPane = new HashMap<>();
    public Map<PlayerToken, HBox> tokenToHandHBox = new HashMap<>();
    @FXML public StackPane eventPane;
    @FXML public Text eventText;
    @FXML public Text eventTitle;
    @FXML public StackPane importantEventPane;
    @FXML public Text importantEventText;
    @FXML public StackPane scoretrackStackPane;
    @FXML public ImageView redTokenImageView;
    @FXML public ImageView greenTokenImageView;
    @FXML public ImageView blueTokenImageView;
    @FXML public ImageView yellowTokenImageView;

    /* ENDED PHASE */
    @FXML public StackPane endedGamePane;
    @FXML public StackPane gameEndedTextPane;
    @FXML public StackPane firstPlayerEndedPane;
    @FXML public StackPane secondPlayerEndedPane;
    @FXML public StackPane thirdPlayerEndedPane;
    @FXML public StackPane fourthPlayerEndedPane;
    @FXML public Text gameEndedText;

    @FXML public Text firstPlayerEndedText;
    @FXML public Text secondPlayerEndedText;
    @FXML public Text thirdPlayerEndedText;
    @FXML public Text fourthPlayerEndedText;

    @FXML public Text firstPlayerPoints;
    @FXML public Text secondPlayerPoints;
    @FXML public Text thirdPlayerPoints;
    @FXML public Text fourthPlayerPoints;

    @FXML public Rectangle endedGameRectangle;
    @FXML public Button quitButton;
    @FXML public VBox endedPlayersVBox;

    @FXML public StackPane endedScoretrackStackPane;
    public Pair<Integer, Integer> endedScoretrackDimensions = new Pair<>(231, 480);
    public double endedScoretrackXRatio = 231.0 / 378.0;
    public double endedScoretrackYRatio = 480.0 / 756.0;

    /* CHAT */
    @FXML private AnchorPane chatAnchorPane;
    @FXML private Button sendMessageButton;
    @FXML private VBox globalMessagesVBox;
    @FXML private TextField messageTextField;

    @FXML private ScrollPane currentChatScrollPane;
    private ScrollPane globalChatScrollPane;
    @FXML Button globalChatButton;
    private Map<UserInfo, ScrollPane> userToChatScrollPane = new HashMap<>();

    @FXML private VBox chatPlayersVBox;

    /* HELPERS */
    // mouse drag
    private double dragStartX;
    private double dragStartY;

    // cards playability
    private boolean clearList = false;
    public Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability = new HashMap<>();
    public List<Coords> availableSlots = new ArrayList<>();

    // draw card
    boolean drawnCard = false;

    // miscellaneous structures
    public Pair<ImageView, List<Integer>> resourceDeck;
    public Pair<ImageView, List<Integer>> goldDeck;
    public Pair<Pair<ImageView, AtomicInteger>, Pair<ImageView, AtomicInteger>> resourceVisibleList;
    public Pair<Pair<ImageView, AtomicInteger>, Pair<ImageView, AtomicInteger>> goldVisibleList;

    Map<ImageView, Pair<ImageView, List<Integer>>> deckViewToDeck;
    Map<ImageView, Supplier<Integer>> visibleSlotToCardId;
    Map<ImageView, Pair<ImageView, List<Integer>>> visibleSlotToDeck;
    Map<ImageView, Consumer<Integer>> visibleSlotToVisibleListSetter;

    public List<StackPane> playersPanes = new ArrayList<>();

    public List<CardSide> visibleHandCardsSides = new ArrayList<>(Arrays.asList(CardSide.BACK, CardSide.BACK, CardSide.BACK));

    public Pair<Double, Double> scoreTrackDimensions = new Pair<>(270.0, 560.0);
    public double scoretrackYRatio = 560.0 / 756.0;
    public double scoretrackXRatio = 270.0 / 378.0;
    Map<Integer, Coords> scoretrackPositions = new HashMap<>() {{
        put(0, new Coords((int) (99.987), (int) (703.545)));
        put(1, new Coords((int) (188.976), (int) (703.545)));
        put(2, new Coords((int) (277.967), (int) (703.545)));
        put(3, new Coords((int) (322.461), (int) (622.147)));
        put(4, new Coords((int) (233.471), (int) (622.147)));
        put(5, new Coords((int) (144.481), (int) (622.147)));
        put(6, new Coords((int) (55.492), (int) (622.147)));
        put(7, new Coords((int) (55.492), (int) (540.748)));
        put(8, new Coords((int) (144.481), (int) (540.748)));
        put(9, new Coords((int) (233.471), (int) (540.749)));
        put(10, new Coords((int) (322.461), (int) (540.748)));
        put(11, new Coords((int) (322.462), (int) (459.351)));
        put(12, new Coords((int) (233.472), (int) (459.351)));
        put(13, new Coords((int) (144.481), (int) (459.351)));
        put(14, new Coords((int) (55.492), (int) (459.351)));
        put(15, new Coords((int) (55.492), (int) (377.953)));
        put(16, new Coords((int) (144.481), (int) (377.953)));
        put(17, new Coords((int) (233.472), (int) (377.953)));
        put(18, new Coords((int) (322.461), (int) (377.953)));
        put(19, new Coords((int) (322.143), (int) (296.555)));

        put(20, new Coords((int) (188.717), (int) (256.552)));
        put(21, new Coords((int) (55.810), (int) (296.554)));
        put(22, new Coords((int) (55.670), (int) (215.156)));
        put(23, new Coords((int) (55.670), (int) (133.759)));
        put(24, new Coords((int) (107.122), (int) (67.044)));
        put(25, new Coords((int) (188.976), (int) (52.361)));
        put(26, new Coords((int) (270.830), (int) (67.044)));
        put(27, new Coords((int) (322.283), (int) (133.759)));
        put(28, new Coords((int) (322.283), (int) (215.156)));
        put(29, new Coords((int) (188.976), (int) (151.093)));
    }};

    /**
     * Method to call to initialize the controller and the scene.
     * Sets up all needed structures, panes, and objects.
     *
     * @param gui the main application
     */
    public void initialize(GUI gui, SlimGameModel slimGameModel, Map<UserInfo, PlayerToken> userInfoToToken) {
        this.gui = gui;
        this.connectionHandler = gui.connectionHandler;
        this.selfUserInfo = gui.selfUserInfo;
        this.lobby = gui.currentLobby;

        this.userInfoToToken = userInfoToToken;

        this.slimGameModel = slimGameModel;

        this.selfPlayerToken = userInfoToToken.get(selfUserInfo.get());

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

        initializeTabs();
        initializeDecks();
        initializeEventPanes();
        initializePlayersList(lobby.get().users);
        initializeChat();

        lobby.get().users.forEach(this::initializePlayer);    // sets up structures for each player, such as scroll and grid pane

        currentPlayerToken = selfPlayerToken;
        switchPlayerView(selfPlayerToken);
    }

    /**
     * Sets up the event panes for in-game notifications.
     */
    public void initializeEventPanes() {
        eventPane.setMouseTransparent(true);
        eventPane.setVisible(false);

        eventText.setMouseTransparent(true);
        eventText.setVisible(false);

        importantEventPane.getStyleClass().add("important-event-pane");
        importantEventPane.setMouseTransparent(true);
        importantEventPane.setVisible(false);

        importantEventText.getStyleClass().add("important-text");
        importantEventText.setMouseTransparent(true);
        importantEventText.setVisible(false);
    }

    /**
     * Allows to enable side panes after initialization phase is finished.
     */
    public void initializeTabs() {
        // enable side tab panes
        leftTabPane.setDisable(false);
        leftPanelButton.setDisable(false);

        rightTabPane.setDisable(false);
        rightPanelButton.setDisable(false);

        Arrays.asList(PlayerToken.RED, PlayerToken.BLUE, PlayerToken.GREEN, PlayerToken.YELLOW).forEach(token -> {
                handleScoreTrackEvent(token, 0);
        });
    }

    /**
     * Sets first imageViews of cards on deck, based on the SlimGameModel.
     */
    public void initializeDecks() {
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
        firstResourceImageView.setDisable(true);

        secondResourceImageView.setImage(getCardImage(slimGameModel.visibleResourceCardsList.get(1), CardSide.FRONT));
        secondResourceImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
        secondResourceImageView.setDisable(true);

        // gold visible
        firstGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.get(0), CardSide.FRONT));
        firstGoldImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
        firstGoldImageView.setDisable(true);

        secondGoldImageView.setImage(getCardImage(slimGameModel.visibleGoldCardsList.get(1), CardSide.FRONT));
        secondGoldImageView.setOnMouseClicked(this::handleVisibleListMouseClicked);
        secondGoldImageView.setDisable(true);

        firstObjectiveSlot.setImage(getCardImage(slimGameModel.commonObjectives.get(0), CardSide.FRONT));
        secondObjectiveSlot.setImage(getCardImage(slimGameModel.commonObjectives.get(1), CardSide.FRONT));
        secretObjectiveSlot.setImage(getCardImage(slimGameModel.tokenToSecretObjective.get(selfPlayerToken), CardSide.FRONT));
    }

    /**
     * Allows to initialize chat pane
     */
    public void initializeChat() {
        globalMessagesVBox.setAlignment(Pos.TOP_CENTER);
        sendMessageButton.setOnAction(this::sendMessage);
        sendMessageButton.getStyleClass().add("send-message-button");

        globalChatScrollPane = currentChatScrollPane;
        globalChatButton.setOnAction(actionEvent -> {
            currentChatScrollPane = globalChatScrollPane;
            chatAnchorPane.getChildren().set(1, currentChatScrollPane);
        });

        lobby.get().users.stream().filter(player -> !player.equals(selfUserInfo.get())).forEach(player -> {
            ScrollPane scrollPane = new ScrollPane();
            AnchorPane.setTopAnchor(scrollPane, 10.0);
            AnchorPane.setLeftAnchor(scrollPane, 10.0);
            AnchorPane.setBottomAnchor(scrollPane, 235.0);
            AnchorPane.setLeftAnchor(scrollPane, 10.0);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox vbox = new VBox();
            vbox.setPrefWidth(254);
            vbox.setPrefHeight(530);
            vbox.setSpacing(5);
            Pane pane = new Pane();
            pane.setPrefHeight(5);
            vbox.getChildren().add(pane);
            scrollPane.setContent(vbox);

            userToChatScrollPane.put(player, scrollPane);

            StackPane stackPane = new StackPane();
            stackPane.setPrefHeight(185);
            Button button = new Button();
            button.setText(player.name);
            button.setOnAction(actionEvent -> {
                currentChatScrollPane = userToChatScrollPane.get(player);
                chatAnchorPane.getChildren().set(1, currentChatScrollPane);
            });
            button.setMaxHeight(Double.MAX_VALUE);
            button.setMaxWidth(Double.MAX_VALUE);
            stackPane.getChildren().add(button);

            chatPlayersVBox.getChildren().add(stackPane);
        });
    }

    /**
     * Dynamically builds the player list in the bottom right pane.
     *
     * @param players list of players playing
     */
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
            // setup entry
            StackPane mainPlayerPane = new StackPane();
            mainPlayerPane.setPrefWidth(playerImageViewDimensions.first);
            mainPlayerPane.setPrefHeight(playerImageViewDimensions.second);
            mainPlayerPane.setOnMouseClicked(this::handlePlayerPaneMouseClick);
            mainPlayerPane.getStyleClass().add("player-pane");

            HBox playerHBox = new HBox();
            playerHBox.setMouseTransparent(true);

            // setup avatar
            StackPane playerAvatarPane = new StackPane();
            playerAvatarPane.setMouseTransparent(true);
            HBox.setMargin(playerAvatarPane, new Insets(5, 0, 5, 15));

            ImageView playerAvatar = new ImageView(playersImages.get(i));
            playerAvatar.setMouseTransparent(true);
            playerAvatar.setPreserveRatio(true);
            playerAvatar.setFitHeight(playerImageViewDimensions.second - 10);
            playerAvatar.setFitWidth(playerImageViewDimensions.second - 10);

            playerAvatarPane.getChildren().add(playerAvatar);

            // setup name
            StackPane playerNamePane = new StackPane();
            playerNamePane.setMouseTransparent(true);
            HBox.setMargin(playerNamePane, new Insets(5, 0, 5, 5));
            playerNamePane.setPrefWidth(135);

            Label playerName = new Label(players.get(i).name);
            playerName.setMouseTransparent(true);
            StackPane.setMargin(playerName, new Insets(0, 0, 5, 0));
            playerName.getStyleClass().add("player-name");
            playerName.setStyle("-fx-font-size: 18");

            playerNamePane.getChildren().add(playerName);

            // setup view icon
            StackPane viewIconPane = new StackPane();
            viewIconPane.setMouseTransparent(true);
            viewIconPane.setPrefHeight(playerImageViewDimensions.second - 10);
            viewIconPane.setPrefWidth(40);
            HBox.setMargin(viewIconPane, new Insets(5, 0, 5, 0));

            ImageView viewIcon = new ImageView(new Image("images/icons/view_icon.png"));
            viewIcon.setMouseTransparent(true);
            viewIcon.setPreserveRatio(true);
            viewIcon.setFitHeight(20);
            viewIcon.setFitWidth(20);

            viewIconPane.getChildren().add(viewIcon);

            // end setup
            playerHBox.getChildren().add(playerAvatarPane);
            playerHBox.getChildren().add(playerNamePane);
            playerHBox.getChildren().add(viewIconPane);
            mainPlayerPane.getChildren().add(playerHBox);

            playersList.getChildren().add(mainPlayerPane);
            playersPanes.add(mainPlayerPane);

            if(!first) VBox.setMargin(mainPlayerPane, new Insets(5.0, 0.0, 0.0, 0.0));
            first = false;
        }
    }

    /**
     * Allows to initialize a single player's structures and panes.
     *
     * @param player player being initialized
     */
    public void initializePlayer(UserInfo player) {
        // get player token
        PlayerToken playerToken = userInfoToToken.get(player);

        // set up scroll pane and grid pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);

        scrollPane.setOnKeyPressed(this::handleKeyPressedEvent);

        GridPane gridPane = new GridPane();
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

        gridPane.setGridLinesVisible(false);

        gridPane.setOnMouseDragged(this::handleMouseDragged);
        gridPane.setOnMousePressed(this::handleOnMousePressed);

        scrollPane.setContent(gridPane);
        tokenToScrollPane.put(playerToken, scrollPane);

        // place starter card
        setCard(
                playerToken,
                slimGameModel.tokenToPlayedCards.get(playerToken).get(0).first,
                slimGameModel.tokenToPlayedCards.get(playerToken).get(0).second,
                new Coords(0,0)
        );

        // set up hand hbox
        HBox handHBox = new HBox();

        AnchorPane.setLeftAnchor(handHBox, 407.5);
        AnchorPane.setRightAnchor(handHBox, 407.5);
        AnchorPane.setBottomAnchor(handHBox, 20.0);

        ImageView firstCardImageView = new ImageView(getCardImage(slimGameModel.tokenToHand.get(playerToken).get(0), CardSide.BACK));
        firstCardImageView.setPreserveRatio(true);
        firstCardImageView.setFitWidth(195);
        firstCardImageView.setFitHeight(130);

        ImageView secondCardImageView = new ImageView(getCardImage(slimGameModel.tokenToHand.get(playerToken).get(1), CardSide.BACK));
        secondCardImageView.setPreserveRatio(true);
        secondCardImageView.setFitWidth(195);
        secondCardImageView.setFitHeight(130);

        ImageView thirdCardImageView = new ImageView(getCardImage(slimGameModel.tokenToHand.get(playerToken).get(2), CardSide.BACK));
        thirdCardImageView.setPreserveRatio(true);
        thirdCardImageView.setFitWidth(195);
        thirdCardImageView.setFitHeight(130);

        handHBox.getChildren().addAll(firstCardImageView, secondCardImageView, thirdCardImageView);
        HBox.setMargin(firstCardImageView, new Insets(10.0, 0.0, 10.0, 10.0));
        HBox.setMargin(secondCardImageView, new Insets(10.0, 0.0, 10.0, 10.0));
        HBox.setMargin(thirdCardImageView, new Insets(10.0, 10.0, 10.0, 10.0));

        tokenToHandHBox.put(playerToken, handHBox);

        // enable structures if the player being initialized is self
        if (playerToken == selfPlayerToken) {
            firstCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            firstCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            firstCardImageView.setOnMouseExited(this::handleHandMouseExited);

            secondCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            secondCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            secondCardImageView.setOnMouseExited(this::handleHandMouseExited);

            thirdCardImageView.setOnMouseClicked(this::handleHandMouseClicked);
            thirdCardImageView.setOnMouseEntered(this::handleHandMouseEntered);
            thirdCardImageView.setOnMouseExited(this::handleHandMouseExited);
        }
    }

    public Coords getIndexesFromCoords(Coords coords) {
        int x = coords.x + (gridCellsCount.first - 1) / 2;
        int y = - coords.y + (gridCellsCount.first - 1) / 2;

        if(x < 0 || x > gridCellsCount.first || y < 0 || y > gridCellsCount.second) return null;

        return new Coords(x, y);
    }

    public Coords getCoordsFromIndexes(Coords indexes) {
        int x = indexes.x - (gridCellsCount.first - 1) / 2;
        int y = (gridCellsCount.first - 1) / 2 - indexes.y;

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

        tokenToScrollPane.values().forEach(scrollPane -> {
            scrollPane.getContent().setScaleX(currentZoomScale);
            scrollPane.getContent().setScaleY(currentZoomScale);
        });
    }

    /**
     * Allows to see other player's board, by switching the second to last element of the mainStackPane.
     *
     * @param playerToken token of the player whose board the user is interested to see
     */
    public void switchPlayerView(PlayerToken playerToken) {
        // switch grid pane
        if(!tokenToScrollPane.containsKey(playerToken)) throw new RuntimeException("Player not found");

        ScrollPane nextScrollPane = tokenToScrollPane.get(playerToken);
        mainStackPane.getChildren().set(0, nextScrollPane);
        currentScrollPane = nextScrollPane;
        currentGridPane = (GridPane) currentScrollPane.getContent();


        // switch hand
        currentHandHBox = tokenToHandHBox.get(playerToken);
        mainAnchorPane.getChildren().set(2, currentHandHBox);

        // update elements
        animalResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.ANIMAL).toString());
        plantResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.PLANT).toString());
        fungiResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.FUNGI).toString());
        insectResourcesCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Resources.INSECT).toString());
        quillItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.QUILL).toString());
        inkwellItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.INKWELL).toString());
        manuscriptItemsCounter.setText(slimGameModel.tokenToElements.get(playerToken).get(Items.MANUSCRIPT).toString());

        // enable / disable decks
        if (playerToken == selfPlayerToken && isPlayCardState) enableDecksPane();
        else disableDecks();

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

        ImageView cardImageView = ((ImageView) currentHandHBox.getChildren().get(handIndex));
        cardImageView.setImage(getCardImage(cardId, oppositeCardSide));

        if (isPlayable(slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex), visibleHandCardsSides.get(handIndex))) {
            if (currentTurn == selfPlayerToken && isPlayCardState) enableCardDragAndDrop(cardImageView);

            enableCardHover(cardImageView);
            showCardsPlayability();
        }
        else {
            disableCardDragAndDrop(cardImageView);
            disableCardHover(cardImageView);
            hideCardsPlayability();
        }
    }


    /**
     * Allows to show the slots where the card can be placed.
     *
     * @param event the MouseEvent event
     */
    public void handleHandMouseEntered(MouseEvent event) {
        showCardsPlayability();
    }

    /**
     * Hides the slots where the card can be placed.
     *
     * @param event the MouseEvent event
     */
    public void handleHandMouseExited(MouseEvent event) {
        hideCardsPlayability();
    }

    public boolean isPlayable(int cardId, CardSide cardSide) {
         Pair<CardSide, Boolean> result =
                 cardsPlayability.get(cardId).stream()
                 .filter(x -> x.first == cardSide)
                 .findFirst()
                 .orElseThrow(() -> new RuntimeException("No such card found in cards playability"));

         return result.second;
    }

    /**
     * Handles the start of the event of dragging the card around on a board (grid pane).
     *
     * @param event the DragEvent event
     */
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

        showCardsPlayability();

        content.putImage(resizedImage);
        dragboard.setContent(content);
        event.consume();
    }

    /**
     * Handles the event of dropping the drag event on a board (grid pane).
     * Allows to place a card in the dropped position.
     *
     * @param event the DragEvent event
     */
    public void handleHandDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        double dropX = event.getX();
        double dropY = event.getY();

        Coords indexes = getCellIndexes(currentGridPane, dropX, dropY);
        if (indexes == null) return;

        Coords coords = getCoordsFromIndexes(indexes);

        int handIndex = Integer.parseInt(dragboard.getString());
        if (handIndex < 0 || handIndex > 2) throw new RuntimeException("Illegal index: " + handIndex);

        int cardId = slimGameModel.tokenToHand.get(currentPlayerToken).get(handIndex);
        CardSide cardSide = visibleHandCardsSides.get(handIndex);

        if (availableSlots.contains(coords)) {
            playCard(currentPlayerToken, cardId, cardSide, coords);
            clearList = true;
            hideCardsPlayability();
        }

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
                        if (getCell(currentGridPane, cellIndexes.x, cellIndexes.y).isPresent()) {
                            return;
                        }

                        currentGridPane.add(pane, cellIndexes.x, cellIndexes.y);
                    });
                });
    }

    public void hideCardsPlayability() {
        availableSlots.stream()
                .map(this::getIndexesFromCoords)
                .filter(Objects::nonNull)
                .forEach(cellIndexes -> {
                    Platform.runLater(() -> {
                        getCell(currentGridPane, cellIndexes.x, cellIndexes.y).ifPresent(value -> {
                            if(value.getClass().equals(Pane.class)) currentGridPane.getChildren().remove(value);
                        });
                    });
                });

        if(clearList) availableSlots.clear();
    }

    /**
     * Allows to retrieve indexes inside a grid pane, given the coordinates of a point
     *
     * @param gridPane the grid pane
     * @param x x coordinate
     * @param y y coordinate
     * @return a Pair containing the two indexes, null if coordinates are not valid
     */
    private Coords getCellIndexes(GridPane gridPane, double x, double y) {
        int col = (int) (x / (currentGridPane.getWidth() / gridCellsCount.first));
        int row = (int) (y / (currentGridPane.getHeight() / gridCellsCount.second));

        if (row < 0 || row > gridPane.getRowConstraints().size() || col < 0 || col > gridPane.getColumnConstraints().size())
            return null;

        return new Coords(col, row);
    }

    /**
     * Allows to retrieve a node in a grid pane, given the indexes
     *
     * @param gridPane the grid pane
     * @param i row index
     * @param j column index
     * @return the node if present, Optional.isEmpty() otherwise
     */
    public Optional<Node> getCell(GridPane gridPane, int i, int j) {
        return gridPane.getChildren().stream()
                .filter(cell -> GridPane.getRowIndex(cell) == j && GridPane.getColumnIndex(cell) == i)
                .findFirst();
    }

    public List<Node> getCells(GridPane gridPane) {
        return gridPane.getChildren().stream()
                .toList();
    }

    /**
     * Allows to build an image of the card resource having the specified id and card side
     *
     * @param id id of the card
     * @param cardSide side of the card
     * @return the image of the card resource
     */
    public Image getCardImage(int id, CardSide cardSide) {
        return new Image("images/cards/" + (cardSide == CardSide.FRONT ? "fronts/" : "backs/") + id + ".png");
    }

    /**
     * Represents the action of playing a card on a board, GUI side.
     * Places the card and removes it from the hand.
     *
     * @param playerToken token of the player
     * @param cardId id of the card
     * @param cardSide side of the card played
     * @param coords coordinates where the card is played
     */
    public void playCard(PlayerToken playerToken, int cardId, CardSide cardSide, Coords coords) {
        setCard(playerToken, cardId, cardSide, coords);     // updates the board
        removeCardFromHand(playerToken, cardId);

        if (playerToken == selfPlayerToken) playCardDisable();

        isPlayCardState = false;

        gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToGameServer(new PlayCardCommand(playerToken, coords, cardId, cardSide));
            System.out.println("[INFO] Submitted PlayCardCommand");
        });
    }

    /**
     * Places a card in a player's board, GUI side.
     *
     * @param playerToken token of the player
     * @param cardId id of the card
     * @param cardSide side of the card played
     * @param coords coordinates where the card is played
     */
    public void setCard(PlayerToken playerToken, int cardId, CardSide cardSide, Coords coords) {
        GridPane gridPane = ((GridPane) tokenToScrollPane.get(playerToken).getContent());

        ImageView cardImageView = new ImageView(getCardImage(cardId, cardSide));
        cardImageView.setEffect(new DropShadow());
        cardImageView.setFitWidth(adjustedCardDimensions.first);
        cardImageView.setFitHeight(adjustedCardDimensions.second);

        StackPane stackPane = new StackPane(cardImageView);
        GridPane.setHalignment(stackPane, HPos.CENTER);
        GridPane.setValignment(stackPane, VPos.CENTER);
        GridPane.setHgrow(stackPane, Priority.NEVER);
        GridPane.setVgrow(stackPane, Priority.NEVER);

        Coords indexes = getIndexesFromCoords(coords);
        gridPane.add(stackPane, indexes.x, indexes.y);
    }

    /**
     * Allows to place a card into the player's hand, GUI side.
     *
     * @param playerToken token of the player
     * @param cardId id of the card to add
     */
    public void addCardToHand(PlayerToken playerToken, int cardId) {
        int handIndex = slimGameModel.tokenToHand.get(playerToken).indexOf(null);
        if (handIndex == -1) throw new RuntimeException("Hand is full");

        ImageView cardImageView = (ImageView) tokenToHandHBox.get(playerToken).getChildren().get(handIndex);
        cardImageView.setImage(getCardImage(cardId, CardSide.FRONT));

        if (playerToken == selfPlayerToken) {
            visibleHandCardsSides.set(handIndex, CardSide.FRONT);

            enableCardClick(cardImageView);
        }
    }

    /**
     * Allows to remove a card from a player's hand, GUI side.
     * Sets a placeholder in the slot emptied, and disables all functionalities of the slot, if needed.
     *
     * @param playerToken token of the player
     * @param cardId id of the card to remove
     */
    public void removeCardFromHand(PlayerToken playerToken, int cardId) {
        int handIndex = slimGameModel.tokenToHand.get(playerToken).indexOf(cardId);
        if (handIndex == -1) throw new RuntimeException("Card is not in player's hand");

        ImageView cardImageView = (ImageView) tokenToHandHBox.get(playerToken).getChildren().get(handIndex);
        cardImageView.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));

        if (playerToken == selfPlayerToken) {
            visibleHandCardsSides.set(handIndex, null);

            disableCardClick(cardImageView);
            disableCardHover(cardImageView);
            disableCardDragAndDrop(cardImageView);
        }
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

        addCardToHand(currentPlayerToken, drawnCardId);
        drawCardDisable();

        if (deck.equals(resourceDeck)) gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToGameServer(new DrawResourceDeckCardCommand(currentPlayerToken));
            System.out.println("[INFO] Submitted DrawResourceDeckCardCommand");
        });
        else if (deck.equals(goldDeck)) gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToGameServer(new DrawGoldDeckCardCommand(currentPlayerToken));
            System.out.println("[INFO] Submitted DrawGoldDeckCardCommand");
        });

        drawnCard = true;
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

        addCardToHand(currentPlayerToken, visibleSlotToCardId.get(visibleSlotImageView).get());

        if (deck.second.isEmpty()) {
            // TODO in event visibleSlotToVisibleListSetter.get(visibleSlotImageView).accept(null);
            visibleSlotImageView.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
            visibleSlotImageView.setDisable(true);

            return;
        }

        Integer nextCard = popCardFromDeck(deck);
        if (nextCard == null) throw new RuntimeException("Deck is empty: should be disabled");
        visibleSlotImageView.setImage(getCardImage(nextCard, CardSide.FRONT));
        // TODO in event visibleSlotToVisibleListSetter.get(visibleSlotImageView).accept(nextCard);

        drawCardDisable();

        if (deck.equals(resourceDeck)) gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToGameServer(new DrawVisibleResourceCardCommand(currentPlayerToken, visibleSlotImageView.equals(firstResourceImageView) ? 0 : 1));
            System.out.println("[INFO] Submitted DrawVisibleResourceCardCommand: token = " + currentPlayerToken + ", slot = " + (visibleSlotImageView.equals(firstResourceImageView) ? "0" : "1"));
        });
        else if (deck.equals(goldDeck)) gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToGameServer(new DrawVisibleGoldCardCommand(currentPlayerToken, visibleSlotImageView.equals(firstGoldImageView) ? 0 : 1));
            System.out.println("[INFO] Submitted DrawVisibleGoldCardCommand: token = " + currentPlayerToken + ", slot = " + (visibleSlotImageView.equals(firstGoldImageView) ? "0" : "1"));
        });

        drawnCard = true;
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

        int drawnCardId = deck.second.getLast();
        if (deck.second.size() == 1) {
            deck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
            deck.first.setDisable(true);
        }
        else deck.first.setImage(getCardImage(deck.second.get(deck.second.size() - 2), CardSide.BACK));

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

    public void handlePlayerPaneMouseClick(MouseEvent mouseEvent) {
        int playerIndex = playersPanes.indexOf((StackPane) mouseEvent.getTarget());

        switchPlayerView(userInfoToToken.get(lobby.get().users.get(playerIndex)));
    }

    public void enableDecksPane() {
        System.out.println("[DEBUG] Enabling decks pane");
        decksAnchorPane.setDisable(false);
    }

    public void disableDecksPane() {
        System.out.println("[DEBUG] Disabling decks pane");
        decksAnchorPane.setDisable(true);
    }

    public void showError() {

    }

    public void showInfo(boolean important) {

    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide, Coords playedCardCoordinates) {
        Platform.runLater(() -> {
            slimGameModel.applyPlayedCardEvent(playerToken, playedCardId, playedCardSide, playedCardCoordinates);

            if (playerToken != selfPlayerToken) {
                setCard(playerToken, playedCardId, playedCardSide, playedCardCoordinates);
                removeCardFromHand(playerToken, playedCardId);

                return;
            }

            System.out.println("[DEBUG] Enabling decks");
            drawCardEnable();
        });
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> {
            slimGameModel.applyDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex);

            if (playerToken != selfPlayerToken) {
                if (deckEmptied) goldDeck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
                else goldDeck.first.setImage(getCardImage(nextCardId, CardSide.BACK));

                setCardToHand(playerToken, drawnCardId, handIndex);
            }
        });
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> {
            slimGameModel.applyDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex);

            if (playerToken != selfPlayerToken) {
                if (deckEmptied) resourceDeck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
                else resourceDeck.first.setImage(getCardImage(nextCardId, CardSide.BACK));

                setCardToHand(playerToken, drawnCardId, handIndex);
            }
        });
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> {
            slimGameModel.applyDrawnVisibleResourceCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId, deckEmptied, nextCardId, handIndex);

            if(playerToken != selfPlayerToken) {
                if (deckEmptied) resourceDeck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
                else resourceDeck.first.setImage(getCardImage(nextCardId, CardSide.BACK));

                switch (drawnCardPosition) {
                    case 0 -> firstResourceImageView.setImage(getCardImage(replacementCardId, CardSide.FRONT));
                    case 1 -> secondResourceImageView.setImage(getCardImage(replacementCardId, CardSide.FRONT));
                }

                setCardToHand(playerToken, drawnCardId, handIndex);
            }
        });
    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> {
            slimGameModel.applyDrawnVisibleGoldCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId, deckEmptied, nextCardId, handIndex);

            if(playerToken != selfPlayerToken) {
                if (deckEmptied) goldDeck.first.setImage(getCardImage(DEFAULT_OBJECTIVE_CARD_ID, CardSide.BACK));
                else goldDeck.first.setImage(getCardImage(nextCardId, CardSide.BACK));

                switch (drawnCardPosition) {
                    case 0 -> firstGoldImageView.setImage(getCardImage(replacementCardId, CardSide.FRONT));
                    case 1 -> secondGoldImageView.setImage(getCardImage(replacementCardId, CardSide.FRONT));
                }

                setCardToHand(playerToken, drawnCardId, handIndex);
            }
        });
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        Platform.runLater(() -> {
            slimGameModel.applyPlayerElementsEvent(playerToken, resources);

            if (playerToken == currentPlayerToken) {
                animalResourcesCounter.setText(resources.get(Resources.ANIMAL).toString());
                plantResourcesCounter.setText(resources.get(Resources.PLANT).toString());
                fungiResourcesCounter.setText(resources.get(Resources.FUNGI).toString());
                insectResourcesCounter.setText(resources.get(Resources.INSECT).toString());
                quillItemsCounter.setText(resources.get(Items.QUILL).toString());
                inkwellItemsCounter.setText(resources.get(Items.INKWELL).toString());
                manuscriptItemsCounter.setText(resources.get(Items.MANUSCRIPT).toString());
            }
        });
    }

    /**
     * Allows the player to retrieve data on where his cards can be placed.
     * Event is only read by the intended receiver.
     */
    @Override
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots, Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        System.out.println("CARDS PLAYABILITY RECEIVED: ");

        Platform.runLater(() -> {
            if (playerToken != selfPlayerToken) return;     // don't care about events not related to self

            // update structures
            this.availableSlots.clear();
            this.availableSlots.addAll(availableSlots);
            this.cardsPlayability.clear();
            this.cardsPlayability.putAll(cardsPlayability);

            cardsPlayabilityEnable();
        });
    }

    /**
     * Enables needed panes and nodes for a player to see where his cards can be placed.
     * Enables mouse clicking for all cards, and hovering only for playable cards.
     */
    public void cardsPlayabilityEnable() {
        tokenToHandHBox.get(selfPlayerToken).getChildren().forEach(imageView -> {
            int handIndex = tokenToHandHBox.get(selfPlayerToken).getChildren().indexOf(imageView);

            Integer cardId = slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex);
            CardSide currentCardSide = visibleHandCardsSides.get(handIndex);

            if (cardId == null) return;     // enable nothing for empty slots

            if (cardsPlayability.get(cardId).stream().filter(x -> x.first == currentCardSide).anyMatch(x -> x.second))
                enableCardHover((ImageView) imageView);   // enable hover if card is playable

            enableCardClick((ImageView) imageView);     // enable mouse click if card is not null
        });
    }

    public void showImportantMessage(String text, boolean isError) {
        Platform.runLater(() -> {
            importantEventPane.getStyleClass().add(isError ? "important-error-msg" : "important-info-msg");
            importantEventText.setText(text);

            importantEventPane.setVisible(true);
            importantEventText.setVisible(true);

            FadeTransition fadePaneTransition = new FadeTransition(Duration.seconds(2), importantEventPane);
            fadePaneTransition.setDelay(Duration.seconds(3));
            fadePaneTransition.setFromValue(1);
            fadePaneTransition.setToValue(0);

            fadePaneTransition.setOnFinished(event -> {
                importantEventPane.setVisible(false);
                importantEventPane.setMouseTransparent(true);
                importantEventText.setVisible(false);
                importantEventText.setMouseTransparent(true);
            });

            fadePaneTransition.play();
        });
    }

    public void showMessage(String title, String text, boolean isError) {

    }

    @Override
    public void handleLastRoundEvent() {
        showImportantMessage("Last round started!", false);
    }

    /**
     * Handles a player turn event.
     * Notifies the player of the turn event, and if the current turn is the player's, enables his hand.
     *
     * @param turn the new turn
     */
    @Override
    public void handlePlayerTurnEvent(PlayerToken turn) {
        Platform.runLater(() -> {
            currentTurn = turn;
            isPlayCardState = true;

            UserInfo player = userInfoToToken.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(turn))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cannot find player."));

            showMessage("Turn changed!", player.name + "'s turn.", false);

            if (turn != selfPlayerToken) return;

            showImportantMessage("It's your turn! Play a card.", false);
            playCardEnable();
        });
    }

    /**
     * Enables needed panes and nodes for the player to play his card.
     * As card Hover and card Click are already enabled by CardsPlayabilityEvent, the PlayCardEvent only enables the drag and drop.
     */
    public void playCardEnable() {
        tokenToHandHBox.get(selfPlayerToken).getChildren().forEach(imageView -> {
            int handIndex = tokenToHandHBox.get(selfPlayerToken).getChildren().indexOf(imageView);

            Integer cardId = slimGameModel.tokenToHand.get(selfPlayerToken).get(handIndex);
            CardSide currentCardSide = visibleHandCardsSides.get(handIndex);

            if (cardId == null || cardsPlayability.get(cardId).stream().filter(x -> x.first == currentCardSide).anyMatch(x -> !x.second)) return;

            enableCardDragAndDrop((ImageView) imageView);
            enableGridDrop((GridPane) tokenToScrollPane.get(selfPlayerToken).getContent()); // enable the grid only if any card was enabled
        });
    }

    /**
     * Disables the structures after the player played the card.
     */
    public void playCardDisable() {
        disableGridDrop((GridPane) tokenToScrollPane.get(selfPlayerToken).getContent());    // disable grid drop

        tokenToHandHBox.get(selfPlayerToken).getChildren().forEach(cardImageView -> {   // disable cards
            disableCardHover((ImageView) cardImageView);
            disableCardDragAndDrop((ImageView) cardImageView);
        });
    }

    /**
     * Enables non-empty decks when it's self's turn.
     */
    public void drawCardEnable() {
        decksAnchorPane.setDisable(false);

        if(!resourceDeck.second.isEmpty()) resourceDeckImageView.setDisable(false);
        if(!goldDeck.second.isEmpty()) goldDeckImageView.setDisable(false);

        if(visibleSlotToCardId.get(firstResourceImageView).get() != null) firstResourceImageView.setDisable(false);
        if(visibleSlotToCardId.get(secondResourceImageView).get() != null) secondResourceImageView.setDisable(false);
        if(visibleSlotToCardId.get(firstGoldImageView).get() != null) firstGoldImageView.setDisable(false);
        if(visibleSlotToCardId.get(secondGoldImageView).get() != null) secondGoldImageView.setDisable(false);
    }

    /**
     *
     */
    public void drawCardDisable() {
        decksAnchorPane.setDisable(true);
    }

    /**
     * Enables drag and and drop of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void enableCardDragAndDrop(ImageView cardImageView) {
        cardImageView.setOnDragDetected(this::handleHandDragDetected);
        cardImageView.setOnDragDone(this::handleDragDone);
    }

    /**
     * Disables drag and drop of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void disableCardDragAndDrop(ImageView cardImageView) {
        cardImageView.setOnDragDetected(Event::consume);
        cardImageView.setOnDragDone(Event::consume);
    }

    /**
     * Enables hover of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void enableCardHover(ImageView cardImageView) {
        cardImageView.setOnMouseEntered(this::handleHandMouseEntered);
        cardImageView.setOnMouseExited(this::handleHandMouseExited);
    }

    /**
     * Disables hover of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void disableCardHover(ImageView cardImageView) {
        cardImageView.setOnMouseEntered(Event::consume);
        cardImageView.setOnMouseExited(Event::consume);
    }

    /**
     * Enables mouse click of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void enableCardClick(ImageView cardImageView) {
        cardImageView.setOnMouseClicked(this::handleHandMouseClicked);
    }

    /**
     * Disables mouse click of a card's image view.
     *
     * @param cardImageView the image view of interest
     */
    public void disableCardClick(ImageView cardImageView) {
        cardImageView.setOnMouseClicked(Event::consume);
    }

    /**
     * Enables drop actions on the grid pane.
     *
     * @param gridPane the grid pane
     */
    public void enableGridDrop(GridPane gridPane) {
        gridPane.setOnDragDropped(this::handleHandDragDropped);
        gridPane.setOnDragOver(this::handleDragOver);
    }

    /**
     * Disables drop actions on the grid pane.
     *
     * @param gridPane the grid pane
     */
    public void disableGridDrop(GridPane gridPane) {
        gridPane.setOnDragDropped(Event::consume);
        gridPane.setOnDragOver(Event::consume);
   }

    /**
     * Allows to set a card into a player's hand.
     */
   public void setCardToHand(PlayerToken playerToken, int cardId, int handIndex) {
       ((ImageView) tokenToHandHBox.get(playerToken).getChildren().get(handIndex)).setImage(getCardImage(cardId, CardSide.BACK));
   }

    /**
     * Allows to handle the game results
     *
     * @param gameResults results of the game
     */
    @Override
    public void handleGameResultsEvent(List<Trio<PlayerToken, Integer, Integer>> gameResults) {
        List<Node> players = endedPlayersVBox.getChildren();

        for (int i = 0; i < gameResults.size(); i++) {
            PlayerToken playerToken = gameResults.get(i).first;
            Integer points = gameResults.get(i).second;

            String name = userInfoToToken.entrySet().stream().filter(x -> x.getValue().equals(playerToken)).map(Map.Entry::getKey).findFirst().get().name;

            StackPane playerStackPane = (StackPane) players.get(i);
            ((Text) ((StackPane) ((HBox) playerStackPane.getChildren().getFirst()).getChildren().get(1)).getChildren().getFirst()).setText(name);
            ((Text) ((StackPane) ((HBox) playerStackPane.getChildren().getFirst()).getChildren().get(2)).getChildren().getFirst()).setText(String.valueOf(points));
        }

        gameResults.forEach(entry -> {
            Pair<PlayerToken, Integer> score = new Pair<>(entry.first, entry.second);

            int x = (int) (scoretrackPositions.get(score.second).x * endedScoretrackXRatio - endedScoretrackDimensions.first / 2.0);
            int y = (int) (scoretrackPositions.get(score.second).y * endedScoretrackYRatio - endedScoretrackDimensions.second / 2.0);

            Pair<Integer, Integer> translation = null;
            switch (score.first) {
                case PlayerToken.RED -> {
                    translation = new Pair<>((int) -(22 * endedScoretrackXRatio), (int) -(22 * endedScoretrackYRatio));
                    ImageView tokenImageView = new ImageView(new Image("images/tokens/token_red.png"));
                    endedScoretrackStackPane.getChildren().add(tokenImageView);
                    tokenImageView.setScaleX(0.04);
                    tokenImageView.setScaleY(0.04);

                    tokenImageView.setTranslateX(x + translation.first);
                    tokenImageView.setTranslateY(y + translation.second);
                }
                case PlayerToken.GREEN -> {
                    translation = new Pair<>((int) +(22 * endedScoretrackXRatio), (int) -(22 * endedScoretrackYRatio));
                    ImageView tokenImageView = new ImageView(new Image("images/tokens/token_green.png"));
                    endedScoretrackStackPane.getChildren().add(tokenImageView);
                    tokenImageView.setScaleX(0.04);
                    tokenImageView.setScaleY(0.04);

                    tokenImageView.setTranslateX(x + translation.first);
                    tokenImageView.setTranslateY(y + translation.second);
                }
                case PlayerToken.BLUE -> {
                    translation = new Pair<>((int) -(22 * endedScoretrackXRatio), (int) +(22 * endedScoretrackYRatio));
                    ImageView tokenImageView = new ImageView(new Image("images/tokens/token_blue.png"));
                    endedScoretrackStackPane.getChildren().add(tokenImageView);
                    tokenImageView.setScaleX(0.04);
                    tokenImageView.setScaleY(0.04);

                    tokenImageView.setTranslateX(x + translation.first);
                    tokenImageView.setTranslateY(y + translation.second);
                }
                case PlayerToken.YELLOW -> {
                    translation = new Pair<>((int) +(22 * endedScoretrackXRatio), (int) +(22 * endedScoretrackYRatio));
                    ImageView tokenImageView = new ImageView(new Image("images/tokens/token_yellow.png"));
                    endedScoretrackStackPane.getChildren().add(tokenImageView);
                    tokenImageView.setScaleX(0.04);
                    tokenImageView.setScaleY(0.04);

                    tokenImageView.setTranslateX(x + translation.first);
                    tokenImageView.setTranslateY(y + translation.second);

                    System.out.println("translation: " + x + ", " + y);
                    System.out.println("scale: " + tokenImageView.getScaleX() + ", " + tokenImageView.getScaleY());
                }
            }
        });

        quitButton.setOnAction(event -> {
            Platform.runLater(() -> {
                gui.changeToMenuScene();
            });
        });
        quitButton.setDisable(false);
        quitButton.setVisible(true);

        firstPlayerEndedPane.getStyleClass().add("player-ended-pane");
        secondPlayerEndedPane.getStyleClass().add("player-ended-pane");
        thirdPlayerEndedPane.getStyleClass().add("player-ended-pane");
        fourthPlayerEndedPane.getStyleClass().add("player-ended-pane");

        endedGameRectangle.setDisable(false);
        endedGameRectangle.setVisible(true);
        endedPlayersVBox.setDisable(false);
        endedPlayersVBox.setVisible(true);

        endedGamePane.getStyleClass().add("game-ended-pane");
        endedGamePane.setDisable(false);
        endedGamePane.setVisible(true);
    }

    public void sendMessage(ActionEvent event) {
        if (messageTextField.getText().isEmpty()) return;

        if (currentChatScrollPane.equals(globalChatScrollPane)) {
            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new GroupMessageCommand(selfUserInfo.get(), messageTextField.getText()));
                System.out.println("[INFO] Submitted GroupMessageCommand: from " + selfUserInfo.get().name);
            });
        }
        else {
            UserInfo receiver = userToChatScrollPane.entrySet().stream()
                    .filter(x -> x.getValue() == currentChatScrollPane)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new DirectMessageCommand(selfUserInfo.get(), receiver, messageTextField.getText()));
                System.out.println("[INFO] Submitted DirectMessageCommand: from " + selfUserInfo.get().name + " to " + receiver.name);
            });
        }

        messageTextField.clear();
    }

    @Override
    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        if (!sender.equals(selfUserInfo.get()) && !receiver.equals(selfUserInfo.get())) return;

        ScrollPane scrollPane = null;

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setMaxWidth(160);

        if (sender.equals(selfUserInfo.get())) {
            scrollPane = userToChatScrollPane.get(receiver);

            textFlow.setTextAlignment(TextAlignment.RIGHT);
            textFlow.getStyleClass().add("single-message");
            VBox.setMargin(textFlow, new Insets(0, 10, 0, 80));
            textFlow.setPadding(new Insets(3, 10, 3, 10));
        }
        else {
            scrollPane = userToChatScrollPane.get(sender);

            textFlow.setTextAlignment(TextAlignment.LEFT);
            textFlow.getStyleClass().add("single-message");
            VBox.setMargin(textFlow, new Insets(0, 80, 0, 10));
            textFlow.setPadding(new Insets(3, 10, 3, 10));
        }

        ((VBox) scrollPane.getContent()).getChildren().add(textFlow);
        scrollPane.layout();
        scrollPane.setVvalue(scrollPane.getVmax());
    }

    @Override
    public void handleGroupMessageEvent(UserInfo sender, String message) {
        TextFlow textFlow = new TextFlow();
        Text senderName = new Text(sender.name);
        senderName.setFont(Font.font("System", FontWeight.BOLD, 13.0));

        textFlow.getChildren().addAll(senderName, new Text("\n"), new Text(message));

        textFlow.setMaxWidth(160);

        if (sender.equals(selfUserInfo.get())) {
            textFlow.setTextAlignment(TextAlignment.RIGHT);
            textFlow.getStyleClass().add("single-message");
            VBox.setMargin(textFlow, new Insets(10, 10, 0, 80));
            textFlow.setPadding(new Insets(3, 10, 3, 10));
        }
        else {
            textFlow.setTextAlignment(TextAlignment.LEFT);
            textFlow.getStyleClass().add("single-message");
            VBox.setMargin(textFlow, new Insets(10, 80, 0, 10));
            textFlow.setPadding(new Insets(3, 10, 3, 10));
        }

        ((VBox) globalChatScrollPane.getContent()).getChildren().add(textFlow);

        updateChatScrollPane(globalChatScrollPane);
    }

    public void updateChatScrollPane(ScrollPane scrollPane) {
        scrollPane.layout();
        scrollPane.setVvalue(scrollPane.getVmax());
    }

    public void handleScoreTrackEvent(PlayerToken playerToken, int score) {
        int x = (int) (scoretrackPositions.get(score).x * scoretrackXRatio - scoreTrackDimensions.first / 2);
        int y = (int) (scoretrackPositions.get(score).y * scoretrackYRatio - scoreTrackDimensions.second / 2);

        Pair<Integer, Integer> translation = null;
        switch (playerToken) {
            case PlayerToken.RED -> {
                translation = new Pair<>((int) -(22 * scoretrackXRatio), (int) -(22 * scoretrackYRatio));
                redTokenImageView.setTranslateX(x + translation.first);
                redTokenImageView.setTranslateY(y + translation.second);
            }
            case PlayerToken.GREEN -> {
                translation = new Pair<>((int) +(22 * scoretrackXRatio), (int) -(22 * scoretrackYRatio));
                greenTokenImageView.setTranslateX(x + translation.first);
                greenTokenImageView.setTranslateY(y + translation.second);
            }
            case PlayerToken.BLUE -> {
                translation = new Pair<>((int) -(22 * scoretrackXRatio), (int) +(22 * scoretrackYRatio));
                blueTokenImageView.setTranslateX(x + translation.first);
                blueTokenImageView.setTranslateY(y + translation.second);
            }
            case PlayerToken.YELLOW -> {
                translation = new Pair<>((int) +(22 * scoretrackXRatio), (int) +(22 * scoretrackYRatio));
                yellowTokenImageView.setTranslateX(x + translation.first);
                yellowTokenImageView.setTranslateY(y + translation.second);
            }
        }
    }
}