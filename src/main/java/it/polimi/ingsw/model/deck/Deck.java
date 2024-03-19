package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.Card;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * TODO: add javadoc, after discussion (see below)
 */
// TODO discuss implementation of a deck factory/abstract factory
public class Deck<T extends Card> {
    protected final Stack<T> deck;

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
}
