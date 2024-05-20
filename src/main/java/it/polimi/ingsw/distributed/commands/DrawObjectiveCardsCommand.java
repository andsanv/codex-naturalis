package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

public class DrawObjectiveCardsCommand {
    private final PlayerToken playerToken;

    public DrawObjectiveCardsCommand(PlayerToken playerToken) {
        this.playerToken = playerToken;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawObjectiveCards(playerToken);
    }
}
