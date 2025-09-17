package controller.states;

import controller.GameFlowManager;
import controller.GameModelUpdater;
import controller.ServerPrinter;
import controller.observer.Observer;
import controller.usermanagement.UserInfo;
import distributed.events.game.EndedInitializationPhaseEvent;
import distributed.events.game.PlayerTurnEvent;
import model.GameModel;
import model.card.CardSide;
import model.card.ObjectiveCard;
import model.card.StarterCard;
import model.deck.Decks;
import model.player.PlayerToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * State through which the flow manager initializes the model
 */
public class InitializationState extends GameState {
    private final List<Observer> observers;
    private final AtomicInteger lastEventId;
    private final Decks decks;

    public InitializationState(GameFlowManager gameFlowManager, Decks decks, List<Observer> observers,
            AtomicInteger lastEventId) {
        super(gameFlowManager);

        this.decks = decks;
        this.observers = observers;
        this.lastEventId = lastEventId;
    }

    /**
     * Handles the initialization phase
     *
     * @param userInfoToToken      maps players to their tokens
     * @param tokenToStarterCard   maps players tokens to their starter card
     * @param tokenToCardSide      maps players to their chosen starter card side
     * @param tokenToObjectiveCard maps players to their objective card
     * @return true
     */
    @Override
    public boolean handleInitialization(
            Map<UserInfo, PlayerToken> userInfoToToken,
            Map<PlayerToken, StarterCard> tokenToStarterCard,
            Map<PlayerToken, CardSide> tokenToCardSide,
            Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard) {
        // set up model
        List<ObjectiveCard> commonObjectives = new ArrayList<>(
                Arrays.asList(decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow(),
                        decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow()));
        List<PlayerToken> playerTokens = new ArrayList<>(userInfoToToken.values());

        GameModelUpdater gameModelUpdater = new GameModelUpdater(
                new GameModel(decks, playerTokens, tokenToStarterCard, tokenToCardSide, tokenToObjectiveCard,
                        commonObjectives,
                        observers, lastEventId));

        // set up controller
        userInfoToToken.entrySet().forEach(x -> gameFlowManager.userInfoToToken.put(x.getKey(), x.getValue()));
        gameFlowManager.setGameModelUpdater(gameModelUpdater);
        gameFlowManager.initializeGameStates();

        // start the game
        gameFlowManager.setState(gameFlowManager.playCardState);
        ServerPrinter.displayInfo("Initialization phase ended for lobby " + gameFlowManager.lobbyId);
        gameFlowManager.notify(new EndedInitializationPhaseEvent(gameModelUpdater.getSlimGameModel()));
        userInfoToToken.values().forEach(token -> gameModelUpdater.computeCardsPlayability(token));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ServerPrinter.displayError("GameFlowManager thread was interrupted."); // allows clients to fetch data from
                                                                                   // slim
                                                                                   // model
        }
        gameFlowManager.notify(new PlayerTurnEvent(gameFlowManager.getTurn()));

        return true;
    }
}
