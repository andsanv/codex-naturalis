package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.corner.CornerItems;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the area of the board personal to a single player.
 * @see Player
 */
public class PlayerBoard {
    /**
     * This attribute represents the single starter card of the player's board.
     */
    private final StarterCard starterCard;
    /**
     * This map represents the number of items available on the board.
     */
    private Map<CornerItems, Integer> visibleItems;

    /**
     * TODO: add description
     * @param starterCard starter card of the player's board
     */
    PlayerBoard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.visibleItems = new HashMap<>();
    }
}
