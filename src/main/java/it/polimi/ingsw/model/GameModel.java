package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.deck.Deck;
import it.polimi.ingsw.model.gameflowmanager.manager.GameFlowManager;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.List;
import java.util.Map;

/**
 * TODO: add javadoc
 */
public class GameModel {
    public final Map<PlayerToken, Player> tokenToPlayer;

    private Deck resourceCardsDeck;
    private List<ResourceCard> visibleResourceCards;

    private Deck goldCardsDeck;
    private List<ResourceCard> visibleGoldCards;

    private Deck objectiveCardsDeck;
    private Deck starterCardsDeck;

    private GameFlowManager stateMachine;

    private final List<PlayerToken> playersOrder;

    GameModel() {
        // TODO
        tokenToPlayer = null;
        playersOrder = null;
    }
}
