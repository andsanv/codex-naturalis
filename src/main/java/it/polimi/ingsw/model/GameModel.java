package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.ResourceCard;
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

    private ResourceDeck resourceCardsDeck;
    private List<ResourceCard> visibleResourceCards;

    private GoldDeck goldCardsDeck;
    private List<ResourceCard> visibleGoldCards;

    private ObjectiveDeck objectiveCardsDeck;
    private StarterDeck starterCardsDeck;

    private ScoreTracker scoreTracker;

    private GameFlowManager stateMachine;

    private final List<PlayerToken> playersOrder;

    GameModel() {
        // TODO
        tokenToPlayer = null;
        playersOrder = null;
    }
}
