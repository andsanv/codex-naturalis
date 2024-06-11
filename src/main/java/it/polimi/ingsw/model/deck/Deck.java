package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.Card;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * A deck is a collection of cards, implemented as a stack of generic cards.
 * All cards in a Deck must be of the same type.
 *
 * @param <CardType>
 */
public class Deck<CardType extends Card> extends Observable {
  /**
   * The actual stack.
   */
  private final Stack<CardType> deck;

  /**
   * @param cards list of cards that will be added to the deck
   */
  public Deck(List<CardType> cards) {
    deck = new Stack<>();

    Collections.shuffle(cards);
    deck.addAll(cards);
  }

  public Optional<CardType> draw() {
    return deck.empty() ? Optional.empty() : Optional.of(deck.pop());
  }

  /**
   * Override of Object::toString method for Deck class.
   *
   * @return a string that represents the deck
   */
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    deck.forEach(e -> stringBuilder.append(e.toString()).append("\n"));
    return stringBuilder.toString();
  }

  /**
   * Reimplementation of Stack::empty method.
   *
   * @return true if deck is empty, false otherwise
   */
  public boolean isEmpty() {
    return deck.empty();
  }

  /**
   * Reimplementation of Stack::size method.
   *
   * @return the size of the deck
   */
  public int size() {
    return deck.size();
  }
}
