package it.polimi.ingsw.model.corner;

/**
 * This enum defines the position of a corner in a card.
 *
 * @see Corner
 */
public enum CornerPosition {
  TOP_LEFT,
  TOP_RIGHT,
  BOTTOM_RIGHT,
  BOTTOM_LEFT;

  /**
   * Allows to draw opposite position.
   *
   * @return the opposite position
   */
  public CornerPosition getOpposite() {
      return switch (this) {
          case TOP_LEFT -> BOTTOM_RIGHT;
          case TOP_RIGHT -> BOTTOM_LEFT;
          case BOTTOM_RIGHT -> TOP_LEFT;
          case BOTTOM_LEFT -> TOP_RIGHT;
      };

  }
}
