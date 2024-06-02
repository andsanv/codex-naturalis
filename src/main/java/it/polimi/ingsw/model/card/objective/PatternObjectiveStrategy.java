package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerBoard;
import java.util.*;
import java.util.stream.Collectors;

/** Strategy to calculate points for an objective card based on patterns on a player board. */
public class PatternObjectiveStrategy implements ObjectiveStrategy {
  /**
   * Map representing a matrix of the required pattern for objective completion. The cell in the
   * bottom-left of the smallest matrix containing the pattern must have (0,0) coordinates.
   */
  private Map<Coords, Resources> pattern;

  /**
   * @param patternMap the pattern to be matched
   */
  public PatternObjectiveStrategy(Map<Coords, Resources> patternMap) {
    this.pattern = patternMap;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  /**
   * @param playerBoard the player's board
   * @return number of times the objective has been completed.
   */
  @Override
  public int getCompletedOccurrences(PlayerBoard playerBoard) {
    Map<Coords, Resources> board =
        playerBoard.getBoard().entrySet().stream()
            .filter(e -> !e.getKey().equals(new Coords(0, 0)))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e ->
                        e.getValue()
                            .getType()
                            .get() // isPresent() isn't checked since the starter card has been
                    // filtered out.
                    ));

    // Early return if only the starter card has been placed
    if (board.keySet().size() == 0) return 0;

    int x_max = Integer.MIN_VALUE;
    int x_min = Integer.MAX_VALUE;
    int y_max = Integer.MIN_VALUE;
    int y_min = Integer.MAX_VALUE;

    for (Coords c : board.keySet()) {
      System.out.println(c);
      if (c.x > x_max) x_max = c.x;
      if (c.x < x_min) x_min = c.x;
      if (c.y > y_max) y_max = c.y;
      if (c.y < y_min) y_min = c.y;
    }

    // i_min and j_min are (0,0) (check pattern attribute description)
    int i_max = Integer.MIN_VALUE;
    int j_max = Integer.MIN_VALUE;

    for (Coords c : pattern.keySet()) {
      if (c.x > i_max) i_max = c.x;
      if (c.y > j_max) j_max = c.y;
    }

    int pattern_count = 0;

    for (int x = x_min; x <= x_max - i_max; x++) {
      for (int y = y_min; y <= y_max - j_max; y++) {
        boolean pattern_found = true;
        List<Coords> used_coordinates = new ArrayList<>();

        for (int i = 0; i <= i_max; i++) {
          for (int j = 0; j <= j_max; j++) {
            Resources pattern_resource = pattern.get(new Coords(i, j));

            Coords current_coords = new Coords(x + i, y + j);

            if (pattern_resource != null && pattern_resource != board.get(current_coords))
              pattern_found = false;
            else used_coordinates.add(current_coords);
          }
        }

        if (pattern_found) {
          pattern_count++;

          for (Coords c : used_coordinates) board.remove(c);
        }
      }
    }

    return pattern_count;
  }

  public Map<Coords, Resources> getPattern() {
    return new HashMap<>(pattern);
  }
}
