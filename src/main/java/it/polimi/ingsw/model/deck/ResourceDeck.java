package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.Card;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ResourceCard;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ResourceDeck extends Deck<ResourceCard> {

    ResourceDeck(List<ResourceCard> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
        Collections.shuffle(deck);
    }
}
