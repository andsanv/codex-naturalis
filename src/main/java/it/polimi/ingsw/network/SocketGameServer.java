package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketGameServer implements GameServer {
    private GameFlowManager gameFlowManager;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets;
    private Map<Player, Socket> playerSockets;

    public SocketGameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSockets = new ArrayList<>();
        playerSockets = new HashMap<>();

        waitClients();
    }

    private void waitClients() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    clientSockets.add(socket);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void addPlayer(Player player) {

    }

    @Override
    public void startGame() {
        gameFlowManager = new GameFlowManager();
    }

    @Override
    public void placeCard(Player player, Coords coords, PlayableCard card, CardSide cardSide) {

    }

    @Override
    public void drawResourceCard(Player player) {

    }

    @Override
    public void drawGoldDeckCard(Player player) {

    }

    @Override
    public void drawVisibleResourceCard(Player player, int chosen) {

    }

    @Override
    public void drawVisibleGoldCard(Player player, int chosen) {

    }

    @Override
    public void drawObjectiveCard(Player player) {

    }

    @Override
    public void drawStarterCard(Player player) {

    }

    @Override
    public void limitPointsReached(Player player) {

    }

    @Override
    public String getGameId() {
        return "";
    }

    @Override
    public List<Player> getPlayers() {
        return List.of();
    }
}
