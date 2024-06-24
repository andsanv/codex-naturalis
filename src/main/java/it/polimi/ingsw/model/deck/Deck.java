package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.DrawnGoldDeckCardEvent;
import it.polimi.ingsw.distributed.events.game.DrawnResourceDeckCardEvent;
import it.polimi.ingsw.distributed.events.game.DrawnStarterCardEvent;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A deck is a collection of cards, implemented as a stack of generic cards.
 * All cards in a Deck must be of the same type.
 *
 * @param <CardType> type of the cards inside the deck
 */
public class Deck<CardType extends Card> extends Observable {
    /**
     * The actual stack that holds the cards of the deck.
     */
    private final Stack<CardType> deck;

    /**
     * @param cards       list of cards that will be added to the deck
     * @param observers   list of observers
     * @param lastEventId integer used to uniquely identify events
     */
    public Deck(List<CardType> cards, List<Observer> observers, AtomicInteger lastEventId) {
        super(observers, lastEventId);

        deck = new Stack<>();
        Collections.shuffle(cards);
        deck.addAll(cards);
    }

    /**
     * Allows to draw a card from the top of the deck.
     *
     * @param playerToken token of the player drawing the card
     * @param handIndex   index of the hand where card will be placed
     * @return Optional.of(drawn card) if the deck was not empty, Optional.empty()
     *         otherwise
     */
    public Optional<CardType> draw(PlayerToken playerToken, int handIndex) {
        if (isEmpty())
            return Optional.empty();

        CardType card = deck.pop();

        Integer nextCardId = null;
        if (!deck.isEmpty()) {
            CardType nextCard = deck.peek();

            if (nextCard instanceof ResourceCard || nextCard instanceof GoldCard)
                nextCardId = nextCard.id;
        }

        if (card instanceof ResourceCard)
            notify(new DrawnResourceDeckCardEvent(playerToken, card.id, deck.isEmpty(), nextCardId, handIndex));
        else if (card instanceof GoldCard)
            notify(new DrawnGoldDeckCardEvent(playerToken, card.id, deck.isEmpty(), nextCardId, handIndex));
        else if (card instanceof StarterCard)
            notify(new DrawnStarterCardEvent(playerToken, card.id));

        return Optional.of(card);
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

    /**
     * Only used during setup phase of the game and when it's guaranteed decks are
     * not empty.
     * Invocation of this method will not notify observers.
     *
     * @return a trio containing: the drawn card, a boolean telling if the draw
     *         emptied the main deck, and the next card's id
     */
    public Trio<Optional<CardType>, Boolean, Integer> anonymousDraw() {
        if (isEmpty())
            return new Trio<>(Optional.empty(), true, null);

        CardType card = deck.pop();
        if (isEmpty())
            return new Trio<>(Optional.of(card), true, null);
        {
        }

        CardType nextCard = deck.peek();
        Integer nextCardId = null;
        if (nextCard instanceof ResourceCard || nextCard instanceof GoldCard) {
            nextCardId = nextCard.id;
        }

        return new Trio<>(Optional.of(card), deck.isEmpty(), nextCardId);
    }

    /**
     * Allows to retrieve the id of the card at the top of the deck.
     *
     * @return null if deck is empty, the id of the top card otherwise
     */
    public Integer getNextCardId() {
        if (isEmpty())
            return null;
        ;

        return deck.peek().id;
    }

    /**
     * Cretes a list containing the card ids.
     * The first id is the one of the card that's at the bottom of the deck and the
     * last id in the list belongs to the card at the top of the deck.
     * 
     * @return the deck as a list of ids
     */
    public List<Integer> asListOfIds() {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < deck.size(); i++)
            result.add(deck.get(i).id);

        return result;
    }
}
