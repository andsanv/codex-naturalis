package distributed.commands.main;

import controller.Server;
import controller.usermanagement.UserInfo;

/**
 * This command is used to keep the connection alive.
 */
public class KeepAliveCommand extends MainCommand {

    public int lastKnownEventId;

    public final UserInfo userInfo;

    public KeepAliveCommand(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void execute() {
        Server.INSTANCE.updateKeepAlive(this.userInfo);
    }

}
