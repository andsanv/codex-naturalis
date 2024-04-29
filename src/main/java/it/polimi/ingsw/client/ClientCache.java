package it.polimi.ingsw.client;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

public class ClientCache {
    private ClientModel currentGameModel;
    private List<Pair<String, String>> lobbyChat;
    private Map<String, List<Pair<String, String>>> directMessages;

    public void addDirectMessage(String sender, String content) {
        // TODO
    }

    public void updateScoreTrack(PlayerToken token, int score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateScoreTrack'");
    }
}
