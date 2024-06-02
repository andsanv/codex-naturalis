package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

public class LeaveLobbyCommand extends MainCommand {

  private final UserInfo userInfo;
  private final int lobbyId;

  @Override
  public void execute() {
    Server.INSTANCE.leaveLobby(userInfo, lobbyId);
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public int getLobbyId() {
    return lobbyId;
  }

  public LeaveLobbyCommand(UserInfo userInfo, int lobbyId) {
    this.userInfo = userInfo;
    this.lobbyId = lobbyId;
  }
}
