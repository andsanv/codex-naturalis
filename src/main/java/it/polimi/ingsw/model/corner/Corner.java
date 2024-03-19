package it.polimi.ingsw.model.corner;

/**
 * Abstract class that represents a generic corner of a card.
 */
public abstract class Corner {
    /**
     * @return True if a card can be placed on the corner, false otherwise
     */
    public abstract boolean canPlaceCardAbove();
}

