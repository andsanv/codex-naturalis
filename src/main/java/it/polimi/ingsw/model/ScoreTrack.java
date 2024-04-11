package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents the scoreboard.
 *
 * @see Player
 */
public class ScoreTrack {

    /**
     * Map that holds players' scores.
     */
    private Map<Player, Integer> scores;

    private int maxScore;

    /**
     * @param players currently playing
     */
    public ScoreTrack(List<Player> players) {
        this.scores = new HashMap<>();

        for (Player player : players)
            this.scores.put(player, 0);
    }

    /**
     * @param player player that gets the points
     * @param incrementPoints points to add to the player's current points
     */
    public void updatePlayerScore(Player player, Integer incrementPoints) {
        int newScore = scores.get(player) + incrementPoints;
        scores.put(player, newScore);

        if(scores.get(player) > maxScore)
            maxScore = newScore;
    }

    /**
     * @return True if someone reached 20 points, false otherwise.
     */
    public boolean gameEnded() {
        return maxScore >= 20;
    }

    /**
     * @return a copy of the scores
     */
    public Map<Player, Integer> getScores() {
        return new HashMap<>(this.scores);
    }

}
