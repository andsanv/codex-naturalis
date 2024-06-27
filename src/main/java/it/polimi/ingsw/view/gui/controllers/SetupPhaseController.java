package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.Card;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.gui.GUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.*;

public class SetupPhaseController extends Controller {
    private Map<UserInfo, PlayerToken> userInfoToToken = null;
    private UserInfo selfUserInfo = null;
    private GUI gui;

    // general
    @FXML public StackPane mainStackPane;


    /* TOKEN SELECTION PHASE */
    @FXML public StackPane tokenPane;
    @FXML public Text tokenText;
    @FXML public Button tokenNextButton;

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
    @FXML public Button objectiveNextButton;

    @FXML public ImageView firstObjectiveCardImageView;
    @FXML public ImageView secondObjectiveCardImageView;
    public List<ImageView> objectiveCardsImageViewsList;

    // network
    public Integer selectedCard = null;
    List<Integer> availableObjectiveCards = new ArrayList<>(Arrays.asList(59, 80));


    public SetupPhaseController() {}

    public void initialize(GUI gui, UserInfo selfUserInfo) {
        this.gui = gui;
        this.selfUserInfo = selfUserInfo;

        initializeTokens();
    }

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
    }



    /* GUI EVENTS HANDLERS */

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
            // TODO send command with selected token
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
            // TODO send command with selected card
        });
    }


    // objective phase

    public void handleObjectiveImageViewMouseClicked(MouseEvent event) {
        Platform.runLater(() -> {
            Integer cardIndex = objectiveCardsImageViewsList.indexOf((ImageView) event.getTarget());

            if (selectedCard != null && selectedCard.equals(availableObjectiveCards.get(cardIndex))) {
                selectedCard = null;
                objectiveNextButton.setDisable(true);

                // TODO set css on image view

                return;
            }

            selectedCard = availableObjectiveCards.get(cardIndex);
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
            // TODO send command with selected card
        });
    }


    /* NETWORK EVENTS HANDLERS */

    // token phase

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken playerToken) {
        Platform.runLater(() -> {
            userInfoToToken.put(player, playerToken);

            if (playerToken == selectedToken && !player.equals(selfUserInfo)) {
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
            this.userInfoToToken = userInfoToToken;

            // TODO send draw card command
        });
    }


    // starter card phase

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        Platform.runLater(() -> {
            if (playerToken != userInfoToToken.get(selfUserInfo)) return;

            initializeStarter(drawnCardId);

            mainStackPane.getChildren().getFirst().setDisable(true);
            mainStackPane.getChildren().removeFirst();
        });
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {

    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        Platform.runLater(() -> {
            // TODO send draw objective cards command
        });
    }


    // objective card phase

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
        Platform.runLater(() -> {
           if (playerToken != userInfoToToken.get(selfUserInfo)) return;

           initializeObjective(new ArrayList<>(Arrays.asList(drawnCardId, secondDrawnCardId)));

            mainStackPane.getChildren().getFirst().setDisable(true);
            mainStackPane.getChildren().removeFirst();
        });
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {

    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        Platform.runLater(() -> {
            mainStackPane.getChildren().getFirst().setDisable(true);
            mainStackPane.getChildren().removeFirst();
        });
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/tempGameView.fxml"));
            Parent root;

            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            TempGameController controller = loader.getController();

//            try {
//                controller.initialize(gui, slimGameModel);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            changeScene(root, mainStackPane.getScene().getWindow());
        });
    }

    public Image getCardImage(int id, CardSide cardSide) {
        return new Image("images/cards/" + (cardSide == CardSide.FRONT ? "fronts/" : "backs/") + id + ".png");
    }
}
