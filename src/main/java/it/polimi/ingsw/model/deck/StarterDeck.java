package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.StarterCard;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class StarterDeck extends Deck<StarterCard> {

    StarterDeck(List<StarterCard> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
        Collections.shuffle(deck);
    }
}
