package it.polimi.ingsw;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.RMIMainView;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.LeaveLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.SignUpCommand;
import it.polimi.ingsw.distributed.commands.main.StartGameCommand;
import it.polimi.ingsw.distributed.server.MainServerActions;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

class ClientEntry implements UI {
    public static void main(String[] args) throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
        MainServerActions serverActions = (MainServerActions) registry.lookup(Config.RMIServerName);

        mainServerActions = serverActions;

        ClientEntry clientEntry = new ClientEntry();
        MainViewActions clientMainView = new RMIMainView(clientEntry);

        mainServerActions.connect("nome prova", clientMainView);

        mainViewActions = clientMainView;

        userInfo = new UserInfo(new User("nome prova"));

        UserInfo userInfo = ClientEntry.userInfo;

        mainServerActions.send(new CreateLobbyCommand(userInfo));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}

        mainServerActions.send(new LeaveLobbyCommand(userInfo, 0));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}

        mainServerActions.send(new CreateLobbyCommand(userInfo));
    }

    private static UserInfo userInfo = null;
    private static MainServerActions mainServerActions;
    private static MainViewActions mainViewActions;


    @Override
    public void handleReceivedConnection(String rmiConnectionInfo, String socketConnectionInfo) {
        System.out.println("Connection event received");
        System.out.println("RMI Connection Info: " + rmiConnectionInfo);
        System.out.println("Socket Connection Info: " + socketConnectionInfo);

        try {
            mainServerActions.connect(rmiConnectionInfo, mainViewActions);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleServerError(String error) {
        System.out.println("Server error: " + error);
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        System.out.println("-------- New lobbies ----------");
        for (LobbyInfo lobby : lobbies) {
            System.out.println(lobby);
        }
        System.out.println("------------------------------");

    }

    @Override
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        System.out.println("Score track event received");
        System.out.println("Sender Token: " + senderToken);
        System.out.println("Score: " + score);
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        System.out.println("Played card event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Played Card ID: " + playedCardId);
        System.out.println("Played Card Side: " + playedCardSide);
        System.out.println("Played Card Coordinates: " + playedCardCoordinates);
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        System.out.println("Drawn gold deck card event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Drawn Card ID: " + drawnCardId);
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        System.out.println("Drawn resource deck card event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Drawn Card ID: " + drawnCardId);
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId) {
        System.out.println("Drawn visible resource card event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Drawn Card Position: " + drawnCardPosition);
        System.out.println("Drawn Card ID: " + drawnCardId);
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        System.out.println("Common objective event received");
        System.out.println("First Common Objective ID: " + firstCommonObjectiveId);
        System.out.println("Second Common Objective ID: " + secondCommonObjectiveId);
    }

    @Override
    public void handleDirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
        System.out.println("Direct message event received");
        System.out.println("Sender Token: " + senderToken);
        System.out.println("Receiver Token: " + receiverToken);
        System.out.println("Message: " + message);
    }

    @Override
    public void handleGroupMessageEvent(PlayerToken senderToken, String message) {
        System.out.println("Group message event received");
        System.out.println("Sender Token: " + senderToken);
        System.out.println("Message: " + message);
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        System.out.println("Token assignment event received");
        System.out.println("Player: " + player);
        System.out.println("Assigned Token: " + assignedToken);
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        System.out.println("Played card event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Secret Objective Card ID: " + secretObjectiveCardId);
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        System.out.println("Player elements event received");
        System.out.println("Player Token: " + playerToken);
        System.out.println("Resources: " + resources);
    }

    @Override
    public void handleGameError(String error) {
        System.out.println("Game error: " + error);
    }

    @Override
    public void handleUserInfo(UserInfo userInfo) {
        ClientEntry.userInfo = userInfo;
        System.out.println("UserInfo: " + userInfo);
    }

}