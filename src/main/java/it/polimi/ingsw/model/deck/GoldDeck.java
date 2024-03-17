package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.GoldCard;


import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GoldDeck extends Deck<GoldCard>  {

    GoldDeck(List<GoldCard> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
        Collections.shuffle(deck);
    }

}
