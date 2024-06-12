package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.DrawnVisibleGoldCardEvent;
import it.polimi.ingsw.distributed.events.game.DrawnVisibleResourceCardEvent;
import it.polimi.ingsw.model.card.Card;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

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
  public final List<CardType> cards;

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
   * @param index the index of the element to be removed
   * @return Optional.empty() if index out of bound or no card was in specified position, Optional.of(drawn card) otherwise
   */
  public Optional<CardType> draw(PlayerToken playerToken, int index) {
    if(index < 0 || index > 1 || cards.get(index) == null) return Optional.empty();

    CardType card = cards.get(index);
    Trio<Optional<CardType>, Boolean, Optional<Resources>> deckDrawResult = deck.anonymousDraw();

    if (card instanceof ResourceCard) {
      notify(new DrawnVisibleResourceCardEvent(
              playerToken,
              index,
              card.id,
              deckDrawResult.first.isEmpty() ? Optional.empty() : Optional.of(deckDrawResult.first.get().id),
              deckDrawResult.second, deckDrawResult.third));
    }
    else if (card instanceof GoldCard) {
      notify(new DrawnVisibleGoldCardEvent(
              playerToken,
              index,
              card.id,
              deckDrawResult.first.isEmpty() ? Optional.empty() : Optional.of(deckDrawResult.first.get().id),
              deckDrawResult.second, deckDrawResult.third));    }
    else throw new RuntimeException();

    cards.set(index, deckDrawResult.first.orElse(null));
    return Optional.of(card);
  }
}
