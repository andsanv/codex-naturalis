package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal that a card has been drawn from the gold cards' deck.
 */
public final class DrawnGoldDeckCardEvent extends GameEvent {
    private final PlayerToken playerToken;
    private final int drawnCardId;

    /**
     * @param playerToken   the token of the player who draws the card from the gold deck
     * @param drawnCardId   the drawn card id
     */
    public DrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        this.playerToken = playerToken;
        this.drawnCardId = drawnCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDrawnGoldDeckCardEvent(playerToken, drawnCardId);
    }
}
