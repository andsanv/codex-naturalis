package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal that a card has been drawn from visible resource cards.
 */
public final class DrawnVisibleResourceCardEvent extends GameEvent {
    private final PlayerToken playerToken;
    private final int drawnCardPosition;
    private final int drawnCardId;

    /**
     * @param playerToken       the token of the player that draws the card
     * @param drawnCardPosition the drawn card position in the visibile resource
     *                          cards
     * @param drawnCardId       the id of the drawn card
     */
    public DrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId) {
        this.playerToken = playerToken;
        this.drawnCardPosition = drawnCardPosition;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnVisibleResourceCardEvent(playerToken, drawnCardPosition, drawnCardId);
    }
}
