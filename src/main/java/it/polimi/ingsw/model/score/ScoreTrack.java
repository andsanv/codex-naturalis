package it.polimi.ingsw.model.score;

import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the scoreboard
 *
 * @see Player
 */
public class ScoreTrack {

    /**
     * This map TODO
     */
    private Map<Player, Integer> scores;

    /**
     * TODO: add description
     *
     * @param players currently playing
     */
    public ScoreTrack(List<Player> players) {
        this.scores = new HashMap<>();

        for (Player player : players)
            this.scores.put(player, 0);
    }

    public void updatePlayerScore(Player player, Integer incrementPoints) {
        scores.put(player, scores.get(player) + incrementPoints);
    }

    //getter
    public Map<Player, Integer> getScore() {
        return new HashMap<>(this.scores);
    }

}
