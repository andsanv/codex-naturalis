package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;

public abstract class Client implements Observer, MainViewActions, GameViewActions {
    private Status status = Status.OFFLINE;
    private final Object statusLock = new Object();

    public final Status getStatus() {
        synchronized(statusLock) {
            return status;
        }
    }

    public final void setStatus(Status status) {
        synchronized(statusLock) {
            this.status = status;
        }
    }

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