package model.deck;

import controller.observer.Observable;
import controller.observer.Observer;
import distributed.events.game.DrawnVisibleGoldCardEvent;
import distributed.events.game.DrawnVisibleResourceCardEvent;
import model.card.Card;
import model.card.GoldCard;
import model.card.ResourceCard;
import model.player.PlayerToken;
import util.Trio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class represents a list of visible cards on the game table.
 * Every VisibleCardsList will have maximum 2 cards.
 *
 * @param <CardType> type of the visible cards' list
 * @see Card
 * @see Deck
 */
public class VisibleCardsList<CardType extends Card> extends Observable {
  /**
   * The actual list of cards.
   */
  private final List<CardType> cards;

  /**
   * Deck with the same CardType as this, from which replacement cards will be drawn.
   */
  public final Deck<CardType> deck;

  /**
   * @param deck deck from which the class pops new cards.
   * @param observers list of observers
   * @param lastEventId integer used to uniquely identify events
   */
  public VisibleCardsList(Deck<CardType> deck, List<Observer> observers, AtomicInteger lastEventId) {
    super(observers, lastEventId);

    this.deck = deck;
    this.cards = new ArrayList<>(Arrays.asList(deck.anonymousDraw().first.orElseThrow(), deck.anonymousDraw().first.orElseThrow()));
  }

  /**
   * Allows players to draw from the visible cards list.
   * Replaces the drawn card with a card drawn from the deck.
   * If the deck is empty, it replaces the card with null.
   *
   * @param playerToken token of the player drawing the card
   * @param listIndex the list index of the element to be removed
   * @param handIndex index in the player's hand where card will be placed
   * @return Optional.empty() if listIndex out of bound or no card was in specified position, Optional.of(drawn card) otherwise
   */
  public Optional<CardType> draw(PlayerToken playerToken, int listIndex, int handIndex) {
    if(listIndex < 0 || listIndex > 1 || cards.get(listIndex) == null) return Optional.empty();

    CardType card = cards.get(listIndex);
    Trio<Optional<CardType>, Integer, Integer> deckDrawResult = deck.anonymousDraw();

    if (card instanceof ResourceCard) {
      notify(new DrawnVisibleResourceCardEvent(
              playerToken,
              listIndex,
              card.id,
              deckDrawResult.first.isEmpty() ? null : deckDrawResult.first.get().id,
              deckDrawResult.second,
              deckDrawResult.third,
              handIndex));
    }
    else if (card instanceof GoldCard) {
      notify(new DrawnVisibleGoldCardEvent(
              playerToken,
              listIndex,
              card.id,
              deckDrawResult.first.isEmpty() ? null : deckDrawResult.first.get().id,
              deckDrawResult.second,
              deckDrawResult.third,
              handIndex));
    }
    else throw new RuntimeException("Illegal card type");

    cards.set(listIndex, deckDrawResult.first.orElse(null));
    return Optional.of(card);
  }

  /**
   * cards' getter, allows to have cards as private.
   * 
   * @return list of cards in the list
   */
  public List<CardType> getCards() {
    return new ArrayList<>(cards);
  }

  /**
   * Allows to get the card in the list at a certain index.
   * 
   * @param index index in the list of the card of interest
   * @return the card at the index in the list
   */
  public CardType get(int index) {
    if(index < 0 || index > 1) return null;

    return cards.get(index);
  }
}
