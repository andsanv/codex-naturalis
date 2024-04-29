package it.polimi.ingsw.client;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * ClientModel
 */
public class ClientModel {
    private Map<PlayerToken, String> tokens;
    private Map<String, Integer> scoreTrack;
    private List<ClientCard> playerHand;
    private Map<String, List<Pair<CardType, Resources>>> otherHands;
    private Map<String, Map<Coords, ClientCard>> boards;

    

    public void updateScoreTrack(PlayerToken token, int score) {
        scoreTrack.put(tokens.get(token), score);
    }
}

enum CardType {
    RESOURCE,
    GOLD
}