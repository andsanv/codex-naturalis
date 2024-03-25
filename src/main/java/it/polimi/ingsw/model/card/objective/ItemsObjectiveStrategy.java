package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.player.PlayerBoard;

import java.util.Map;

/**
 * Strategy to calculate points for an objective card based on items on a player board.
 */
public class ItemsObjectiveStrategy implements ObjectiveStrategy {
    /**
     * Map that defines amount of items required to complete the objective.
     */
    Map<CornerItems, Integer> requiredItems;

    /**
     * Constructor that requires the following parameters.
     * @param requiredItems map for amount of required items.
     */
    public ItemsObjectiveStrategy(Map<CornerItems, Integer> requiredItems) {
        this.requiredItems = requiredItems;
    }

    /**
     * @param playerBoard the player's board.
     * @return number of times the objective has been completed.
     */
    @Override
    public int getCompletedOccurrences(PlayerBoard playerBoard) {
        //TODO implement items occurrences calculation
        return 0;
    }
}
