package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawResourceDeckCardCommand {
    private final String playerId;

    public DrawResourceDeckCardCommand(String playerId) {
        this.playerId = playerId;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawResourceDeckCard(playerId);
    }
}
