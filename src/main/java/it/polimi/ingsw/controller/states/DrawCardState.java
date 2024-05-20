package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.Optional;

/**
 * The state of the game where a certain player needs to draw a card
 */
public class DrawCardState extends GameState {
    public DrawCardState(GameFlowManager gameFlowManager) {
        super(gameFlowManager);
    }

    /**
     * Draws a card from the Resource Deck into playerToken's hand, and proceeds to increment turn counter
     *
     * @param playerToken Token that represents the player
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        return gameModelUpdater.drawResourceDeckCard(playerToken);
    }

    /**
     * Draws a card from the Gold Deck into playerToken's hand, and proceeds to increment turn counter
     *
     * @param playerToken Token that represents the player
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawGoldDeckCard(PlayerToken playerToken) {
        return gameModelUpdater.drawGoldDeckCard(playerToken);
    }

    /**
     * Draws a card from the VisibleResourceCards list into playerToken's hand, based on the player's choice, and proceeds to increment turn counter
     *
     * @param playerToken Token that represents the player
     * @param choice Integer used to choose between the visible cards
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawVisibleResourceCard(PlayerToken playerToken, int choice) {
        return gameModelUpdater.drawVisibleResourceCard(playerToken, choice);
    }

    /**
     * Draws a card from the VisibleGoldCards list into playerToken's hand, based on the player's choice, and proceeds to increment turn counter
     *
     * @param playerToken Token that represents the player
     * @param choice Integer used to choose between the visible cards
     * @return A boolean that depends on whether the operation was successful or not
     */
    @Override
    public boolean drawVisibleGoldCard(PlayerToken playerToken, int choice) {
        return gameModelUpdater.drawVisibleGoldCard(playerToken, choice);
    }
}
