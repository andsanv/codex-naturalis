package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.player.PlayerToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * State through which the flow manager initializes the model
 */
public class InitializationState extends GameState {
  public InitializationState(GameFlowManager gameFlowManager) {
    super(gameFlowManager);
  }

    /**
     * Handles the initialization phase
     *
     * @param idToToken maps players to their tokens
     * @param tokenToStarterCard maps players tokens to their starter card
     * @param tokenToCardSide maps players to their chosen starter card side
     * @param tokenToObjectiveCard maps players to their objective card
     * @return true
     */
  @Override
  public boolean handleInitialization(
      Map<String, PlayerToken> idToToken,
      Map<PlayerToken, StarterCard> tokenToStarterCard,
      Map<PlayerToken, CardSide> tokenToCardSide,
      Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard) {
    // set up ScoreTrack
    gameModelUpdater.setScoreTrack(new ArrayList<>(idToToken.values()));

    // add Player objects
    idToToken
        .values()
        .forEach(
            playerToken ->
                gameModelUpdater.addPlayer(
                    playerToken,
                    tokenToStarterCard.get(playerToken),
                    tokenToCardSide.get(playerToken),
                    tokenToObjectiveCard.get(playerToken)));

    // fill players' hands
    idToToken
        .values()
        .forEach(
            playerToken -> {
              gameModelUpdater.drawResourceDeckCard(playerToken);
              gameModelUpdater.drawResourceDeckCard(playerToken);
              gameModelUpdater.drawGoldDeckCard(playerToken);
            });

    // set common objectives
    gameModelUpdater.setCommonObjectives(
        new ArrayList<>(
            Arrays.asList(
                gameModelUpdater.drawObjectiveCard().get(),
                gameModelUpdater.drawObjectiveCard().get())));

    // start the game
    gameFlowManager.setState(gameFlowManager.playCardState);
    return true;
  }
}
