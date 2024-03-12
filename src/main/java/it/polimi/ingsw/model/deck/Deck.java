package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.Card;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

// TODO discuss implementation of a deck factory/abstract factory
public abstract class Deck {
    private Stack<Card> deck;

    public Optional<Card> draw() {
        return deck.empty() ? Optional.empty() : Optional.of(deck.pop());
    }

    Deck(List<Card> cards) {
        deck = new Stack<>();
        Collections.shuffle(cards);
        deck.addAll(cards);
    }
}
