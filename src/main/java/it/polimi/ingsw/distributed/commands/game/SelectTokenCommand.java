package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.player.PlayerToken;

public class SelectTokenCommand {
    public final UserInfo player;
    public final PlayerToken playerToken;

    public SelectTokenCommand(UserInfo player, PlayerToken playerToken) {
        this.player = player;
        this.playerToken = playerToken;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.getCurrentState().selectToken(player.name, playerToken);
    }
}
