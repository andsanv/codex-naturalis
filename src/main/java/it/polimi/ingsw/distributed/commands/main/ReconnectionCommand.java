package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.UserInfo;

public class ReconnectionCommand extends MainCommand {

  public final UserInfo userInfo;
  public final int lastEventId;

  public ReconnectionCommand(UserInfo userInfo) {
    this.userInfo = userInfo;
    this.lastEventId = -1;
  }

  public ReconnectionCommand(UserInfo userInfo, int lastEventId) {
    this.userInfo = userInfo;
    this.lastEventId = lastEventId;
  }

  @Override
  public void execute() {}
}
