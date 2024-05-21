package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

public class DrawResourceDeckCardCommand extends GameCommand {
    private final PlayerToken playerToken;

    public DrawResourceDeckCardCommand(PlayerToken playerToken) {
        this.playerToken = playerToken;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.getCurrentState().drawResourceDeckCard(playerToken);
    }
}
