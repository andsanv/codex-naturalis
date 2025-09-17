package model;

import controller.observer.Observable;
import controller.observer.Observer;
import distributed.events.game.UpdatedScoreTrackEvent;
import model.player.Player;
import model.player.PlayerToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that represents the scoreboard.
 *
 * @see Player
 * @see GameModel
 */
public class ScoreTrack extends Observable {
  /**
   * Map that holds players' scores.
   */
  private final Map<PlayerToken, Integer> scores;

  /**
   * When a player reaches limitScore, last turn of the game starts
   */
  private final int limitScore = 1;

  /**
   * @param scores initial scores of the players (all zero)
   * @param observers list of observers
   * @param lastEventId integer used to uniquely identify events
   */
  public ScoreTrack(Map<PlayerToken, Integer> scores, List<Observer> observers, AtomicInteger lastEventId) {
    super(observers, lastEventId);
    this.scores = scores;
  }

  /**
   * Allows to update a player's score.
   *
   * @param playerToken token of the player that gets the points
   * @param incrementPoints points to insert to the player's current points
   */
  public void updatePlayerScore(PlayerToken playerToken, Integer incrementPoints) {
    int newScore = scores.get(playerToken) + incrementPoints;
    scores.put(playerToken, newScore);

    notify(new UpdatedScoreTrackEvent(playerToken, newScore));
  }

  /**
   * @return true if someone reached limitScore, false otherwise.
   */
  public boolean limitPointsReached() {
    return scores.values().stream().anyMatch(x -> x >= limitScore);
  }

  /**
   * @return a copy of the scores
   */
  public Map<PlayerToken, Integer> getScores() {
    return new HashMap<>(this.scores);
  }
}
