package it.polimi.ingsw.model.corner;

/**
 * TODO
 */
public enum CornerTypes {
    /**
     * A visible corner is a corner of a card that is either empty or contains an item, but it must not be hidden (see rulebook).
     */
    VISIBLE,
    /**
     *  A covered corner is a corner of a card that has been covered by another card, thus it is not visible.
     */
    COVERED,
    /**
     * A hidden corner is a missing corner, where you can't place any card.
     */
    HIDDEN
}
