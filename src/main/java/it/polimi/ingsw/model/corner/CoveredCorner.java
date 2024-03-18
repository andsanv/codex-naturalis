package it.polimi.ingsw.model.corner;

import it.polimi.ingsw.model.card.PlayableCard;

// TODO do we really need the covering card and getter? (we use a grid now)
/**
 * A covered corner is a corner of a card that has been covered by another card, thus it is not visible.
 */
public final class CoveredCorner extends Corner {
    /**
     * The covering card is the card that has been placed on the corner.
     */
    private PlayableCard coveringCard;
    public PlayableCard getCoveringCard() {
        return coveringCard;
    }
}
