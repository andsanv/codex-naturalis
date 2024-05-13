package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a card has been drawn from the gold cards' deck.
 */
public final class DrawnGoldDeckCardEvent extends GameEvent {
    private final UserInfo player;
    private final int drawnCardId;

    /**
     * @param player      the player who draws the card from the gold deck
     * @param drawnCardId the drawn card id
     */
    public DrawnGoldDeckCardEvent(UserInfo player, int drawnCardId) {
        this.player = player;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnGoldDeckCardEvent(player, drawnCardId);
    }
}
