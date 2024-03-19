package it.polimi.ingsw.model.corner;

/**
 * A covered corner is a corner of a card that has been covered by another card, thus it is not visible.
 */
public final class CoveredCorner extends Corner {
    @Override
    public boolean canPlaceCardAbove() {
        return false;
    }
}
