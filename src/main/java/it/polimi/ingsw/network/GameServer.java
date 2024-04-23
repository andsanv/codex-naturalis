package it.polimi.ingsw.network;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayerBoard;

import java.util.List;

public interface GameServer {

    // Network functionalities
    void startGame();

    // Game functionalities
    void placeCard(Player player, Coords coords, PlayableCard card, CardSide cardSide) throws Exception;
    void drawResourceCard(Player player) throws Exception;
    void drawGoldDeckCard(Player player) throws Exception;
    void drawVisibleResourceCard(Player player, int chosen) throws Exception;
    void drawVisibleGoldCard(Player player, int chosen) throws Exception;
    void drawObjectiveCard(Player player) throws Exception;
    void drawStarterCard(Player player) throws Exception;
    void limitPointsReached(Player player) throws Exception;

    // Informations
    String getGameId();
    List<Player> getPlayers();
}
