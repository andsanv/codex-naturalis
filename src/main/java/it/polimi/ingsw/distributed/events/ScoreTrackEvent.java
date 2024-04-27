package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.client.lightModel.ClientModel;
import it.polimi.ingsw.model.player.PlayerToken;

public class ScoreTrackEvent extends Event {
    public final PlayerToken token;
    public final int score;

    public ScoreTrackEvent(PlayerToken token, int score) {
        this.token = token;
        this.score = score;
    }

    @Override
    public void update(ClientModel model) {
        model.updateScoreTrack(token, score);
    }
    
}
