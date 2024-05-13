package it.polimi.ingsw.distributed.events;

import java.util.Map;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.common.Elements;

/**
 * Event to signal a player's updated elements' map.
 */
public final class PlayerElementsEvent extends GameEvent {
    private final UserInfo player;
    private final Map<Elements, Integer> resources;

    /**
     * @param player    the player who gets his elements updated
     * @param resources the new elementes map
     */
    public PlayerElementsEvent(UserInfo player, Map<Elements, Integer> resources) {
        this.player = player;
        this.resources = resources;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handlePlayerElementsEvent(player, resources);
    }
}
