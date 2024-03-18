package it.polimi.ingsw.model.scoreTracker;

import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the scoreboard
 * @see Player
 */
public class ScoreTracker {

    /**
     * This map TODO
     */
    private Map<Player, Integer> score;

    /**
     * TODO: add description
     * @param players currently playing
     */
    public ScoreTracker(List<Player> players) {
        this.score = new HashMap<>();

        for(Player player: players)
            this.score.put(player, 0);
    }


    public void move(Player player, Integer points) {
        score.put(player, score.get(player) + points);
    }


    //getter
    public Map<Player, Integer> getScore() {
        return new HashMap<>(this.score);
    }

}
