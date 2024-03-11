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

        // TODO count of resources on starter card and following resources count update
    }
}

// TODO differentiate between starter card and resource cards. Following is starter card
class CardSlot {
    final Card card;
    List<Optional<CardSlot>> adjacentSlots;

    CardSlot(Card card) {
        this.card = card;
        this.adjacentSlots = new ArrayList<>();
    }
}