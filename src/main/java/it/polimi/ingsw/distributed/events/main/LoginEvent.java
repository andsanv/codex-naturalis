package it.polimi.ingsw.distributed.events.main;

import java.util.Optional;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.MainEventHandler;

/**
 * After attempting a login or signup to the game server, the client receives
 * this event.
 * This event contains the UserInfo that will be used for future requests.
 * If the credentials were invalid or the account is already in use, the user
 * will receive both an error message and a new valid UserInfo.
 */
public class LoginEvent extends MainEvent {
  private final UserInfo userInfo;
  private final Optional<String> error;

  public LoginEvent(UserInfo userInfo, Optional<String> error) {
    this.userInfo = userInfo;
    this.error = error;
  }

  @Override
  public void execute(MainEventHandler mainEventHandler) {
    mainEventHandler.handleLoginEvent(userInfo, error);
  }
}
