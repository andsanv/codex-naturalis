package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a group message has been sent.
 */
public final class GroupMessageEvent extends GameEvent {
    private final UserInfo sender;
    private final String message;

    /**
     * @param sender the sender of the message
     * @param message the sent message
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
