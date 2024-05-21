package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;

public class DrawVisibleGoldCardCommand extends GameCommand {
    private final String playerId;
    private final int choice;

    public DrawVisibleGoldCardCommand(String playerId, int choice) {
        this.playerId = playerId;
        this.choice = choice;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawVisibleGoldCard(playerId, choice);
    }
}
