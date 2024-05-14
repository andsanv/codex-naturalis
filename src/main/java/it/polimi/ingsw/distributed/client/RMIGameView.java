package it.polimi.ingsw.distributed.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.GameEvent;

public class RMIGameView implements VirtualGameView {
    public RMIGameView() throws RemoteException {
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEvent'");
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendError'");
    }    
}
