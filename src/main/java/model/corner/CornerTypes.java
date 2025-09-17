package model.corner;

/**
 * The enum defines all possible corner types.
 *
 * @see Corner
 */
public enum CornerTypes {
  /**
   * A visible corner is a corner of a card that is either empty or contains an item, but it must not be hidden.
   */
  VISIBLE,
  /**
   * A covered corner is a corner of a card that has been covered by another card, thus it is not visible.
   */
  COVERED,
  /**
   * A hidden corner is a missing corner, where you can't place any card.
   */
  HIDDEN
}
