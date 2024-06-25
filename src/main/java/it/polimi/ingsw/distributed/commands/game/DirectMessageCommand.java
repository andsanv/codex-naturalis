package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.events.game.DirectMessageEvent;

/**
 * Command to send a private message from a player to another.
 */
public class DirectMessageCommand extends MessageCommand {
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
    public DirectMessageCommand(UserInfo sender, UserInfo receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        if (message != null && !message.isEmpty())
            gameFlowManager.forwardMessage(new DirectMessageEvent(sender, receiver, message));

        return true;
    }
}
