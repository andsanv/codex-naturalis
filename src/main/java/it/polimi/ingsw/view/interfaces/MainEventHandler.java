package it.polimi.ingsw.view.interfaces;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.List;
import java.util.Map;

/**
 * This interface represents the main event handler, which is used to handle the
 * main events
 * coming from the server.
 * This interface is implemented by the UI abstract class.
 */
public interface MainEventHandler {

  /**
   * This method handles the reception of the UserInfo and an eventual error
   * message.
   * 
   * @param userInfo
   */
  public void handleLoginEvent(UserInfo userInfo, String error);

  /**
   * This method handles the reception of generic server error
   * 
   * @param error description of the error
   */
  public void handleServerError(String error);

  /**
   * This method handles the reception of the active lobbies
   * 
   * @param lobbies list of lobbies
   */
  public void handleLobbiesEvent(List<LobbyInfo> lobbies);

  /**
   * This method handles the reconnection to an active game
   * 
   * @param userToToken UserInfo to PlayerToken mapping
   * @param slimModel   the simplified game model
   */
  public void handleReconnetionToGame(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken);

  /**
   * This method is called when the user tries to join a lobby or create one while
   * being in another.
   */
  public void handleJoinLobbyError(String message);

  /**
   * Method called when an error occurred while starting a game.
   * Possible errors are user is not the lobby manager, the lobby doesn't
   * exist or there aren't enough players in the lobby.
   * 
   * @param message the error message
   */
  public void handleStartGameError(String message);

  /**
   * Method called when an error occurred while creating a lobby.
   * The user could already be in another one.
   * 
   * @param message the error message
   */
  public void handleCreateLobbyError(String message);

  /**
   * Method called when the user tried to leave a lobby that was non-existent or
   * a lobby he wasn't in.
   * 
   * @param message the error message
   */
  public void handleLeaveLobbyError(String message);

  public void handleGameStartedEvent(List<UserInfo> users);
}
