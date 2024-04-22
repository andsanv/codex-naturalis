package it.polimi.ingsw.network;

import it.polimi.ingsw.model.player.Player;

import java.util.List;

public interface RoomServer {

    void addPlayer(Player player) throws Exception;
    void startGame() throws Exception;
    List<GameServer> getServers() throws Exception;
    List<Player> getPlayers() throws Exception;

}
