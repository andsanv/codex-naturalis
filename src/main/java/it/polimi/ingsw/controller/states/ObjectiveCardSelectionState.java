package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.ChosenObjectiveCardEvent;
import it.polimi.ingsw.distributed.events.game.DrawnObjectiveCardsEvent;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ObjectiveCardSelectionState extends GameState {
  private final long timeLimit; // in seconds
  private Boolean timeLimitReached;

  private final List<PlayerToken> playerTokens;
  private final Map<PlayerToken, Pair<ObjectiveCard, ObjectiveCard>> TokenToDrawnObjectiveCards;
  private final Map<PlayerToken, ObjectiveCard> TokenToChosenObjectiveCard;

  public ObjectiveCardSelectionState(
      GameFlowManager gameFlowManager, List<PlayerToken> playerTokens, long timeLimit) {
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

    TimerTask timeElapsedTask =
        new TimerTask() {
          @Override
          public void run() {
            synchronized (timeLimitReached) {
              timeLimitReached = true;
            }
          }
        };
    timer.schedule(timeElapsedTask, timeLimit * 1000);

    while (true) {
      if (!timeLimitReached)
        synchronized (commands) {
          if (!commands.isEmpty() && commands.poll().execute(gameFlowManager)) {
            if (TokenToDrawnObjectiveCards.keySet().size() == playerTokens.size()
                && TokenToChosenObjectiveCard.keySet().size() == playerTokens.size()) {
              timer.cancel();
              break;
            }
          } else {
            // view.displayError("error");
          }
        }
      else {
        Random random = new Random();

        playerTokens.stream()
            .filter(pt -> !TokenToDrawnObjectiveCards.containsKey(pt))
            .forEach(
                pt ->
                    TokenToDrawnObjectiveCards.put(
                        pt,
                        new Pair<>(
                            gameModelUpdater.drawObjectiveCard().get(),
                            gameModelUpdater.drawObjectiveCard().get())));
        playerTokens.stream()
            .filter(pt -> !TokenToChosenObjectiveCard.containsKey(pt))
            .forEach(
                pt ->
                    TokenToChosenObjectiveCard.put(
                        pt,
                        random.nextInt(2) == 0
                            ? TokenToDrawnObjectiveCards.get(pt).first
                            : TokenToDrawnObjectiveCards.get(pt).second));

        break;
      }
      ;
    }

    gameFlowManager.setState(gameFlowManager.initializationState);
    return new HashMap<>(TokenToChosenObjectiveCard);
  }

  @Override
  public boolean drawObjectiveCards(PlayerToken playerToken) {
    if (TokenToDrawnObjectiveCards.containsKey(playerToken)) return false;

    ObjectiveCard firstCard = gameModelUpdater.drawObjectiveCard().get();
    ObjectiveCard secondCard = gameModelUpdater.drawObjectiveCard().get();
    TokenToDrawnObjectiveCards.put(playerToken, new Pair<>(firstCard, secondCard));

    gameFlowManager.observers.forEach(
        observer ->
            observer.update(
                new DrawnObjectiveCardsEvent(playerToken, firstCard.getId(), secondCard.getId())));
    return true;
  }

  @Override
  public boolean selectObjectiveCard(PlayerToken playerToken, int choice) {
    if (!TokenToDrawnObjectiveCards.containsKey(playerToken)
        || TokenToChosenObjectiveCard.containsKey(playerToken)) return false;

    ObjectiveCard chosenCard =
        (choice == 0)
            ? TokenToDrawnObjectiveCards.get(playerToken).first
            : TokenToDrawnObjectiveCards.get(playerToken).second;
    TokenToChosenObjectiveCard.put(playerToken, chosenCard);

    gameFlowManager.observers.forEach(
        observer -> observer.update(new ChosenObjectiveCardEvent(playerToken, chosenCard.getId())));
    return true;
  }
}
