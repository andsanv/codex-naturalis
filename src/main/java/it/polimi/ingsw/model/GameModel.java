package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public class GameModel {
    public final Map<PlayerToken, Player> tokenToPlayer;

    private Deck resourceCardsDeck;
    private List<ResourceCards> visibleResourceCards;

    private Deck goldCardsDeck;
    private List<ResourceCards> visibleGoldCards;

    private Deck objectiveCardsDeck;
    private Deck starterCardsDeck;

    private GameStateMachine stateMachine;

    private final List<PlayerToken> playersOrder;

    GameModel() {
        // TODO
        tokenToPlayer = null;
        playersOrder = null;
    }
}
