package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import java.util.List;

/**
 * This interface represents the main event handler, which is used to handle the main events 
 * coming from the server
 */
public interface MainEventHandler {

  /**
   * This method handles the reception of the assigned user info
   * @param userInfo
   */
  public void handleUserInfo(UserInfo userInfo);

  /**
   * This method handles the reception of generic server error
   * @param error description of the error
   */
  public void handleServerError(String error);

  /**
   * This method handles the reception of the active lobbies
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
}
