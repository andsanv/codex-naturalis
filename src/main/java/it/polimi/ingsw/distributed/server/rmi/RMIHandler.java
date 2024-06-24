package it.polimi.ingsw.distributed.server.rmi;

import java.io.IOException;

import it.polimi.ingsw.controller.server.ServerPrinter;
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
        if (isDisconnected()){
            ServerPrinter.displayError("Client is disconnected cannot send event " + event);
            return;
        }
        try {
            rmiGameView.transmitEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            ServerPrinter.displayError("Failed to send update event " + event);
            ServerPrinter.displayError("Setting client as disconnected");
        }

    }

    // TODO: If status is disconnected do not send
    @Override
    public void trasmitEvent(MainEvent event) {
        if (isDisconnected()){
            ServerPrinter.displayError("Client is disconnected cannot send event " + event);
            return;
        }
        try {
            rmiMainView.trasmitEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            ServerPrinter.displayError("Failed to send event " + event);
            ServerPrinter.displayError("Setting client as disconnected");
        }
    }

    @Override
    public void setGameServer(GameServerActions gameServer) {
        if (isDisconnected()){
            ServerPrinter.displayError("Client is disconnected cannot set game server");
            return;
        }
        try {
            rmiMainView.setGameServer(gameServer);
        } catch (IOException e) {
            setDisconnectionStatus();
            ServerPrinter.displayError("Failed to set game server");
            ServerPrinter.displayError("Setting client as disconnected");
        }
    }

    @Override
    public void transmitEvent(GameEvent event) {
        if (isDisconnected()){
            return;
        }
        try {
            rmiGameView.transmitEvent(event);
        } catch (IOException e) {
            setDisconnectionStatus();
            ServerPrinter.displayError("Failed to send event " + event);
            ServerPrinter.displayError("Setting client as disconnected");
        }
    }

    public boolean isDisconnected() {
        return getStatus() == Status.DISCONNETED_FROM_GAME || getStatus() == Status.OFFLINE;
    }
}
