package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.ObjectiveCard;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ObjectiveDeck extends Deck<ObjectiveCard> {

    ObjectiveDeck(List<ObjectiveCard> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
        Collections.shuffle(deck);
    }
}
