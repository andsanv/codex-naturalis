package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Command to allow a player to discover at which coordinates he can play the cards in his hand.
 
 * As soon as a player draws a card, he sends this command
 * The command will be received in the playCardState, when it's the turn of the next player
 */
public class CardsPlayabilityCommand extends GameCommand {
    /**
     * token representing the player model side
     */
    private final PlayerToken playerToken;

    public CardsPlayabilityCommand(PlayerToken playerToken) {
        this.playerToken = playerToken;
    }

    @Override
    public boolean execute(GameFlowManager gameFlowManager) {
        return gameFlowManager.currentState.getCardsPlayability(playerToken);
    }
}
