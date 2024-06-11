package it.polimi.ingsw.model.player;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.PlayableCard;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the three cards in a player's hand.
 *
 * @see Player
 * @see PlayableCard
 */
public class PlayerHand extends Observable {
  /**
   * The list that holds the cards in the player's hand.
   */
  private final List<PlayableCard> cards;

  /**
   * Initializes the player hand as empty.
   */
  public PlayerHand() {
    this.cards = new ArrayList<>();
  }

  /**
   * Adds a card to the hand of the player.
   * 
   * @param card card to add
   */
  public void addCard(PlayableCard card) {
    cards.add(card);
  }

  /**
   * Removes a card from the hand of the player.
   *
   * @param card card to remove
   */
  public void removeCard(PlayableCard card) {
    cards.remove(card);
  }

  /**
   * cards' getter.
   * Private final and getter with copy (instead of public) to make the list constant.
   *
   * @return list of cards.
   */
  public List<PlayableCard> getCards() {
    return new ArrayList<>(cards);
  }
}
