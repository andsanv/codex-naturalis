package model.player;

import model.card.PlayableCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * This class represents the three cards in a player's hand.
 *
 * @see Player
 * @see PlayableCard
 */
public class PlayerHand {
  /**
   * Fixed dimension of a player's hand.
   */
  public static final int HAND_SIZE = 3;

  /**
   * The array that holds the cards in the player's hand.
   */
  private final PlayableCard[] cards;

  /**
   * Initializes the player, and fills it with the PlayableCard list given as parameter.
   *
   * @param initialCards initial cards in a player's hand
   */
  public PlayerHand(List<PlayableCard> initialCards) {
    if (initialCards.size() != HAND_SIZE)
      throw new IllegalArgumentException("The size of player hand is incorrect");

    cards = new PlayableCard[HAND_SIZE];

    for(int i = 0; i < HAND_SIZE; i++)
      cards[i] = initialCards.get(i);
  }

  /**
   * Adds a card to the hand of the player, in the first available position.
   * 
   * @param card card to add to the hand
   * @return index in the array where the card was placed, '-1' if the card could not be placed.
   */
  public int add(PlayableCard card) {
    if(size() == HAND_SIZE) return -1;

    for(int i = 0; i < cards.length; i++)
      if (cards[i] == null) {
        cards[i] = card;
        return i;
      }

    return -1;
  }

  /**
   * Removes a card from the hand of the player.
   *
   * @param card card to remove
   * @return true if the card was previously in the hand, false otherwise
   */
  public boolean remove(PlayableCard card) {
    for(int i = 0; i < cards.length; i++)
      if(cards[i] == card) {
        cards[i] = null;
        return true;
      }

    return false;
  }

  /**
   * cards' getter.
   * Private final and getter with copy (instead of public) to make the list constant.
   *
   * @return list of cards
   */
  public List<PlayableCard> getCards() {
    return new ArrayList<>(Arrays.asList(cards));
  }

  /**
   * Used to get a card in the player's hand from its id.
   *
   * @param id id of the card of interest
   * @return the card if present, null otherwise
   */
  public PlayableCard get(int id) {
    return Arrays.stream(cards).filter(Objects::nonNull).filter(x -> x.id == id).findFirst().orElse(null);
  }

  /**
   * @return number of cards in the hand
   */
  public long size() {
    return Arrays.stream(cards).filter(Objects::nonNull).count();
  }

  /**
   * @return the first index where there is no card in the player's hand, '-1' if hand is full
   */
  public int getFirstFree() {
    return IntStream.range(0, HAND_SIZE).filter(x -> cards[x] == null).findFirst().orElse(-1);
  }
}
