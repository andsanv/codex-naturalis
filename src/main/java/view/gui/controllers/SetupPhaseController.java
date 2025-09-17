package view.gui.controllers;

import controller.usermanagement.LobbyInfo;
import controller.usermanagement.UserInfo;
import distributed.commands.game.*;
import model.SlimGameModel;
import model.card.CardSide;
import model.player.PlayerToken;
import view.gui.GUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller that manages the first part of the game.
 */
public class SetupPhaseController extends Controller {
    public AtomicReference<LobbyInfo> currentLobby;

    private Map<UserInfo, PlayerToken> userInfoToToken = null;
    private PlayerToken selfPlayerToken = null;

    // general
    @FXML public StackPane mainStackPane;

    /* TOKEN SELECTION PHASE */
    @FXML public StackPane tokenPane;
    @FXML public Text tokenText;
    @FXML public Button tokenNextButton;
    @FXML public StackPane tokenTextPane;

    @FXML public ImageView redTokenImageView;
    @FXML public ImageView greenTokenImageView;
    @FXML public ImageView blueTokenImageView;
    @FXML public ImageView yellowTokenImageView;

    public List<ImageView> tokenImageViews;
    public List<PlayerToken> tokens;
    public PlayerToken selectedToken = null;


    /* STARTER CARD PHASE */
    @FXML public StackPane starterCardPane;
    @FXML public Text starterText;
    @FXML public StackPane starterTextPane;
    @FXML public Button starterNextButton;

    @FXML public ImageView frontStarterCardSideImageView;
    @FXML public ImageView backStarterCardSideImageView;

    public List<ImageView> starterCardSideImageViews;
    public CardSide selectedStarterCardSide = null;
    public List<CardSide> availableStarterCardSides = new ArrayList<>(Arrays.asList(CardSide.FRONT, CardSide.BACK));


    /* OBJECTIVE CARDS PHASE */
    // interface
    @FXML public StackPane objectiveCardsPane;
    @FXML public Text objectiveText;
    @FXML public StackPane objectiveTextPane;
    @FXML public Button objectiveNextButton;

    @FXML public ImageView firstObjectiveCardImageView;
    @FXML public ImageView secondObjectiveCardImageView;
    public List<ImageView> objectiveCardsImageViewsList;

    // network
    public Integer selectedCard = null;
    List<Integer> availableObjectiveCards = new ArrayList<>(Arrays.asList(59, 80));


    public SetupPhaseController() {}

    /**
     * Controller initializer.
     *
     * @param gui the GUI application
     */
    public void initialize(GUI gui) {
        this.gui = gui;
        this.connectionHandler = gui.connectionHandler;
        this.selfUserInfo = gui.selfUserInfo;
        this.currentLobby = gui.currentLobby;

        initializeTokens();
        applyCss();
    }

    /**
     * Allows to handle the first part of the game, where tokens are chosen.
     */
    public void initializeTokens() {
        userInfoToToken = new HashMap<>();

        tokenImageViews = new ArrayList<>(Arrays.asList(redTokenImageView, greenTokenImageView, blueTokenImageView, yellowTokenImageView));
        tokens = new ArrayList<>(Arrays.asList(PlayerToken.RED, PlayerToken.GREEN, PlayerToken.BLUE, PlayerToken.YELLOW));

        tokenImageViews.forEach(imageView -> {
            imageView.setOnMouseClicked(this::handleTokenImageViewMouseClicked);
            imageView.setOnMouseEntered(this::handleTokenImageViewMouseEntered);
            imageView.setOnMouseExited(this::handleTokenImageViewMouseExited);
        });

        tokenNextButton.setOnAction(this::handleTokenNextButtonAction);
        tokenNextButton.setDisable(true);

        tokenPane.setDisable(false);
        starterCardPane.setDisable(true);
        objectiveCardsPane.setDisable(true);

        tokenPane.setVisible(true);
        starterCardPane.setVisible(false);
        objectiveCardsPane.setVisible(false);
    }

    public void initializeStarter(Integer starterCardId) {
        frontStarterCardSideImageView.setImage(getCardImage(starterCardId, CardSide.FRONT));
        backStarterCardSideImageView.setImage(getCardImage(starterCardId, CardSide.BACK));

        starterCardSideImageViews = new ArrayList<>(Arrays.asList(frontStarterCardSideImageView, backStarterCardSideImageView));

        starterCardSideImageViews.forEach(imageView -> {
            imageView.setOnMouseClicked(this::handleStarterImageViewMouseClicked);
            imageView.setOnMouseEntered(this::handleStarterImageViewMouseEntered);
            imageView.setOnMouseExited(this::handleStarterImageViewMouseExited);
        });

        starterNextButton.setOnAction(this::handleStarterNextButtonAction);
        starterNextButton.setDisable(true);

        tokenPane.setDisable(true);
        starterCardPane.setDisable(false);
        objectiveCardsPane.setDisable(true);

        tokenPane.setVisible(false);
        starterCardPane.setVisible(true);
        objectiveCardsPane.setVisible(false);

    }

