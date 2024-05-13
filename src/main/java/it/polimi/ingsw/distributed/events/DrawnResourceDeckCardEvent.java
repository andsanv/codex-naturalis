package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a card has been drawn from the resource cards' deck.
 */
public final class DrawnResourceDeckCardEvent extends GameEvent {
    private final UserInfo player;
    private final int drawnCardId;

    /**
     * @param player      the player who draws the card from the resource deck
     * @param drawnCardId the drawn card id
     */
    public DrawnResourceDeckCardEvent(UserInfo player, int drawnCardId) {
        this.player = player;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnResourceDeckCardEvent(player, drawnCardId);
    }
}
