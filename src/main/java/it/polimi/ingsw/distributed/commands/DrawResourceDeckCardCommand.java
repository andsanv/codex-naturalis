package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawResourceDeckCardCommand extends GameCommand {
    private final String playerId;

    public DrawResourceDeckCardCommand(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawResourceDeckCard(playerId);
    }
}
