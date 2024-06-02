package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.Card;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/** TODO: add javadoc, after discussion (see below) */
// TODO discuss implementation of a deck factory/abstract factory
public class Deck<T extends Card> extends Observable {
  private final Stack<T> deck;

  public Optional<T> draw() {
    return deck.empty() ? Optional.empty() : Optional.of(deck.pop());
  }

  public boolean isEmpty() {
    return deck.empty();
  }

  Deck(List<T> cards) {
    deck = new Stack<>();
    Collections.shuffle(cards);
    deck.addAll(cards);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    deck.stream()
        .forEach(
            e -> {
              stringBuilder.append(e.toString() + "\n");
            });
    return stringBuilder.toString();
  }

  public int size() {
    return deck.size();
  }
}
