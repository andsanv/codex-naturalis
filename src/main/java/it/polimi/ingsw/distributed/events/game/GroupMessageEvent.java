package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event for a message that is sent to all player in the lobby.
 */
public class GroupMessageEvent extends MessageEvent {
    /**
     * Sender of the message
     */
    private final UserInfo sender;

    /**
     * Message as a string
     */
    private final String message;

    /**
     * @param sender  the sender of the message
     * @param message the message as a string
     */
    public GroupMessageEvent(UserInfo sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGroupMessageEvent(sender, message);
    }
}
