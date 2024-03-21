package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.player.PlayerBoard;

import java.util.Map;

/**
 * Calculate points based on items on a player board
 * TODO javadoc
 */
public class ItemsObjectiveStrategy implements ObjectiveStrategy {
    Map<CornerItems, Integer> requiredItems;

    public ItemsObjectiveStrategy(Map<CornerItems, Integer> requiredItems) {
        this.requiredItems = requiredItems;
    }

    @Override
    public int getCompletedOccurrences(PlayerBoard playerBoard) {
        //TODO implement items occurrences calculation
        return 0;
    }
}
