package it.polimi.ingsw.model;

import java.util.Optional;
import java.util.Stack;

public abstract class Deck {
    private Stack<Card> deck;

    public Optional<Card> draw() {
        if(!deck.empty()) {
            return Optional.empty();
        } else {
            return Optional.of(deck.pop());
        }
    }
}
