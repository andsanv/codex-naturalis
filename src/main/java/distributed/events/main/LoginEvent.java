package distributed.events.main;

import controller.usermanagement.UserInfo;
import view.interfaces.MainEventHandler;

/**
 * This event is used to notify a connecting/reconnecting client about the login
 * result.
 * This event contains the UserInfo that will be used for future requests.
 * If the credentials were invalid or the account is already in use, the user
 * will receive both an error message and a new valid UserInfo.
 */
public class LoginEvent extends MainEvent {
  /**
   * The user info of the player.
   */
  private final UserInfo userInfo;

  /**
   * The optional error message.
   */
  private final String error;

  /**
   * This constructor creates the event starting from the user info and the error.
   * 
   * @param userInfo the user info of the player.
   * @param error    the error message.
   */
  public LoginEvent(UserInfo userInfo, String error) {
    this.userInfo = userInfo;
    this.error = error;
  }

  public UserInfo getUserInfo() {
    return new UserInfo(userInfo);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleLoginEvent(userInfo, error);
  }
}
