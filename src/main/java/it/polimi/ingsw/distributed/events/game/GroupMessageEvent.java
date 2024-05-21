package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal that a group message has been sent.
 */
public final class GroupMessageEvent extends GameEvent {
    private final PlayerToken senderToken;
    private final String message;

    /**
     * @param senderToken   the token of the sender of the message
     * @param message       the message sent
     */
    public GroupMessageEvent(PlayerToken senderToken, String message) {
        this.senderToken = senderToken;
        this.message = message;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGroupMessageEvent(senderToken, message);
    }
}
