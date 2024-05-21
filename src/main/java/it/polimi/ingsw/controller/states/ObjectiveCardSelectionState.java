package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.*;

public class ObjectiveCardSelectionState extends GameState {
    private final long timeLimit;   // in seconds
    private Boolean timeLimitReached;

    private final List<PlayerToken> playerTokens;
    private final Map<PlayerToken, Pair<ObjectiveCard, ObjectiveCard>> TokenToDrawnObjectiveCards;
    private final Map<PlayerToken, ObjectiveCard> TokenToChosenObjectiveCard;

    public ObjectiveCardSelectionState(GameFlowManager gameFlowManager, List<PlayerToken> playerTokens, long timeLimit) {
        super(gameFlowManager);

        this.playerTokens = playerTokens;
        this.timeLimit = timeLimit;

        this.TokenToDrawnObjectiveCards = new HashMap<>();
        this.TokenToChosenObjectiveCard = new HashMap<>();
    }


    @Override
    public Map<PlayerToken, ObjectiveCard> handleObjectiveCardSelection() {
        Timer timer = new Timer();

        Queue<GameCommand> commands = gameFlowManager.commands;

        TimerTask timeElapsedTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (timeLimitReached) {
                    timeLimitReached = true;
                }
            }
        };
        timer.schedule(timeElapsedTask, timeLimit * 1000);

        while(true) {
            if (!timeLimitReached)
                synchronized (commands) {
                    if (!commands.isEmpty() && commands.poll().execute(gameFlowManager)) {
                        if(TokenToDrawnObjectiveCards.keySet().size() == playerTokens.size() && TokenToChosenObjectiveCard.keySet().size() == playerTokens.size()) {
                            timer.cancel();
                            break;
                        }
                    }
                    else {
                        // view.displayError("error");
                    }
                }
            else {
                Random random = new Random();

                playerTokens.stream().filter(pt -> !TokenToDrawnObjectiveCards.containsKey(pt)).forEach(pt -> TokenToDrawnObjectiveCards.put(pt, new Pair<>(gameModelUpdater.drawObjectiveCard().get(), gameModelUpdater.drawObjectiveCard().get())));
                playerTokens.stream().filter(pt -> !TokenToChosenObjectiveCard.containsKey(pt)).forEach(pt -> TokenToChosenObjectiveCard.put(pt, random.nextInt(2) == 0 ? TokenToDrawnObjectiveCards.get(pt).first : TokenToDrawnObjectiveCards.get(pt).second));

                break;
            };
        }

        gameFlowManager.setState(gameFlowManager.initializationState);
        return new HashMap<>(TokenToChosenObjectiveCard);
    }

    @Override
    public boolean drawObjectiveCards(PlayerToken playerToken) {
        if(TokenToDrawnObjectiveCards.containsKey(playerToken)) return false;

        TokenToDrawnObjectiveCards.put(playerToken, new Pair<>(gameModelUpdater.drawObjectiveCard().get(), gameModelUpdater.drawObjectiveCard().get()));
        return true;
    }

    @Override
    public boolean selectObjectiveCard(PlayerToken playerToken, int choice) {
        if(!TokenToDrawnObjectiveCards.containsKey(playerToken) || TokenToChosenObjectiveCard.containsKey(playerToken)) return false;

        TokenToChosenObjectiveCard.put(playerToken, (choice == 0) ? TokenToDrawnObjectiveCards.get(playerToken).first : TokenToDrawnObjectiveCards.get(playerToken).second);
        return true;
    }
}