    public void initializeObjective(List<Integer> availableObjectiveCards) {
        this.availableObjectiveCards = availableObjectiveCards;

        objectiveCardsImageViewsList = new ArrayList<>(Arrays.asList(firstObjectiveCardImageView, secondObjectiveCardImageView));

        objectiveCardsImageViewsList.forEach(imageView -> {
            imageView.setOnMouseClicked(this::handleObjectiveImageViewMouseClicked);
            imageView.setOnMouseEntered(this::handleObjectiveImageViewMouseEntered);
            imageView.setOnMouseExited(this::handleObjectiveImageViewMouseExited);
        });

        firstObjectiveCardImageView.setImage(getCardImage(availableObjectiveCards.get(0), CardSide.FRONT));
        secondObjectiveCardImageView.setImage(getCardImage(availableObjectiveCards.get(1), CardSide.FRONT));

        objectiveNextButton.setOnAction(this::handleObjectiveNextButtonAction);
        objectiveNextButton.setDisable(true);

        tokenPane.setDisable(true);
        starterCardPane.setDisable(true);
        objectiveCardsPane.setDisable(false);

        tokenPane.setVisible(false);
        starterCardPane.setVisible(false);
        objectiveCardsPane.setVisible(true);
    }



    /* GUI EVENTS HANDLERS */

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        lobbies.stream()
                .filter(l -> l.id == currentLobby.get().id)
                .filter(l -> l.gameStarted)
                .findFirst()
                .ifPresent(l -> {
                    System.out.println("[INFO] Game started");;
                    gui.changeToSetupPhaseScene();
                });
    }


    // token phase
    public void handleTokenImageViewMouseClicked(MouseEvent event) {
        Platform.runLater(() -> {
            PlayerToken token = tokens.get(tokenImageViews.indexOf((ImageView) event.getTarget()));

            if (selectedToken != null && selectedToken == token) {
                selectedToken = null;
                tokenNextButton.setDisable(true);

                // TODO set css on image view

                return;
            }

            selectedToken = token;
            tokenNextButton.setDisable(false);
            // TODO ingrandire quella nuova e rimpicciolire quella vecchia se c'era
        });
    }

    public void handleTokenImageViewMouseEntered(MouseEvent event) {
        Platform.runLater(() -> {
            if (tokenImageViews.indexOf((ImageView) event.getTarget()) != tokens.indexOf(selectedToken)) {
                // TODO ingrandire imageView
            }
        });
    }

    public void handleTokenImageViewMouseExited(MouseEvent event) {
        Platform.runLater(() -> {
            if (tokenImageViews.indexOf((ImageView) event.getTarget()) != tokens.indexOf(selectedToken)) {
                // TODO rimpicciolire imageView
            }
        });
    }

    public void handleTokenNextButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            if (selectedToken == null) throw new RuntimeException("Button should be disabled as no token is selected.");

            tokenPane.setDisable(true);

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new SelectTokenCommand(selfUserInfo.get(), selectedToken));
                System.out.println("[INFO] Submitted SelectTokenCommand with token: " + selectedToken);
            });
        });
    }


    // starter card side phase

    public void handleStarterImageViewMouseClicked(MouseEvent event) {
        Platform.runLater(() -> {
            CardSide side = availableStarterCardSides.get(starterCardSideImageViews.indexOf((ImageView) event.getTarget()));

            if (selectedStarterCardSide != null && selectedStarterCardSide == side) {
                selectedStarterCardSide = null;
                starterNextButton.setDisable(true);

                // TODO set css on image view

                return;
            }

            selectedStarterCardSide = side;
            starterNextButton.setDisable(false);
            // TODO ingrandire quella nuova e rimpicciolire quella vecchia se c'era
        });
    }

    public void handleStarterImageViewMouseEntered(MouseEvent event) {
        Platform.runLater(() -> {
            if (starterCardSideImageViews.indexOf((ImageView) event.getTarget()) != availableStarterCardSides.indexOf(selectedStarterCardSide)) {
                // TODO ingrandire imageView
            }
        });
    }

    public void handleStarterImageViewMouseExited(MouseEvent event) {
        Platform.runLater(() -> {
            if (starterCardSideImageViews.indexOf((ImageView) event.getTarget()) != availableStarterCardSides.indexOf(selectedStarterCardSide)) {
                // TODO rimpicciolire imageView
            }
        });
    }

    public void handleStarterNextButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            if (selectedStarterCardSide == null) throw new RuntimeException("Button should be disabled as no card is selected.");

            starterCardPane.setDisable(true);

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new SelectStarterCardSideCommand(selfPlayerToken, selectedStarterCardSide));
                System.out.println("[INFO] Submitted SelectStarterCardSide with card side: " + selectedStarterCardSide);
            });
        });
    }


    // objective phase

    public void handleObjectiveImageViewMouseClicked(MouseEvent event) {
        Platform.runLater(() -> {
            Integer cardIndex = objectiveCardsImageViewsList.indexOf((ImageView) event.getTarget());

            if (selectedCard != null && selectedCard.equals(cardIndex)) {
                selectedCard = null;
                objectiveNextButton.setDisable(true);

                // TODO set css on image view

                return;
            }

            selectedCard = cardIndex;
            objectiveNextButton.setDisable(false);

            // TODO ingrandire quella nuova e rimpicciolire quella vecchia se c'era
        });
    }

    public void handleObjectiveImageViewMouseEntered(MouseEvent event) {
        Platform.runLater(() -> {
            if (objectiveCardsImageViewsList.indexOf((ImageView) event.getTarget()) != availableObjectiveCards.indexOf(selectedCard)) {
                // TODO ingrandire imageView
            }
        });
    }

    public void handleObjectiveImageViewMouseExited(MouseEvent event) {
        Platform.runLater(() -> {
            if (objectiveCardsImageViewsList.indexOf((ImageView) event.getTarget()) != availableObjectiveCards.indexOf(selectedCard)) {
                // TODO rimpicciolire imageView
            }
        });
    }

    public void handleObjectiveNextButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            if (selectedCard == null) throw new RuntimeException("Button should be disabled as no card is selected.");

            objectiveCardsPane.setDisable(true);

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new SelectObjectiveCardCommand(selfPlayerToken, selectedCard));
                System.out.println("[INFO] Submitted SelectObjectiveCardCommand with card: " + selectedCard);
            });
        });
    }


    /* NETWORK EVENTS HANDLERS */

    // token phase

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken playerToken) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Token " + playerToken + " assigned to " + player);

            userInfoToToken.put(player, playerToken);

            if (playerToken == selectedToken && !player.equals(selfUserInfo.get())) {
                selectedToken = null;

                tokenPane.setDisable(false);
                tokenNextButton.setDisable(true);
            }

            ColorAdjust lowerBrightness = new ColorAdjust();
            lowerBrightness.setBrightness(-0.5);
            tokenImageViews.get(tokens.indexOf(playerToken)).setEffect(lowerBrightness);
            tokenImageViews.get(tokens.indexOf(playerToken)).setDisable(true);
        });
    }

    @Override
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received EndedTokenPhaseEvent");
            this.userInfoToToken = userInfoToToken;
            this.selfPlayerToken = userInfoToToken.get(selfUserInfo.get());

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new DrawStarterCardCommand(selfPlayerToken));
                System.out.println("[INFO] Submitted DrawStarterCardCommand");
            });
        });
    }




    // starter card phase

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received DrawnStarterCardEvent from " + playerToken + " with card: " + drawnCardId);
            if (playerToken != userInfoToToken.get(selfUserInfo.get())) return;

            initializeStarter(drawnCardId);

            System.out.println("before: " + mainStackPane.getChildren());;
            // mainStackPane.getChildren().removeLast();
            System.out.println("starter card pane is visible: " + starterCardPane.isVisible());
            System.out.println("starter card pane is disabled: " + starterCardPane.isDisable());
            System.out.println("after: " + mainStackPane.getChildren());
        });
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received ChosenStarterCardSide from " + playerToken + " with card side: " + cardSide);
        });
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received EndedStarterCardPhaseEvent");

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToGameServer(new DrawObjectiveCardsCommand(selfPlayerToken));
                System.out.println("[INFO] Submitted DrawObjectiveCardsCommand");
            });
        });
    }


    // objective card phase

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received DrawnObjectiveCardsEvent from " + playerToken);

            if (playerToken != userInfoToToken.get(selfUserInfo.get())) return;

            initializeObjective(new ArrayList<>(Arrays.asList(drawnCardId, secondDrawnCardId)));

            // mainStackPane.getChildren().removeLast();
        });
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received ChosenObjectiveCardEvent from " + playerToken + " with card: " + chosenCardId);
        });
    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        Platform.runLater(() -> {
            System.out.println("[INFO] Received EndedObjectiveCardPhaseEvent");

            mainStackPane.getChildren().removeLast();
        });
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        Platform.runLater(() -> {
            gui.changeToGameScene(slimGameModel, userInfoToToken);
        });
    }

    /**
     * Allows to build an image of the card resource having the specified id and card side
     *
     * @param id id of the card
     * @param cardSide side of the card
     * @return the image of the card resource
     */
    public Image getCardImage(int id, CardSide cardSide) {
        InputStream stream = getClass().getResourceAsStream("/images/cards/" + (cardSide == CardSide.FRONT ? "fronts/" : "backs/") + id + ".png");

        return new Image(stream);
    }

    /**
     * Applies CSS to the view.
     */
    public void applyCss() {

        //token css
        tokenPane.getStyleClass().add("main-pane");
        tokenNextButton.getStyleClass().add("button-next");
        tokenTextPane.getStyleClass().add("text-pane");

        //starter css
        starterCardPane.getStyleClass().add("main-pane");
        starterNextButton.getStyleClass().add("button-next");
        starterTextPane.getStyleClass().add("text-pane");

        //starter css
        objectiveCardsPane.getStyleClass().add("main-pane");
        objectiveNextButton.getStyleClass().add("button-next");
        objectiveTextPane.getStyleClass().add("text-pane");
    }
}
