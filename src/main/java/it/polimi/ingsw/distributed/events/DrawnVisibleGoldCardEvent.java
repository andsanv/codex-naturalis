package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a card has been drawn from visible gold cards.
 */
public final class DrawnVisibleGoldCardEvent extends GameEvent {
    private final UserInfo player;
    private final int drawnCardPosition;
    private final int drawnCardId;

    /**
     * @param player            the player who draws the card
     * @param drawnCardPosition the drawn card position in the visibile resource
     *                          cards
     * @param drawnCardId       the id of the drawn card
     */
    public DrawnVisibleGoldCardEvent(UserInfo player, int drawnCardPosition, int drawnCardId) {
        this.player = player;
        this.drawnCardPosition = drawnCardPosition;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnVisibleResourceCardEvent(player, drawnCardPosition, drawnCardId);
    }
}
