package model.card.objective;

import model.player.PlayerBoard;

/**
 * Strategy pattern interface for calculating how many times an objective has been completed.
 *
 * @see ItemsObjectiveStrategy
 * @see PatternObjectiveStrategy
 */
public interface ObjectiveStrategy {
  /**
   * Method that computes how many times the objective has been completed.
   * 
   * @param playerBoard the player's board
   * @return number of times the objective has been completed
   */
  int getCompletedOccurrences(PlayerBoard playerBoard);
}
