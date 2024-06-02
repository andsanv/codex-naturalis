package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.Card;
import java.io.IOException;

public interface DeckCreator {

  public static Deck<? extends Card> createDeck() throws IOException {
    return null;
  }
}
