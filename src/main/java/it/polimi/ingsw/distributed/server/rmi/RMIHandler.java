package it.polimi.ingsw.distributed.server.rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;

public class RMIHandler extends Client {
    public MainViewActions rmiMainView;

    public GameViewActions rmiGameView;

    public RMIHandler(MainViewActions rmiMainView, GameViewActions rmiGameView) {
        this.rmiMainView = rmiMainView;
        this.rmiGameView = rmiGameView;
    }

    @Override
    public void update(GameEvent event) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiGameView.receiveEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }

    }

    // TODO: If status is disconnected do not send
    @Override
    public void receiveEvent(MainEvent serverEvent) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiMainView.receiveEvent(serverEvent);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }
    }

    @Override
    public void setGameServer(GameServerActions gameServer) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiMainView.setGameServer(gameServer);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }
    }

    @Override
    public void receiveEvent(GameEvent event) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiGameView.receiveEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }
    }
}
