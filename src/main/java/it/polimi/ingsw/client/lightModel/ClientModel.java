package it.polimi.ingsw.client.lightModel;

import java.util.List;
import java.util.Map;

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

final class Pair<T, U> {
    public final T first;
    public final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}