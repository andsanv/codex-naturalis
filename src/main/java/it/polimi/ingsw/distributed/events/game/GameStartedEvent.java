package it.polimi.ingsw.distributed.events.game;

import java.util.List;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that the game has been started.
 */
public class GameStartedEvent extends GameEvent {
    /**
     * The list of users in the game.
     */
    private final List<UserInfo> users;

    /**
     * @param users list of users as UserInfo
     */
    public GameStartedEvent(List<UserInfo> users) {
        this.users = users;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGameStarted(users);
    }

}
