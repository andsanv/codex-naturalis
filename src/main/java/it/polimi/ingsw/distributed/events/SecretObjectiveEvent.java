package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;

/**
 * Event to signal that a secret objective has been assingned to a player.
 */
public final class SecretObjectiveEvent extends GameEvent {
    private final UserInfo player;
    private final int secretObjectiveCardId;

    /**
     * @param player                the player who gets the secret card
     * @param secretObjectiveCardId the secret card's id
     */
    public SecretObjectiveEvent(UserInfo player, int secretObjectiveCardId) {
        this.player = player;
        this.secretObjectiveCardId = secretObjectiveCardId;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handlePlayedCardEvent(player, secretObjectiveCardId);
    }
}
