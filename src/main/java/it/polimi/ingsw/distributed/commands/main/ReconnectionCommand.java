package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.UserInfo;

public class ReconnectionCommand extends MainCommand {

    public final UserInfo userInfo;

    public ReconnectionCommand(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void execute() {
    }

}
