package distributed.events.game;

import controller.usermanagement.UserInfo;
import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

import java.util.Map;

/**
 * This event is used to notify the end of token phase.
 */
public class EndedTokenPhaseEvent extends GameEvent {
    /**
     * The map from user to token
     */
    private final Map<UserInfo, PlayerToken> userInfoToToken;

    /**
     * A flag that is true if the time limit for this phase was reached
     */
    private final boolean timeLimitReached;

    /**
     * @param userInfoToToken  a map from users to their tokens
     * @param timeLimitReached a flag true if the time limit was reached
     */
    public EndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {
        this.userInfoToToken = userInfoToToken;
        this.timeLimitReached = timeLimitReached;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedTokenPhaseEvent(userInfoToToken, timeLimitReached);
    }
}
