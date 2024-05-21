package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal the token assigned to a player.
 */
public final class TokenAssignmentEvent extends GameEvent {
    private final UserInfo player;
    private final PlayerToken assignedToken;

    /**
     * @param player        the player who gets the token
     * @param assignedToken the given token
     */
    public TokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        this.player = player;
        this.assignedToken = assignedToken;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleTokenAssignmentEvent(player, assignedToken);
    }
}
