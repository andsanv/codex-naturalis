package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;

/**
 * This abstract class represents a client connection.
 * It implements Observer so that it can be used to notify the updates to the clients.
 * It implents MainViewActions and GameViewActions so that it can perform client actions.
 */
public abstract class Client implements Observer, MainViewActions, GameViewActions {

    /** This field represents the state of the connection */
    private Status status = Status.OFFLINE;
    
    /** This object is used to ensure thread safety */
    private final Object statusLock = new Object();

    /**
     * This method returns the status of the connection.
     * 
     * @return the status of the connection
     */
    public final Status getStatus() {
        synchronized(statusLock) {
            return status;
        }
    }

    /**
     * This method sets the status of the connection.
     * 
     * @param status the status of the connection to be set
     */
    public final void setStatus(Status status) {
        synchronized(statusLock) {
            this.status = status;
        }
    }

    /**
     * This is a helper method that accordingly to the previous state of the connection sets the new one.
     * This function is called when a client results disconnected.
     */
    protected final void setDisconnectionStatus() {
        synchronized(statusLock) {
            if (status == Status.IN_GAME) {
                status = Status.DISCONNETED_FROM_GAME;
            } else if (status == Status.IN_MENU) {
                status = Status.OFFLINE;
            }
        }
    }
}