package it.polimi.ingsw.distributed.server.rmi;

import java.io.IOException;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.Status;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;

/**
 * This class is the RMI implementation of the Client abstract class.
 * This class will be used by the server to perform actions on the client.
 */
public class RMIHandler extends Client {
    /** This is a reference to the client's main view */
    public MainViewActions rmiMainView;

    /** This is a reference to the client's game view */
    public GameViewActions rmiGameView;

    /**
     * This constructore creates from the main view and the game view a new RMIHandler. 
     *
     * @param rmiMainView the main view of the client
     * @param rmiGameView the game view of the client
     */
    public RMIHandler(MainViewActions rmiMainView, GameViewActions rmiGameView) {
        this.rmiMainView = rmiMainView;
        this.rmiGameView = rmiGameView;
    }

    @Override
    public void update(GameEvent event) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiGameView.transmitEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }

    }

    // TODO: If status is disconnected do not send
    @Override
    public void trasmitEvent(MainEvent serverEvent) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiMainView.trasmitEvent(serverEvent);
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
    public void transmitEvent(GameEvent event) {
        if (getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE)
            return;
        try {
            rmiGameView.transmitEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            System.err.println("Failed to send");
        }
    }
}
