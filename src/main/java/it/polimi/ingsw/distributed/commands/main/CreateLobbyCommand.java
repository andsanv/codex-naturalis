package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

/** This command is used to create a new lobby. */
public class CreateLobbyCommand extends MainCommand {

  /** The user creating the lobby */
  private final UserInfo userInfo;

  /**
   * This constructor creates the command starting from the user info.
   * 
   * @param userInfo the user creating the lobby.
   */
  public CreateLobbyCommand(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    Server.INSTANCE.createLobby(userInfo);
  }

  /**
   * This getter method returns the user info of the lobby creator.
   * 
   * @return the user info of the lobby creator.
   */
  public UserInfo getUserInfo() {
    return userInfo;
  }

}
