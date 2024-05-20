package it.polimi.ingsw.distributed.commands;

import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.distributed.client.RMIMainView;

public class RetrieveLobbyCommand extends ServerCommand {

    private final RMIMainView rmiMainView;

    public RetrieveLobbyCommand(RMIMainView rmiMainView) {
        this.rmiMainView = rmiMainView;
    }

    @Override
    public void execute() {
        List<LobbyInfo> lobbies = Server.INSTANCE.getLobbies();
        try {
            rmiMainView.receiveLobbies(lobbies);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
