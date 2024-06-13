package it.polimi.ingsw.model.card;

/**
 * Abstract class that represents a generic card.
 * All cards have a unique id.
 */
public abstract class Card {
  /** Card's unique id */
  public final int id;

  /**
   * @param id Unique id of the card
   */
  public Card(int id) {
    this.id = id;
  }

  /**
   * Override of Object::equals method for Card class.
   *
   * @param other card to which this is compared
   * @return true if the cards are the same, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other == null || other.getClass() != this.getClass()) return false;

    return id == ((Card) other).id;
  }
}
