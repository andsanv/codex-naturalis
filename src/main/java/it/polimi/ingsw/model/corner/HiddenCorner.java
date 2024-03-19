package it.polimi.ingsw.model.corner;

/**
 * A class that represents a missing corner (hidden), where you can't place any card.
 */
public class HiddenCorner extends Corner {
    @Override
    public boolean canPlaceCardAbove() {
        return false;
    }
}
