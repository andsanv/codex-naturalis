package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.view.interfaces.GameEventHandler;
import it.polimi.ingsw.view.interfaces.MainEventHandler;

public interface UI extends MainEventHandler, GameEventHandler {
  /**
   * UserInfo getter
   * 
   * @return the UserInfo of the current user
   */
  public UserInfo getUserInfo();

  /**
   * Handles an eventual disconnection from the server
   */
  public void handleDisconnection();
}
