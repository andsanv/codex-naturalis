package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

/** This command is used to leave a lobby. */
public class LeaveLobbyCommand extends MainCommand {

  /** The user leaving the lobby. */
  private final UserInfo userInfo;

  /** The lobby id */
  private final int lobbyId;

  /** 
   * This constructor creates the command starting from the user info and the lobby id.
   * 
   * @param userInfo the user leaving the lobby.
   * @param lobbyId the id of the lobby to leave.
   */
  public LeaveLobbyCommand(UserInfo userInfo, int lobbyId) {
    this.userInfo = userInfo;
    this.lobbyId = lobbyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    Server.INSTANCE.leaveLobby(userInfo, lobbyId);
  }

  /**
   * This getter method returns the user info of the user leaving the lobby.
   * 
   * @return the user info of the user leaving the lobby.
   */
  public UserInfo getUserInfo() {
    return userInfo;
  }

  /**
   * This getter method returns the id of the lobby to leave.
   * 
   * @return the id of the lobby to leave.
   */
  public int getLobbyId() {
    return lobbyId;
  }

}
