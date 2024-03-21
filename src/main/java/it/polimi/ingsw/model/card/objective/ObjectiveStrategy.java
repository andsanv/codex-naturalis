package it.polimi.ingsw.model.card.objective;

import it.polimi.ingsw.model.player.PlayerBoard;

/**
 * Strategy pattern interface for calculating how many times an objective has been completed (occurences).
 */
public interface ObjectiveStrategy {
    /**
     * @param playerBoard the player's board
     * @return number of times the objective has been completed
     */
    int getCompletedOccurrences(PlayerBoard playerBoard);
}
