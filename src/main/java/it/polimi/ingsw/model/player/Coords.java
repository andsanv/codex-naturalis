package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.StarterCard;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents 2D coordinates that can be used to identify cards' position in the player board.
 * StarterCard will be placed at coordinates (0,0)
 *
 * @see PlayerBoard
 * @see StarterCard
 */
public final class Coords implements Serializable {
  /**
   * Coordinate on the x-axis
   */
  public final int x;

  /**
   * Coordinate on the y-axis
   */
  public final int y;

  /**
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public Coords(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Override of Object::toString method for Coords class.
   *
   * @return the string representation of the object
   */
  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  /**
   * Override of Object::equals method for Coords class.
   *
   * @param other coords to which this is compared
   * @return true if the objects are the same, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other == null || other.getClass() != this.getClass()) return false;

    return ((Coords) other).x == this.x && ((Coords) other).y == this.y;
  }

  /**
   * Override of Object::hashCode method for Coords class.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
