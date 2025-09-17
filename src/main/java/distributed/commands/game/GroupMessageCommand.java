package distributed.commands.game;

import controller.GameFlowManager;
import controller.usermanagement.UserInfo;
import distributed.events.game.GroupMessageEvent;

/**
 * Command to send a private message from a player to another.
 */
public class GroupMessageCommand extends MessageCommand {
    /**
     * Sender of the message
     */
    private final UserInfo sender;

    /**
     * Message as a string
     */
    private final String message;

    /**
     * @param sender   the sender of the message
     * @param message  the message as a string
     */
    public GroupMessageCommand(UserInfo sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        if (message != null && !message.isEmpty())
            gameFlowManager.forwardMessage(new GroupMessageEvent(sender, message));

        return true;
    }
}
