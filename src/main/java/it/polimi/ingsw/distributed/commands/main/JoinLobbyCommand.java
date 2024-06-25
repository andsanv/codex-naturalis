package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.usermanagement.UserInfo;

/** This command is used to join a lobby. */
public class JoinLobbyCommand extends MainCommand {

  /** The joining user. */
  private final UserInfo userInfo;

  /** The lobby id. */
  private final int lobbyId;

  /** 
   * This constructor creates the command starting from the user info and the lobby id.
   * 
   * @param userInfo the user joining the lobby.
   * @param lobbyId the id of the lobby to join.
   */
  public JoinLobbyCommand(UserInfo userInfo, int lobbyId) {
    this.userInfo = userInfo;
    this.lobbyId = lobbyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    Server.INSTANCE.joinLobby(userInfo, lobbyId);
  }

  /**
   * This getter method returns the user info of the user joining.
   * 
   * @return the user info of the user joining the lobby.
   */
  public UserInfo getUserInfo() {
    return userInfo;
  }

  /**
   * This getter method returns the id of the lobby to join.
   * 
   * @return the id of the lobby to join.
   */
  public int getLobbyId() {
    return lobbyId;
  }

}
