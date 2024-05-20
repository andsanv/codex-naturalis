package it.polimi.ingsw;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.distributed.client.Client;
import it.polimi.ingsw.distributed.client.ClientGame;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.RMIMainView;
import it.polimi.ingsw.distributed.client.SocketMainView;
import it.polimi.ingsw.distributed.commands.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.LeaveLobbyCommand;
import it.polimi.ingsw.distributed.commands.SignUpCommand;
import it.polimi.ingsw.distributed.server.MainServerActions;
import it.polimi.ingsw.distributed.server.SocketMainServer;

// Client entrypoint
public class ClientEntry {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
        MainServerActions serverActions = (MainServerActions) registry.lookup(Config.RMIServerName);

        MainViewActions clientMainView = new RMIMainView(new ClientGame(), new Client());

        serverActions.connect(clientMainView);
        
        serverActions.send(new SignUpCommand("test"));
        
        serverActions.send(new CreateLobbyCommand(clientMainView.getUserInfo()));

        serverActions.send(new LeaveLobbyCommand(clientMainView.getUserInfo(), 0));
    }

}