package model.corner;

import model.common.Elements;

import java.util.Objects;
import java.util.Optional;

/**
 * This class represents a corner of a card.
 * Each card has four corners.
 */
public class Corner {
  /**
   * The element on the corner.
   * There could be no elements on a corner.
   */
  public final Optional<Elements> element;

  /**
   * Type of corner, which specifies its visibility.
   * Not final as it can change at runtime.
   */
  public CornerTypes type;

  /**
   * @param element the element contained in the corner
   * @param type the corner's type (hidden, covered or visible)
   */
  public Corner(Elements element, CornerTypes type) {
    this.element = Optional.ofNullable(element);
    this.type = type;
  }

  /**
   * @return true if a card can be placed on the corner, false otherwise
   */
  public boolean canPlaceCardAbove() {
    return type == CornerTypes.VISIBLE;
  }

  /**
   * Override of Object::equals method for Corner class.
   *
   * @param other corner to which this is compared
   * @return true if the corners are the same, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other == null || getClass() != other.getClass()) return false;

    return this == other || Objects.equals(element, ((Corner) other).element) && type == ((Corner) other).type;
  }
}
