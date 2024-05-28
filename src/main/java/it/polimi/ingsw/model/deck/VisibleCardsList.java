package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.ResourceCard;

import java.util.List;
import java.util.ArrayList;

public class VisibleCardsList<Card> extends Observable {
    public final List<Card> cards;

    public VisibleCardsList(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public boolean add(Card card) {
        if(cards.size() < 2)
            return cards.add(card);

        return false;
    }

    public Card get(int index) {
        return cards.get(index);
    }

    public Card remove(int index) {
        return cards.remove(index);
    }

    public void add(int index, Card card) {
        cards.add(index, card);
    }
}
