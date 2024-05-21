package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * The state of the game where a certain player needs to draw a card
 */
public class DrawCardState extends GameState {
    public DrawCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    /**
     * Draws a card from the Resource Deck into playerToken's hand
     *
     * @param playerToken Token that represents the player
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        return gameFlowManager.getTurn().equals(playerToken) && gameModelUpdater.drawResourceDeckCard(playerToken);
    }

    /**
     * Draws a card from the Gold Deck into playerToken's hand
     *
     * @param playerToken Token that represents the player
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawGoldDeckCard(PlayerToken playerToken) {
        return gameFlowManager.getTurn().equals(playerToken) && gameModelUpdater.drawGoldDeckCard(playerToken);
    }

    /**
     * Draws a card from the VisibleResourceCards list into playerToken's hand, based on the player's choice
     *
     * @param playerToken Token that represents the player
     * @param choice Integer used to choose between the visible cards
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawVisibleResourceCard(PlayerToken playerToken, int choice) {
        return gameFlowManager.getTurn().equals(playerToken) && gameModelUpdater.drawVisibleResourceCard(playerToken, choice);
    }

    /**
     * Draws a card from the list of visible GoldCards into playerToken's hand
     *
     * @param playerToken token of the player drawing the card
     * @param choice The offset of the card chosen in the list
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawVisibleGoldCard(PlayerToken playerToken, int choice) {
        return gameFlowManager.getTurn().equals(playerToken) && gameModelUpdater.drawVisibleGoldCard(playerToken, choice);
    }
}
