package it.polimi.ingsw.distributed.commands;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.Game;
import it.polimi.ingsw.controller.states.GameState;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.Optional;

public class DrawStarterCardCommand extends GameCommand {
    private PlayerToken playerToken;

    public DrawStarterCardCommand(PlayerToken playerToken) {
        this.playerToken = playerToken;
    }

    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.drawStarterCard(playerToken);
    }
}
