package it.polimi.ingsw;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.client.RMIConnectionHandler;
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
        ConnectionHandler connectionHandler = new RMIConnectionHandler();

        UserInfo userInfo = new UserInfo(new User("rave"));

        connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));

        CLITest clientEntry = new CLITest();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
    }

}

