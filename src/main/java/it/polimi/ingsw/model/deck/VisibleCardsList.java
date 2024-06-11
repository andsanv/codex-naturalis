package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represents a list of visible cards on the game table.
 * Every VisibleCardsList will have maximum 2 cards.
 *
 * @param <CardType> type of the visible cards' list
 * @see Card
 */
public class VisibleCardsList<CardType> extends Observable {
  /**
   * The actual list of cards.
   */
  public final List<CardType> cards;

  /**
   * @param cards list from which this is created.
   */
  public VisibleCardsList(List<CardType> cards) {
    this.cards = new ArrayList<>(cards);
  }

  /**
   * Reimplementation of List::add method (index).
   *
   * @param card card to be appended to the list
   * @return true if add was successful, false otherwise
   */
  public boolean add(CardType card) {
    if (cards.size() < 2) return cards.add(card);

    return false;
  }

  /**
   * Reimplementation of List::add method (element and index).
   *
   * @param index index at which the specified element is to be inserted
   * @param card card to be inserted
   */
  public void add(int index, CardType card) {
    cards.add(index, card);
  }

  /**
   * Reimplementation of List::get method.
   *
   * @param index index of the element to return
   * @return the element at the specified position in this list
   */
  public CardType get(int index) {
    return cards.get(index);
  }

  /**
   * Reimplementation of List::remove method.
   *
   * @param index the index of the element to be removed
   * @return the element previously at the specified position
   */
  public CardType remove(int index) {
    return cards.remove(index);
  }
}
