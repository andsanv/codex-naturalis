package it.polimi.ingsw.model.corner;

/**
 * A visible corner is a corner of a card that is either empty or contains an item, but it must not be hidden (see rulebook).
 */
public class VisibleCorner extends Corner {
    /**
     * An item is the (object?) contained in a corner
     */
    private CornerItems item;
}
