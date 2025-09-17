package view;

import controller.usermanagement.UserInfo;
import view.interfaces.GameEventHandler;
import view.interfaces.MainEventHandler;

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
