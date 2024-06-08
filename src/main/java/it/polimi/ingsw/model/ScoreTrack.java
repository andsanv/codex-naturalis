package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.distributed.events.game.UpdatedScoreTrackEvent;
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
public class ScoreTrack extends Observable {
  /** Map that holds players' scores. */
  private Map<PlayerToken, Integer> scores;

  private int maxScore;

  /**
   * @param playerTokens list of tokens currently playing
   */
  public ScoreTrack(List<PlayerToken> playerTokens) {
    this.scores = new HashMap<>();

    for (PlayerToken playerToken : playerTokens) this.scores.put(playerToken, 0);
  }

  /**
   * @param playerToken token of the player that gets the points
   * @param incrementPoints points to add to the player's current points
   */
  public int updatePlayerScore(PlayerToken playerToken, Integer incrementPoints) {
    int newScore = scores.get(playerToken) + incrementPoints;
    scores.put(playerToken, newScore);

    if (scores.get(playerToken) > maxScore) maxScore = newScore;

    notify(new UpdatedScoreTrackEvent(playerToken, newScore));
    return newScore;
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
