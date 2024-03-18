package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.gameflowmanager.manager.GameFlowManager;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.model.scoreTracker.ScoreTracker;

import java.util.List;
import java.util.Map;

/**
 * TODO: add javadoc
 */
public class GameModel {
    public final Map<PlayerToken, Player> tokenToPlayer;

    private Deck<ResourceCard> resourceCardsDeck;
    private List<ResourceCard> visibleResourceCards;

    private Deck<GoldCard> goldCardsDeck;
    private List<ResourceCard> visibleGoldCards;

    private Deck<ObjectiveCard> objectiveCardsDeck;
    private Deck<StarterCard> starterCardsDeck;

    private ScoreTracker scoreTracker;

    private GameFlowManager stateMachine;

    private final List<PlayerToken> playersOrder;

    GameModel() {
        // TODO
        tokenToPlayer = null;
        playersOrder = null;
    }

    /**
     * This function must be called during the setup phase of the game
     * @param playersCount number of players in the match
     * @return A list of starter cards of length players count
     */
    public List<StarterCard> getStarterCards(int playersCount) {
        // TODO this function must be called only once
        return null;
    }

    /**
     * This function must be called after players decide how to play their starter card
     * @param tokenToStarterCard
     * @param cardsSidesPlayed
     */
    public void setPlayersMap(Map<PlayerToken, StarterCard> tokenToStarterCard, Map<StarterCard, CardSide> cardsSidesPlayed) {
        // TODO this function must be called only once, after calling method getStarterCards()

        // TODO create here players and tokenToPlayer map

        // TODO randomly choose players' order
    }
}
