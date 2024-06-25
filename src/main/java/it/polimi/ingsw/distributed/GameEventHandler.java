package it.polimi.ingsw.distributed;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

/**
 * This interface represents the game event handler, which is used to handle the
 * game events coming from the server.
 * This interface is implemented by the UI abstract class.
 */
public interface GameEventHandler {

    /**
     * This method handles the received update of the score track
     * 
     * @param senderToken the player token to which the score update refers
     * @param score       the new score of the player
     */
    public void handleScoreTrackEvent(PlayerToken senderToken, int score);

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
            Coords playedCardCoordinates);

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
            Integer nextCardId, int handIndex);

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
            Integer nextCardId, int handIndex);

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
            boolean deckEmptied, Integer nextCardId, int handIndex);

    public void handleDrawnVisibleGoldCardEvent(
            PlayerToken playerToken, int drawnCardPosition, int drawnCardId, Integer replacementCardId,
            boolean deckEmptied, Integer nextCardId, int handIndex);

    /**
     * This method handles the received update of a drawn card from the starter deck
     * 
     * @param playerToken the player token which drew the card
     * @param drawnCardId the id of the drawn card
     */
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId);

    /**
     * This method handles the received update of the chosen side of a starter card
     * 
     * @param playerToken the player token which chose the side
     * @param cardSide    the chosen side
     */
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide);

    /**
     * This method handles the received update about the end of the starter card
     * drawn phase
     */
    public void handleEndedStarterCardPhaseEvent();

    /**
     * This method handles the received update of the drawn cards from the objective
     * deck
     * 
     * @param playerToken       the player token which drew the cards
     * @param firstDrawnCardId  the id of the first drawn card
     * @param secondDrawnCardId the id of the second drawn card
     */
    public void handleDrawnObjectiveCardsEvent(
            PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId);

    /**
     * This method handles the received update of the chosen objective card
     * 
     * @param playerToken  the player token which chose the card
     * @param chosenCardId the id of the chosen card
     */
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId);

    /**
     * This method handles the received update about the end of the objective card
     * drawn phase
     */
    public void handleEndedObjectiveCardPhaseEvent();

    /**
     * This method handles the received update about the drawn common objective
     * cards
     * 
     * @param firstCommonObjectiveId  the id of the first common objective card
     * @param secondCommonObjectiveId the id of the second common objective card
     */
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId);

    /**
     * This method handles the received update about a direct player to player
     * message
     * 
     * @param sender   the player which sent the message
     * @param receiver the player which receive the message
     * @param message  the content of the message
     */
    public void handleDirectMessageEvent(
            UserInfo sender, UserInfo receiver, String message);

    /**
     * This method handles the received update about a group message
     * 
     * @param sender  the player who sent the message
     * @param message the content of the message
     */
    public void handleGroupMessageEvent(UserInfo sender, String message);

    /**
     * This method handles the received update about the start of the game
     * 
     * @param player        the player which chose the token
     * @param assignedToken the token assigned to the player
     */
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken);

    /**
     * This method handles the received update about the end of the token selection
     * phase
     * 
     * @param userInfoToToken  map from users to their tokens
     * @param timeLimitReached flag that is true if the time limit is reached
     */
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached);

    // TODO: change name to the method. It is not clear what it does
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId);

    // TODO: change method name
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources);

    /**
     * This method handles the received update about a generic game error
     * 
     * @param error
     */
    public void handleGameError(String error);

    /**
     * This method handles the received update about the results of the game
     * 
     * @param gameResults this list constains the information about the game
     */
    public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults);

    /**
     * This method handles the received event about the possible coordinates where a
     * card can the placed
     * 
     * @param playerToken      the player token which can place the card
     * @param availableSlots   the list of the available slots
     * @param cardsPlayability the map of the cards playability
     */
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability);

    public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints);

    /**
     * Handles the ending of the initialization phase of the game, receiving a slim
     * game model and setting up a client's internal model.
     *
     * @param slimGameModel slimGameModel representing the initial collections
     */
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel);

    public void handlePlayerTurnEvent(PlayerToken currentPlayer);

    public void handleLastRoundEvent();
}
