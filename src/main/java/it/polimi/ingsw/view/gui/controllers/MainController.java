package it.polimi.ingsw.view.gui.controllers;


import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;

/**
 * The class is used to allow the connection handler to have a single controller over time.
 * This controller calls all his methods on the sub-controller.
 */
public class MainController extends Controller {
    /**
     * Lock object to synchronize on the subController object.
     */
    public final Object controllerLock = new Object();

    /**
     * The actual controller of the scenes.
     */
    public Controller subController;

    public MainController(ConfigController subController) {
        this.subController = subController;
    }

    /**
     * Allows to set a new subController, to go from a scene to another.
     *
     * @param subController the new subController
     */
    public void setSubController(Controller subController) {
        synchronized (controllerLock) {
            this.subController = subController;
        }
    }

    /**
     * subController getter.
     *
     * @return the subController
     */
    public Controller getSubController() {
        synchronized (controllerLock) {
            return subController;
        }
    }



    /* NETWORK */

    // main

    /**
     * This method handles the reception of the UserInfo and an eventual error
     * message.
     *
     * @param userInfo the userInfo
     */
    public void handleLoginEvent(UserInfo userInfo, String error) {
        System.out.println("received login event");
        Platform.runLater(() ->  {
            System.out.println("done");
        });
        System.out.println("tested outside");
    }

    /**
     * This method handles the reception of generic server error
     *
     * @param error description of the error
     */
    public void handleServerError(String error) {
        Platform.runLater(() -> subController.handleServerError(error));
    }

