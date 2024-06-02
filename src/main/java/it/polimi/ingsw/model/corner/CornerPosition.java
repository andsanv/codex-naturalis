package it.polimi.ingsw.model.corner;

/**
 * /** This enum defines the position of a corner in a card.
 *
 * @see Corner
 */
public enum CornerPosition {
  TOP_LEFT,
  TOP_RIGHT,
  BOTTOM_RIGHT,
  BOTTOM_LEFT;

  public CornerPosition getOpposite() {
    switch (this) {
      case TOP_LEFT:
        return BOTTOM_RIGHT;
      case TOP_RIGHT:
        return BOTTOM_LEFT;
      case BOTTOM_RIGHT:
        return TOP_LEFT;
      case BOTTOM_LEFT:
        return TOP_RIGHT;
      default:
        return TOP_LEFT;
    }
  }
}
