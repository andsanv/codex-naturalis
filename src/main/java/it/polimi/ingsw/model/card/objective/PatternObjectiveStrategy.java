package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.player.PlayerBoard;


/**
 * Strategy to calculate points for an objective card based on patterns on a player board.
 */
public class PatternObjectiveStrategy implements ObjectiveStrategy {
    /**
     * Matrix (TODO prolly 3x3) representing the required pattern for objective completion.
     */
    CornerItems[][] pattern;

    /**
     * Constructor for object strategy.
     * @param pattern to be satisfied for object completion.
     */
    public PatternObjectiveStrategy(CornerItems[][] pattern) {
        //TODO add algorithm
        this.pattern = pattern;
    }

    /**
     * @param playerBoard the player's board
     * @return number of times the objective has been completed.
     */
    @Override
    public int getCompletedOccurrences(PlayerBoard playerBoard) {
        return 0;
    }
}
