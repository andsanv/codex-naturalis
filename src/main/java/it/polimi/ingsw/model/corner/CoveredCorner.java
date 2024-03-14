package it.polimi.ingsw.model.corner;

import it.polimi.ingsw.model.card.GoldCardPoints;
import it.polimi.ingsw.model.card.PlayableCard;

/**
 * A covered corner is a corner of a card that has been covered by another card (corner?), thus it is not visible.
 */
public final class CoveredCorner extends Corner {
    /**
     * A covering card is the playable card placed on the corner.
     */
    private PlayableCard coveringCard;
}
