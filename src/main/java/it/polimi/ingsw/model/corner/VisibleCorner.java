package it.polimi.ingsw.model.corner;

/**
 * A visible corner is a corner of a card that is either empty or contains an item, but it must not be hidden (see rulebook).
 */
public class VisibleCorner extends Corner {
    /**
     * This attribute holds the item (resource, object or possibly no item) contained in the corner.
     */
    private CornerItems item;
}
