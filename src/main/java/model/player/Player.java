package model.player;

import controller.observer.Observer;
import model.card.CardSide;
import model.card.ObjectiveCard;
import model.card.PlayableCard;
import model.card.StarterCard;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class represents one client in game.
 *
 * @see PlayerBoard
 */
public class Player {
  /**
   * Player's board.
   */
  public final PlayerBoard playerBoard;

  /**
   * Player's hand.
   */
  public final PlayerHand playerHand;

  /**
   * Player's secret objective.
   */
  public final ObjectiveCard secretObjective;

  /**
   * @param starterCard starter card chosen by the player
   * @param starterCardSide starter card side chosen by the player
   * @param secretObjective secret objective chosen by the player
   * @param initialCards initial cards randomly drawn in the player's hand
   * @param observers list of observers
   * @param lastEventId integer used to uniquely identify events
   */
  public Player(StarterCard starterCard, CardSide starterCardSide, ObjectiveCard secretObjective, List<PlayableCard> initialCards, List<Observer> observers, AtomicInteger lastEventId) {
    this.playerBoard = new PlayerBoard(starterCard, starterCardSide, observers, lastEventId);
    this.secretObjective = secretObjective;
    this.playerHand = new PlayerHand(initialCards);
  }
}
