package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * A direct message from a player to another.
 */
public class DirectMessageEvent extends MessageEvent {
    /**
     * Sender of the message
     */
    private final UserInfo sender;

    /**
     * Receiver of the message
     */
    private final UserInfo receiver;

    /**
     * Message as a string
     */
    private final String message;

    /**
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param message  the message as a string
     */
    public DirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleDirectMessageEvent(sender, sender, message);
    }
}
