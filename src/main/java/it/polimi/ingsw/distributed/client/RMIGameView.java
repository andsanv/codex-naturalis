package it.polimi.ingsw.distributed.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.events.game.GameEvent;

public class RMIGameView implements GameViewActions {

    private final GameEventHandler gameEventHandler;

    public RMIGameView(GameEventHandler gameEventHandler) throws RemoteException {
        this.gameEventHandler = gameEventHandler;
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        event.execute(gameEventHandler);
    }
}
