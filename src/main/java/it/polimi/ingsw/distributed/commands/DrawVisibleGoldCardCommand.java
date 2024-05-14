package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawVisibleGoldCardCommand {
    private final String playerId;
    private final int choice;

    public DrawVisibleGoldCardCommand(String playerId, int choice) {
        this.playerId = playerId;
        this.choice = choice;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawVisibleGoldCard(playerId, choice);
    }
}
