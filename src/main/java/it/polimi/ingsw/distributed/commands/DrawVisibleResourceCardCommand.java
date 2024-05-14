package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawVisibleResourceCardCommand {
    private final String playerId;
    private final int choice;

    public DrawVisibleResourceCardCommand(String playerId, int choice) {
        this.playerId = playerId;
        this.choice = choice;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawVisibleResourceCard(playerId, choice);
    }
}
