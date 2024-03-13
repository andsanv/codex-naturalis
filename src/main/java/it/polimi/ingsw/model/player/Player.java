package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.ObjectiveCard;

public class Player {
    private final PlayerBoard board;
    private final PlayerHand hand;
    private final ObjectiveCard secretObjective;

    Player() {
        this.board = null;
        this.hand = null;
        this.secretObjective = null;
    }
}