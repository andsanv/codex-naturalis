package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.card.objective.ObjectiveStrategy;
import it.polimi.ingsw.model.player.PlayerBoard;

/**
 * An objective card is a card that gives points to the players when the last turn ends if a certain
 * condition is satisfied. Each player has a personal objective, only visible to him, and all the
 * players share two common objectives, visible by everyone.
 */
public class ObjectiveCard extends Card {
  /** Points awarded at the end of the game */
  private int points;

  /** Strategy used to check points */
  private ObjectiveStrategy objectiveStrategy;

  public ObjectiveCard(int id, int points, ObjectiveStrategy objectiveStrategy) {
    super(id);
    this.objectiveStrategy = objectiveStrategy;
    this.points = points;
  }

  /**
   * @param playerBoard the board of the player
   * @return the points awarded for completing this objective
   */
  public int computePoints(PlayerBoard playerBoard) {
    return points * objectiveStrategy.getCompletedOccurrences(playerBoard);
  }

  public int getPoints() {
    return points;
  }

  public ObjectiveStrategy getObjectiveStrategy() {
    return objectiveStrategy;
  }
}
