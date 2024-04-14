package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.PlayerBoard;

import java.util.HashMap;
import java.util.Map;

/**
 * Strategy to calculate points for an objective card based on items on a player board.
 */
public class ItemsObjectiveStrategy implements ObjectiveStrategy {
    /**
     * Map that defines amount of items required to complete the objective.
     */
    Map<Elements, Integer> requiredItems;

    /**
     * Constructor that requires the following parameters.
     * @param requiredItems map for amount of required items.
     */
    public ItemsObjectiveStrategy(Map<Elements, Integer> requiredItems) {
        this.requiredItems = requiredItems;
    }

    /**
     * @param playerBoard the player's board.
     * @return number of times the objective has been completed.
     */
    @Override
    public int getCompletedOccurrences(PlayerBoard playerBoard) {
        return playerBoard.getPlayerItems().entrySet().stream()
            .filter(e -> requiredItems.containsKey(e.getKey()))
            .mapToInt(e -> e.getValue() / requiredItems.get(e.getKey()))
            .min()
            .orElse(0);
    }

    public Map<Elements, Integer> getRequiredItems() {
        return new HashMap<>(requiredItems);
    }
}
