package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a direct message has been sent.
 */
public final class DirectMessageEvent extends GameEvent {
    private final UserInfo sender;
    private final UserInfo receiver;
    private final String message;

    /**
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param message  the sent message
     */
    public DirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDirectMessageEvent(sender, receiver, message);
    }
}
