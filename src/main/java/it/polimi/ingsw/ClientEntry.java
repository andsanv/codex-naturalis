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
import it.polimi.ingsw.client.ConnectionHandler;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

// Client entrypoint
public class ClientEntry {
    // private final ConnectionHandler connectionHandler;

    public static void main(String[] args) throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
        MainServerActions mainServerActions = (MainServerActions) registry.lookup(Config.RMIServerName);

        CLITest clientEntry = new CLITest();
        MainViewActions clientMainView = new RMIMainView(clientEntry);
        UserInfo userInfo = new UserInfo(new User("rave"));

        mainServerActions.connectToMain(userInfo, clientMainView);

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

}

