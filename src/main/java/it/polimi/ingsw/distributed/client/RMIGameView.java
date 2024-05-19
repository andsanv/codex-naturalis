package it.polimi.ingsw.distributed.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.MainUpdateHandler;
import it.polimi.ingsw.distributed.events.GameEvent;

public class RMIGameView implements VirtualGameView {

    private final GameEventHandler gameEventHandler;
    private final MainUpdateHandler mainUpdateHandler;

    public RMIGameView(GameEventHandler gameEventHandler, MainUpdateHandler mainUpdateHandler) throws RemoteException {
        this.gameEventHandler = gameEventHandler;
        this.mainUpdateHandler = mainUpdateHandler;
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        event.execute(gameEventHandler);
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        mainUpdateHandler.handleErrorMessage(error);
    }    
}
