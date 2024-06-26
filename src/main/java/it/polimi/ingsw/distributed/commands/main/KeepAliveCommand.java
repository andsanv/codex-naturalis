package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.Command;

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
