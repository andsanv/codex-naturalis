package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.player.PlayerBoard;


/**
 * TODO javadoc
 */
public class PatternObjectiveStrategy implements ObjectiveStrategy {
    CornerItems[][] pattern;

    public PatternObjectiveStrategy(CornerItems[][] pattern) {
        this.pattern = pattern;
    }

    @Override
    public int getCompletedOccurrences(PlayerBoard playerBoard) {
        return 0;
    }
}
