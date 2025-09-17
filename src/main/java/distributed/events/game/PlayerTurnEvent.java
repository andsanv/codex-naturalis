package distributed.events.game;

import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/**
 * Event that signals that a player's turn started.
 */
public class PlayerTurnEvent extends GameEvent {
    private final PlayerToken currentPlayer;

    /**
     * @param currentPlayer the player token of the player
     */
    public PlayerTurnEvent(PlayerToken currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handlePlayerTurnEvent(currentPlayer);
    }
    
}
