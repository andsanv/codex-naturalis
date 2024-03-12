package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class PlayerBoard {
    final CardSlot starterCardSlot;
    Map<CardItems, Integer> visibleItems;

    PlayerBoard(CardSlot starterCardSlot) {
        this.starterCardSlot = starterCardSlot;
        this.visibleItems = null;
    }
}

class CardSlot {
    final Card card;
    List<Optional<CardSlot>> adjacentSlots;

    CardSlot(Card card) {
        this.card = card;
        this.adjacentSlots = new ArrayList<>();
    }
}