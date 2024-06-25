package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.usermanagement.UserInfo;

/** This command is used to start a game. */
public class StartGameCommand extends MainCommand {

  /** The user starting the game */
  private final UserInfo userInfo;

  /** The lobby id */
  private final int lobbyId;

  /**
   * This constructor creates the command starting from the user info and the lobby id.
   * 
   * @param userInfo the user info.
   * @param lobbyId the lobby id.
   */
  public StartGameCommand(UserInfo userInfo, int lobbyId) {
    this.userInfo = userInfo;
    this.lobbyId = lobbyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    Server.INSTANCE.startGame(userInfo, lobbyId);
  }

  /**
   * This getter method returns the user info.
   * 
   * @return the user info.
   */
  public UserInfo getUserInfo() {
    return userInfo;
  }

  /**
   * This getter method returns the lobby id.
   * 
   * @return the lobby id.
   */
  public int getLobbyId() {
    return lobbyId;
  }
}
