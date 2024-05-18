package it.polimi.ingsw.distributed.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.events.GameEvent;

public class RMIGameView implements VirtualGameView {

    private final GameEventHandler gameEventHandler;

    public RMIGameView(GameEventHandler gameEventHandler) throws RemoteException {
        this.gameEventHandler = gameEventHandler;
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        event.execute(gameEventHandler);
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        // gameEventHandler.showError(error);
    }    
}
