package distributed.events.game;

import controller.usermanagement.UserInfo;
import distributed.events.main.MainEvent;
import view.interfaces.MainEventHandler;

import java.util.List;

/**
 * Event to signal that the game has been started.
 */
public class GameStartedEvent extends MainEvent {
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
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleGameStartedEvent(users);
    }
}
