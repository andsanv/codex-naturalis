package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.view.interfaces.GameEventHandler;
import it.polimi.ingsw.view.interfaces.MainEventHandler;

public abstract interface UI extends MainEventHandler, GameEventHandler {
  public UserInfo getUserInfo();
}
