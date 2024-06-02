package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

public class CreateLobbyCommand extends MainCommand {

  private final UserInfo userInfo;

  @Override
  public void execute() {
    Server.INSTANCE.createLobby(userInfo);
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public CreateLobbyCommand(UserInfo userInfo) {
    this.userInfo = userInfo;
  }
}
