package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.MainEventHandler;

public class UserInfoEvent extends MainEvent {
    private final UserInfo userInfo;

    public UserInfoEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleUserInfo(userInfo);
    }
}