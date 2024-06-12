package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.ChosenObjectiveCardEvent;
import it.polimi.ingsw.distributed.events.game.DrawnObjectiveCardsEvent;
import it.polimi.ingsw.distributed.events.game.EndedObjectiveCardPhaseEvent;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The state represents the game phase where players choose their objective card between two given
 */
public class ObjectiveCardSelectionState extends GameState {
  /**
   * List of tokens in the match
   */
  private final List<PlayerToken> playerTokens;

  /**
   * Decks used to draw setup cards.
   */
  private final Decks decks;

  /**
   * Map that keeps track of objective cards drawn by every player
   */
  private final Map<PlayerToken, Pair<ObjectiveCard, ObjectiveCard>> TokenToDrawnObjectiveCards;

  /**
   * Map that keeps track of chosen objective card by every player
   */
  private final Map<PlayerToken, ObjectiveCard> TokenToChosenObjectiveCard;

  /**
   * Time limit within witch players need to choose their card
   */
  private final long timeLimit; // in seconds

  /**
   * If true, the state machine assigns a random card side to remaining players
   */
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);


  public ObjectiveCardSelectionState(
          GameFlowManager gameFlowManager, Decks decks, List<PlayerToken> playerTokens, long timeLimit) {
    super(gameFlowManager);

    this.decks = decks;
    this.playerTokens = playerTokens;
    this.timeLimit = timeLimit;

    this.TokenToDrawnObjectiveCards = new HashMap<>();
    this.TokenToChosenObjectiveCard = new HashMap<>();
  }

  /**
   * Waits for DrawObjectiveCardsCommands and SelectObjectiveCardCommand by the players.
   * Players need to draw the cards and choose one within a certain time limit, otherwise a random card is chosen for them.
   * If time limit is reached, or if all players chose a card, breaks from loop and returns the map.
   * Throws a EndedObjectiveCardPhaseEvent, so that clients can update their UIs
   *
   * @return The map containing information on what card each player chose
   */
  @Override
  public Map<PlayerToken, ObjectiveCard> handleObjectiveCardSelection() {
    Timer timer = new Timer();

    Queue<GameCommand> commands = gameFlowManager.commands;

    TimerTask timeElapsedTask =
        new TimerTask() {
          @Override
          public void run() {
            synchronized (timeLimitReached) {
              timeLimitReached.set(true);
            }
          }
        };
    timer.schedule(timeElapsedTask, timeLimit * 1000);

    while (true) {
      if (!timeLimitReached.get())
        synchronized (commands) {
          if (!commands.isEmpty() && commands.poll().execute(gameFlowManager)) {
            if (TokenToDrawnObjectiveCards.keySet().size() == playerTokens.size()
                && TokenToChosenObjectiveCard.keySet().size() == playerTokens.size()) {
              timer.cancel();
              break;
            }
          }
        }
      else {
        Random random = new Random();

        playerTokens.stream()
            .filter(playerToken -> !TokenToDrawnObjectiveCards.containsKey(playerToken))
            .forEach(playerToken -> {
              ObjectiveCard firstCard = decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow();
              ObjectiveCard secondCard = decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow();

              decks.objectiveCardsDeck.notify(new DrawnObjectiveCardsEvent(playerToken, firstCard.id, secondCard.id));

              TokenToDrawnObjectiveCards.put(playerToken, new Pair<>(firstCard, secondCard));
            });

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
    }

    gameFlowManager.setState(gameFlowManager.initializationState);
    gameFlowManager.notify(new EndedObjectiveCardPhaseEvent());
    return new HashMap<>(TokenToChosenObjectiveCard);
  }

  /**
   * Handles DrawObjectiveCardsCommand.
   * Updates the TokenToDrawnObjectiveCards map with the drawn cards
   *
   * @param playerToken player drawing the cards
   * @return false if a player already drew his cards or there was an error, true otherwise
   */
  @Override
  public boolean drawObjectiveCards(PlayerToken playerToken) {
    if (TokenToDrawnObjectiveCards.containsKey(playerToken)) return false;

    ObjectiveCard firstCard = decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow();
    ObjectiveCard secondCard = decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow();

    decks.objectiveCardsDeck.notify(new DrawnObjectiveCardsEvent(playerToken, firstCard.id, secondCard.id));
    TokenToDrawnObjectiveCards.put(playerToken, new Pair<>(firstCard, secondCard));
    return true;
  }

  /**
   * Handles SelectObjectiveCardCommand.
   * Updates the TokenToChosenObjectiveCard map with the chosen card.
   *
   * @param playerToken player choosing the card
   * @param choice integer representing the choice
   * @return false if the player already chose his card, true otherwise
   */
  @Override
  public boolean selectObjectiveCard(PlayerToken playerToken, int choice) {
    if (!TokenToDrawnObjectiveCards.containsKey(playerToken)
        || TokenToChosenObjectiveCard.containsKey(playerToken)) return false;

    ObjectiveCard chosenCard =
        (choice == 0)
            ? TokenToDrawnObjectiveCards.get(playerToken).first
            : TokenToDrawnObjectiveCards.get(playerToken).second;
    TokenToChosenObjectiveCard.put(playerToken, chosenCard);

    decks.objectiveCardsDeck.notify(new ChosenObjectiveCardEvent(playerToken, chosenCard.id));
    return true;
  }
}
