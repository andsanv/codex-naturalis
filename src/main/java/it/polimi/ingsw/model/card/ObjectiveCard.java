package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.card.objective.ObjectiveStrategy;
import it.polimi.ingsw.model.player.PlayerBoard;

/**
 * An objective card is a card that gives points to the players when the last turn ends if a certain condition is satisfied.
 * Each player has a personal objective, private, and all players share two common objectives, public.
 *
 * @see Card
 * @see ObjectiveStrategy
 */
public class ObjectiveCard extends Card {
  /**
   * Points awarded at the end of the game.
   */
  private final int points;

  /**
   * Strategy used to check points once game is finished.
   */
  private final ObjectiveStrategy objectiveStrategy;

  /**
   * @param id unique card id
   * @param points amount of points given for completing the objective
   * @param objectiveStrategy type of objective pattern to satisfy to gain points
   */
  public ObjectiveCard(int id, int points, ObjectiveStrategy objectiveStrategy) {
    super(id);
    this.objectiveStrategy = objectiveStrategy;
    this.points = points;
  }

  /**
   * @param playerBoard the board of the player
   * @return the points awarded for completing the objective
   */
  public int computePoints(PlayerBoard playerBoard) {
    return points * objectiveStrategy.getCompletedOccurrences(playerBoard);
  }

  /**
   * @return points given by completing the objective once
   */
  public int getPoints() {
    return points;
  }

  /**
   * @return type of objective pattern to satisfy to gain points
   */
  public ObjectiveStrategy getObjectiveStrategy() {
    return objectiveStrategy;
  }
}
