package it.polimi.ingsw.distributed.server.socket;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerPrinter;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.game.MessageCommand;
import it.polimi.ingsw.distributed.commands.main.KeepAliveCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.LoginEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.interfaces.GameServerActions;
import it.polimi.ingsw.distributed.server.Client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class will handle the communication with the client.
 * Receiving command from the client and sending events to it.
 */
public class SocketClientHandler extends Client implements Runnable {

    /**
     * The input stream for the client Command requests.
     */
    private final ObjectInputStream in;

    /**
     * The output stream for the Event server responses.
     */
    private final ObjectOutputStream out;

    /**
     * This is a reference to the gameFlowManager the clientHandler is connected to.
     */
    private GameFlowManager gameFlowManager;

    /**
     * Socket handler executor
     */
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * The constructor initializes the streams for the socket connection.
     * 
     * @param out the output stream.
     * @param in  the input stream.
     */
    public SocketClientHandler(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    /**
     * This method keeps waiting for reading commands from the client.
     * If the received command is a GameCommand, after checking if the
     * gameFlowManager is set, it is executed.
     * If the received command is a MainCommand, it is directly executed.
     * If exceptions are thrown, the loop is stopped and a new ClientHandler should
     * be created (sending a new connection request to the socket server).
     */
    @Override
    public void run() {
        while (true) {
            try {
                Object object = in.readObject();

                if (object == null)
                    continue;

                Command command = (Command) object;

                executorService.submit(() -> {
                    if (command instanceof GameCommand) {
                        if (gameFlowManager == null) {
                            ServerPrinter.displayError("GameFlowManager not set");
                        }

                        GameCommand gameCommand = (GameCommand) command;

                        if (gameCommand instanceof MessageCommand)
                            gameCommand.execute(gameFlowManager);
                        else
                            gameFlowManager.addCommand(gameCommand);
                    } else if (command instanceof MainCommand) {
                        ((MainCommand) command).execute();
                    } else {
                        ServerPrinter.displayWarning("Unrecognized command: " + command);
                    }
                });
            } catch (EOFException e) {
                ServerPrinter.displayError("Error while reading command from " + this.userInfo.get());
                ServerPrinter.displayError("Setting client " + this.userInfo.get() + " as disconnected");
                setDisconnectionStatus();
                break;
            } catch (IOException | ClassNotFoundException e) {
                ServerPrinter.displayError("Error while reading command from " + this.userInfo.get());
                ServerPrinter.displayError("Setting client " + this.userInfo.get() + " as disconnected");
                setDisconnectionStatus();
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trasmitEvent(MainEvent event) throws IOException {
        if (event instanceof LoginEvent)
            this.userInfo.set(((LoginEvent) event).getUserInfo());
        out.writeObject(event);
        out.reset();
        // ServerPrinter.displayDebug("Sent event: " + event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transmitEvent(GameEvent event) throws IOException {
        out.writeObject(event);
        out.reset();
        // ServerPrinter.displayDebug("Sent event: " + event);
    }

    /**
     * This method sets the gameFlowManager for the clientHandler that is created
     * only when a lobby starts a game.
     * 
     * @param gameFlowManager
     */
    public void setGameFlowManager(GameFlowManager gameFlowManager) {
        this.gameFlowManager = gameFlowManager;
        ServerPrinter.displayDebug("GameFlowManager set on client " + this.userInfo.get());
    }

    /**
     * {@inheritDoc}
     * This method update the client on changes on the game model on the server.
     * This method is inherited from the Observer interface.
     */
    @Override
    public void update(GameEvent event) {
        try {
            transmitEvent(event);
            ServerPrinter.displayDebug("Sent update event " + event.getClass() + " to client " + this.userInfo.get());
        } catch (IOException e) {
            setDisconnectionStatus();
            ServerPrinter.displayError("Failed to send update to client " + this.userInfo.get());
        }
    }

    /**
     * {@inheritDoc}
     * This method is not used in the socket implementation.
     */
    @Override
    public void setGameServer(GameServerActions gameServer) throws RemoteException {
        ;
    }
}