    /**
     * This method handles the reception of the active lobbies
     *
     * @param lobbies list of lobbies
     */
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        Platform.runLater(() -> subController.handleLobbiesEvent(lobbies));
    }

    /**
     * This method handles the reconnection to an active game
     *
     * @param userToToken UserInfo to PlayerToken mapping
     * @param slimModel   the simplified game model
     */
    public void handleReconnetionToGame(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken) {
        Platform.runLater(() -> subController.handleReconnetionToGame(slimModel, userToToken));
    }

    /**
     * This method is called when the user tries to join a lobby or create one while
     * being in another.
     */
    public void handleJoinLobbyError(String message) {
        Platform.runLater(() -> subController.handleJoinLobbyError(message));
    }

    /**
     * Method called when an error occurred while starting a game.
     * Possible errors are user is not the lobby manager, the lobby doesn't
     * exist or there aren't enough players in the lobby.
     *
     * @param message the error message
     */
    public void handleStartGameError(String message) {
        Platform.runLater(() -> subController.handleStartGameError(message));
    }

    /**
     * Method called when an error occurred while creating a lobby.
     * The user could already be in another one.
     *
     * @param message the error message
     */
    public void handleCreateLobbyError(String message) {
        Platform.runLater(() -> subController.handleCreateLobbyError(message));
    }

    /**
     * Method called when the user tried to leave a lobby that was non-existent or
     * a lobby he wasn't in.
     *
     * @param message the error message
     */
    public void handleLeaveLobbyError(String message) {
        Platform.runLater(() -> subController.handleLeaveLobbyError(message));
    }

    public void handleGameStartedEvent(List<UserInfo> users) {
        Platform.runLater(() -> subController.handleGameStartedEvent(users));
    }


    // game

    /**
     * This method handles the received update of the score track
     *
     * @param senderToken the player token to which the score update refers
     * @param score       the new score of the player
     */
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        Platform.runLater(() -> subController.handleScoreTrackEvent(senderToken, score));
    }

    /**
     * This method handles the received update of a played card
     *
     * @param playerToken           the player token which played the card
     * @param playedCardId          the id of the played card
     * @param playedCardSide        the side of the played card
     * @param playedCardCoordinates the coordinates of the played card
     */
    public void handlePlayedCardEvent(
            PlayerToken playerToken,
            int playedCardId,
            CardSide playedCardSide,
            Coords playedCardCoordinates) {
        Platform.runLater(() -> subController.handlePlayedCardEvent(playerToken, playedCardId, playedCardSide, playedCardCoordinates));
    }

    /**
     * This method handles the received update of a drawn card from the gold deck
     *
     * @param playerToken the token of the player who draws the card from the gold
     *                    deck
     * @param drawnCardId the drawn card id
     * @param deckEmptied a flag that is true if the deck is now empty, false
     *                    otherwise
     * @param nextCardId  the id of the next card; null if there is no next card
     * @param handIndex   the position (0,1,2) of the drawn card in the player hand
     */
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
                                             Integer nextCardId, int handIndex) {
        Platform.runLater(() -> subController.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex));
    }

    /**
     * This method handles the received update of a drawn card from the resource
     * deck
     *
     * @param playerToken the player token which drew the card
     * @param drawnCardId the id of the drawn card
     * @param deckEmptied a flag that is true if the deck is now empty, false
     *                    otherwise
     * @param nextCardId  the id of the next card; null if there is no next card
     * @param handIndex   the position (0,1,2) of the drawn card in the player hand
     */
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
                                                 Integer nextCardId, int handIndex) {
        Platform.runLater(() -> subController.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId, deckEmptied, nextCardId, handIndex));
    }

    /**
     * This method handles the received update of a drawn card from the visible
     * resource deck
     *
     * @param playerToken       the player token which drew the card
     * @param drawnCardPosition the position of the drawn card between the visibles
     * @param drawnCardId       the id of the drawn card
     *                          // TODO update javadocs
     */
    public void handleDrawnVisibleResourceCardEvent(
            PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Integer replacementCardId,
            boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> subController.handleDrawnVisibleResourceCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId, deckEmptied, nextCardId, handIndex));
    }

    public void handleDrawnVisibleGoldCardEvent(
            PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Integer replacementCardId,
            boolean deckEmptied, Integer nextCardId, int handIndex) {
        Platform.runLater(() -> subController.handleDrawnVisibleGoldCardEvent(playerToken, drawnCardPosition, drawnCardId, replacementCardId, deckEmptied, nextCardId, handIndex));
    }

    /**
     * This method handles the received update of a drawn card from the starter deck
     *
     * @param playerToken the player token which drew the card
     * @param drawnCardId the id of the drawn card
     */
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        Platform.runLater(() -> subController.handleDrawnStarterCardEvent(playerToken, drawnCardId));
    }

    /**
     * This method handles the received update of the chosen side of a starter card
     *
     * @param playerToken the player token which chose the side
     * @param cardSide    the chosen side
     */
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        Platform.runLater(() -> subController.handleChosenStarterCardSideEvent(playerToken, cardSide));

    }

    /**
     * This method handles the received update about the end of the starter card
     * drawn phase
     */
    public void handleEndedStarterCardPhaseEvent() {
        Platform.runLater(() -> subController.handleEndedStarterCardPhaseEvent());
    }

    /**
     * This method handles the received update of the drawn cards from the objective
     * deck
     *
     * @param playerToken       the player token which drew the cards
     * @param firstDrawnCardId  the id of the first drawn card
     * @param secondDrawnCardId the id of the second drawn card
     */
    public void handleDrawnObjectiveCardsEvent(
            PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
        Platform.runLater(() -> subController.handleDrawnObjectiveCardsEvent(playerToken, firstDrawnCardId, secondDrawnCardId));
    }

    /**
     * This method handles the received update of the chosen objective card
     *
     * @param playerToken  the player token which chose the card
     * @param chosenCardId the id of the chosen card
     */
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        Platform.runLater(() -> subController.handleChosenObjectiveCardEvent(playerToken, chosenCardId));
    }

    /**
     * This method handles the received update about the end of the objective card
     * drawn phase
     */
    public void handleEndedObjectiveCardPhaseEvent() {
        Platform.runLater(() -> subController.handleEndedObjectiveCardPhaseEvent());
    }

    /**
     * This method handles the received update about the drawn common objective
     * cards
     *
     * @param firstCommonObjectiveId  the id of the first common objective card
     * @param secondCommonObjectiveId the id of the second common objective card
     */
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        Platform.runLater(() -> subController.handleCommonObjectiveEvent(firstCommonObjectiveId, secondCommonObjectiveId));
    }

    /**
     * This method handles the received update about a direct player to player
     * message
     *
     * @param sender   the player which sent the message
     * @param receiver the player which receive the message
     * @param message  the content of the message
     */
    public void handleDirectMessageEvent(
            UserInfo sender, UserInfo receiver, String message) {
        Platform.runLater(() -> subController.handleDirectMessageEvent(sender, receiver, message));
    }

    /**
     * This method handles the received update about a group message
     *
     * @param sender  the player who sent the message
     * @param message the content of the message
     */
    public void handleGroupMessageEvent(UserInfo sender, String message) {
        Platform.runLater(() -> subController.handleGroupMessageEvent(sender, message));
    }

    /**
     * This method handles the received update about the start of the game
     *
     * @param player        the player which chose the token
     * @param assignedToken the token assigned to the player
     */
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        Platform.runLater(() -> subController.handleTokenAssignmentEvent(player, assignedToken));
    }

    /**
     * This method handles the received update about the end of the token selection
     * phase
     *
     * @param userInfoToToken  map from users to their tokens
     * @param timeLimitReached flag that is true if the time limit is reached
     */
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {
        Platform.runLater(() -> subController.handleEndedTokenPhaseEvent(userInfoToToken, timeLimitReached));
    }

    // TODO: change name to the method. It is not clear what it does
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        Platform.runLater(() -> subController.handlePlayedCardEvent(playerToken, secretObjectiveCardId));
    }

    // TODO: change method name
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        Platform.runLater(() -> subController.handlePlayerElementsEvent(playerToken, resources));
    }

    /**
     * This method handles the received update about a generic game error
     *
     * @param error the error
     */
    public void handleGameError(String error) {
        Platform.runLater(() -> subController.handleGameError(error));
    }

    /**
     * This method handles the received update about the results of the game
     *
     * @param gameResults this list contains the information about the game
     */
    public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        Platform.runLater(() -> subController.handleGameResultsEvent(gameResults));
    }

    /**
     * This method handles the received event about the possible coordinates where a
     * card can the placed
     *
     * @param playerToken      the player token which can place the card
     * @param availableSlots   the list of the available slots
     * @param cardsPlayability the map of the cards playability
     */
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
                                            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        Platform.runLater(() -> subController.handleCardsPlayabilityEvent(playerToken, availableSlots, cardsPlayability));
    }

    public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints) {
        Platform.runLater(() -> subController.handleLimitPointsReachedEvent(playerToken, score, limitPoints));
    }

    /**
     * Handles the ending of the initialization phase of the game, receiving a slim
     * game model and setting up a client's internal model.
     *
     * @param slimGameModel slimGameModel representing the initial collections
     */
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        Platform.runLater(() -> subController.handleEndedInitializationPhaseEvent(slimGameModel));
    }

    public void handlePlayerTurnEvent(PlayerToken currentPlayer) {
        Platform.runLater(() -> subController.handlePlayerTurnEvent(currentPlayer));
    }

    public void handleLastRoundEvent() {
        Platform.runLater(() -> subController.handleLastRoundEvent());
    }
}
