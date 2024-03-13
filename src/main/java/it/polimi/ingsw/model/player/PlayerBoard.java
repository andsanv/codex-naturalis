package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.corner.CornerItems;

import java.util.HashMap;
import java.util.Map;

public class PlayerBoard {
    private final StarterCard starterCard;
    private Map<CornerItems, Integer> visibleItems;

    PlayerBoard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.visibleItems = new HashMap<>();
    }
}
