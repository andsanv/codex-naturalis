package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;

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
    private Map<PlayerToken, Integer> scores;

    private int maxScore;

    /**
     * @param players currently playing
     */
    public ScoreTrack(List<PlayerToken> playerTokens) {
        this.scores = new HashMap<>();

        for (PlayerToken playerToken : playerTokens)
            this.scores.put(playerToken, 0);
    }

    /**
     * @param player player that gets the points
     * @param incrementPoints points to add to the player's current points
     */
    public void updatePlayerScore(PlayerToken playerToken, Integer incrementPoints) {
        int newScore = scores.get(playerToken) + incrementPoints;
        scores.put(playerToken, newScore);

        if(scores.get(playerToken) > maxScore)
            maxScore = newScore;
    }

    /**
     * @return True if someone reached 20 points, false otherwise.
     */
    public boolean isGameFinished() {
        return maxScore >= 20;
    }

    /**
     * @return a copy of the scores
     */
    public Map<PlayerToken, Integer> getScores() {
        return new HashMap<>(this.scores);
    }

}
