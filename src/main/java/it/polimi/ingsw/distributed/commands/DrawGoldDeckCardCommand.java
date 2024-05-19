package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;


public class DrawGoldDeckCardCommand extends GameCommand {
    private final String playerId;

    public DrawGoldDeckCardCommand(String playerId) {
        this.playerId = playerId;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawGoldDeckCard(playerId);
    }
}
