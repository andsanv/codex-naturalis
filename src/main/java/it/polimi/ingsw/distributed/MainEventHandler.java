package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import java.util.List;

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
   * This method handles the reception of the connection information
   */
  public void handleReceivedConnection();

  /**
   * This method handles the refused reconnection
   */
  public void handleRefusedReconnection();

  /**
   * This method handles the reconnection to an active game
   */
  public void handleReconnetionToGame();

  /**
   * This method is called when the user tries to join a lobby or create one while
   * being in another.
   */
  public void handleAlreadyInLobbyErrorEvent();

}
