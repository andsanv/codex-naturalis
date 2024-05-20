package it.polimi.ingsw.distributed.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.events.GameEvent;

public class RMIGameView implements GameViewActions {

    private final GameEventHandler gameEventHandler;
    private final MainEventHandler mainEventHandler;

    public RMIGameView(GameEventHandler gameEventHandler, MainEventHandler mainEventHandler) throws RemoteException {
        this.gameEventHandler = gameEventHandler;
        this.mainEventHandler = mainEventHandler;
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        event.execute(gameEventHandler);
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        mainEventHandler.handleErrorMessage(error);
    }    
}
