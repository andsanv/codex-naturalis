package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.MainEventHandler;

public abstract interface UI extends MainEventHandler, GameEventHandler {

  public UserInfo getUserInfo();

  public void connectionToGameResult(boolean connectedToGame);
}
