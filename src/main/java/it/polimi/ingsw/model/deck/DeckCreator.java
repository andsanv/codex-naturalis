package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.Card;
import java.io.IOException;

/**
 * The interface allows to quickly load all decks from json files.
 *
 * @see Deck
 */
public interface DeckCreator {
  /**
   * Creates and returns a deck.
   *
   * @return a Deck
   * @throws IOException if an I/O error occurs
   */
  static Deck<? extends Card> createDeck() {
    return null;
  }
}
