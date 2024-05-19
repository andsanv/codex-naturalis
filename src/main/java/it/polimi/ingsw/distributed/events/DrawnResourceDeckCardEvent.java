package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal that a card has been drawn from the resource cards' deck.
 */
public final class DrawnResourceDeckCardEvent extends GameEvent {
    private final PlayerToken playerToken;
    private final int drawnCardId;

    /**
     * @param playerToken   the token of the player who draws the card from the resource deck
     * @param drawnCardId   the drawn card id
     */
    public DrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        this.playerToken = playerToken;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnResourceDeckCardEvent(playerToken, drawnCardId);
    }
}
