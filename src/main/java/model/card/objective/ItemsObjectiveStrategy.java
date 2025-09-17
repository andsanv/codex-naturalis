package model.card.objective;

import model.common.Elements;
import model.player.PlayerBoard;

import java.util.HashMap;
import java.util.Map;

/**
 * Strategy to calculate points for an objective card based on items on a player
 * board.
 *
 * @see ObjectiveStrategy
 */
public class ItemsObjectiveStrategy implements ObjectiveStrategy {
  /**
   * Map that defines the amount of items required to complete the objective
   */
  private final Map<Elements, Integer> requiredElements;

  /**
   * @param requiredElements the required items to draw points for the objective
   */
  public ItemsObjectiveStrategy(Map<Elements, Integer> requiredElements) {
    this.requiredElements = requiredElements;
  }

  /**
   * Method that computes how many times the objective has been completed.
   * 
   * @param playerBoard the player's board
   * @return number of times the objective has been completed
   */
  @Override
  public int getCompletedOccurrences(PlayerBoard playerBoard) {
    return playerBoard.playerElements.entrySet().stream()
        .filter(e -> requiredElements.containsKey(e.getKey()))
        .mapToInt(e -> e.getValue() / requiredElements.get(e.getKey()))
        .min()
        .orElse(0);
  }

  /**
   * Getter for the required elements for completing the objective.
   * 
   * @return a copy of the Map containing the required elements
   */
  public Map<Elements, Integer> getRequiredElements() {
    return new HashMap<>(requiredElements);
  }
}
