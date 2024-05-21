package it.polimi.ingsw.distributed.events.game;

import java.util.Map;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Event to signal a player's updated elements' map.
 */
public final class PlayerElementsEvent extends GameEvent {
    private final PlayerToken playerToken;
    private final Map<Elements, Integer> resources;

    /**
     * @param playerToken   the token of the player that gets his elements updated
     * @param resources     the updated elements map
     */
    public PlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        this.playerToken = playerToken;
        this.resources = resources;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handlePlayerElementsEvent(playerToken, resources);
    }
}
