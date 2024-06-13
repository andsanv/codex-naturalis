package it.polimi.ingsw.controller;

import it.polimi.ingsw.distributed.events.game.*;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.player.*;

import it.polimi.ingsw.util.Pair;

import java.util.*;

/**
 * One of the tree main classes that manage a game.
 * It receives instructions by the GameFlowManager, and acts on the GameModel, updating it.
 *
 * @see GameFlowManager
 * @see GameModel
 */
public class GameModelUpdater {
  /**
   * The model of the game.
   */
  public final GameModel gameModel;

  /**
   * @param gameModel model of the game
   */
  public GameModelUpdater(GameModel gameModel) {
    this.gameModel = gameModel;
  }

  /**
   * Allows a player to play the given card (and side) at the given coordinates.
   * It updates player elements in case the card placement was successful.
   *
   * @param playerToken token of the player playing the card
   * @param coords coords at which to play the card
   * @param cardId id of the card to play
   * @param cardSide side of the card to play
   * @return false if the card cannot be played at that coordinates, true otherwise
   */
  public boolean playCard(PlayerToken playerToken, Coords coords, int cardId, CardSide cardSide) {
    Player player = gameModel.tokenToPlayer.get(playerToken);
    PlayerBoard playerBoard = player.playerBoard;
    PlayerHand playerHand = player.playerHand;
    PlayableCard card = playerHand.get(cardId);

    if (card == null || !playerBoard.canPlaceCardAt(coords, card, cardSide)) return false;

    playerHand.remove(card);
    PlayedCardEvent playedCardEvent = playerBoard.placeCard(playerToken, coords, card, cardSide);
    gameModel.slimGameModel.addPlayedCard(playedCardEvent);

    playerBoard.updatePlayerElements(playerToken, coords);

    int points = switch (card.pointsType) {
        case ONE -> 1;
        case THREE -> 3;
        case FIVE -> 5;
        case ONE_PER_QUILL -> playerBoard.playerElements.get(Items.QUILL);
        case ONE_PER_INKWELL -> playerBoard.playerElements.get(Items.INKWELL);
        case ONE_PER_MANUSCRIPT -> playerBoard.playerElements.get(Items.MANUSCRIPT);
        case TWO_PER_COVERED_CORNER -> 2 * playerBoard.adjacentCards(coords).keySet().size();
        default -> 0;
    };

    gameModel.scoreTrack.updatePlayerScore(playerToken, points);

    return true;
  }

  /**
   * Checks at which coordinates each card in the hand of the player can be placed, and sends him an event with the results.
   *
   * @param playerToken token representing the player
   * @return true
   */
  public boolean computeCardsPlayability(PlayerToken playerToken) {
    PlayerBoard playerBoard = gameModel.tokenToPlayer.get(playerToken).playerBoard;
    List<PlayableCard> handCards = gameModel.tokenToPlayer.get(playerToken).playerHand.getCards();
    List<Coords> availableSlots = playerBoard.availableCoords();

    Map<Integer, List<Pair<CardSide, Boolean>>> enoughResources = new HashMap<>();
    for(PlayableCard card : handCards) {
      ArrayList<Pair<CardSide, Boolean>> enoughResourcesByCardSide = new ArrayList<>();
      enoughResourcesByCardSide.add(new Pair<>(CardSide.FRONT, card.enoughResources(playerBoard.playerElements, CardSide.FRONT)));
      enoughResourcesByCardSide.add(new Pair<>(CardSide.BACK, card.enoughResources(playerBoard.playerElements, CardSide.BACK)));

      enoughResources.put(card.id, enoughResourcesByCardSide);
    }

    playerBoard.notify(new CardsPlayabilityEvent(playerToken, availableSlots, enoughResources));

    return true;
  }

  /**
   * Allows a player to draw a ResourceCard card.
   * Draws a card from the ResourceCard deck, and adds it to player's hand
   *
   * @param playerToken the token of the player
   * @return false if player's hand is full or deck is empty, true otherwise
   */
  public boolean drawResourceDeckCard(PlayerToken playerToken) {
    PlayerHand playerHand = gameModel.tokenToPlayer.get(playerToken).playerHand;

    if (playerHand.getCards().size() >= 3 || playerHand.getFirstFree() == -1) return false;

    Optional<ResourceCard> card = gameModel.resourceCardsDeck.draw(playerToken, playerHand.getFirstFree());

    if (card.isEmpty()) return false;

    playerHand.add(card.get());
    return true;
  }

  /**
   * Allows a player to draw a GoldCard card.
   * Draws a card from the GoldCard deck, and adds it to player's hand
   *
   * @param playerToken the token of the player
   * @return false if player's hand is full or deck is empty, true otherwise
   */
  public boolean drawGoldDeckCard(PlayerToken playerToken) {
    PlayerHand playerHand = gameModel.tokenToPlayer.get(playerToken).playerHand;

    if (playerHand.size() >= 3 || playerHand.getFirstFree() == -1) return false;

    Optional<GoldCard> card = gameModel.goldCardsDeck.draw(playerToken, playerHand.getFirstFree());

    if (card.isEmpty()) return false;

    playerHand.add(card.get());
    return true;
  }

  /**
   * Allows a player to draw a ResourceCard card.
   * Draws a card from the ResourceCard visible list, and adds it to player's hand.
   *
   * @param playerToken the token of the player
   * @param chosen list index of the card chosen
   * @return false if player's hand is full or deck is empty, true otherwise
   */
  public boolean drawVisibleResourceCard(PlayerToken playerToken, int chosen) {
    PlayerHand playerHand = gameModel.tokenToPlayer.get(playerToken).playerHand;

    if (playerHand.getCards().size() >= 3 || playerHand.getFirstFree() == -1) return false;

    Optional<ResourceCard> card = gameModel.visibleResourceCards.draw(playerToken, chosen, playerHand.getFirstFree());

    if (card.isEmpty()) return false;

    playerHand.add(card.get());
    return true;
  }

  /**
   * Allows a player to draw a ResourceCard card.
   * Draws a card from the GoldCard visible list, and adds it to player's hand.
   *
   * @param playerToken the token of the player
   * @param chosen the card to draw from visible gold cards (0 or 1)
   * @return true if the action has been completed successfully, false otherwise
   */
  public boolean drawVisibleGoldCard(PlayerToken playerToken, int chosen) {
    PlayerHand playerHand = gameModel.tokenToPlayer.get(playerToken).playerHand;

    if (playerHand.getCards().size() >= 3 || playerHand.getFirstFree() == -1) return false;

    Optional<GoldCard> card = gameModel.visibleGoldCards.draw(playerToken, chosen, playerHand.getFirstFree());

    if (card.isEmpty()) return false;

    playerHand.add(card.get());
    return true;
  }

  /**
   * Checks whether limit score is reached by any player.
   *
   * @return true if any player has reached limit score, false otherwise
   */
  public boolean limitScoreReached() {
    return gameModel.scoreTrack.limitPointsReached();
  }

  /**
   * Checks whether one of the two decks is empty.
   *
   * @return true if one of the two decks is empty, false otherwise
   */
  public boolean anyDeckEmpty() {
    return gameModel.resourceCardsDeck.isEmpty() || gameModel.goldCardsDeck.isEmpty();
  }
}
