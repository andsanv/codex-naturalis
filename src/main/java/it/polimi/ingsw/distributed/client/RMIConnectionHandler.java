package it.polimi.ingsw.distributed.client;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.client.rmi.CommandConsumer;
import it.polimi.ingsw.distributed.client.rmi.RMIGameView;
import it.polimi.ingsw.distributed.client.rmi.RMIMainView;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.distributed.interfaces.GameServerActions;
import it.polimi.ingsw.distributed.interfaces.MainServerActions;
import it.polimi.ingsw.view.UI;

/**
 * This class is the RMI implementation of the ConnectionHandler interface.
 */
public class RMIConnectionHandler extends ConnectionHandler {

    /**
     * These are the server remote objects used to communicate.
     */
    private MainServerActions mainServerActions;
    private GameServerActions gameServerActions;

    /**
     * These queues are used to keep the commands to be sent to the server.
     */
    private final BlockingQueue<MainCommand> serverCommandQueue;
    private final BlockingQueue<GameCommand> gameCommandQueue;

    /**
     * This is a reference to the client's main view.
     */
    private RMIMainView clientMainView;

    /**
     * This is a reference to the client's game view.
     */
    private RMIGameView clientGameView;

    /**
     * This constructor creates a new RMIConnectionHandler.
     * Initializes the fields and set null the gameServerActions (game is not
     * started).
     * After that, it tries to connect to the rmi main server and created 2 threads
     * with a consumer fot the command queues.
     * 
     * @param userInterface the user interface of the client
     * @throws Exception
     */
    public RMIConnectionHandler(UI userInterface) throws Exception {
        super(userInterface);

        serverCommandQueue = new LinkedBlockingQueue<>();
        gameCommandQueue = new LinkedBlockingQueue<>();

        gameServerActions = null;

        try {
            Registry registry = LocateRegistry.getRegistry(Config.ServerIP, Config.RMIServerPort);
            mainServerActions = (MainServerActions) registry.lookup(Config.RMIServerName);

            this.clientMainView = new RMIMainView(userInterface, this);
            this.clientGameView = new RMIGameView(userInterface);

            new Thread(new CommandConsumer<>(serverCommandQueue, this)).start();
            new Thread(new CommandConsumer<>(gameCommandQueue, this)).start();

            this.isConnected.set(true);
        } catch (Exception e) {
            this.isConnected.set(false);
            this.userInterface.handleDisconnection();
            throw new Exception("Failed to connect to RMI server");
        }
    }

    /**
     * This method adds a main command to the server command queue.
     * 
     * @param command the command to be added
     */
    public void addCommand(MainCommand command) {
        serverCommandQueue.add(command);
    }

    /**
     * This method adds a game command to the game command queue.
     * 
     * @param command the command to be added
     */
    public void addCommand(GameCommand command) {
        gameCommandQueue.add(command);
    }

    /**
     * This method sends a main command to the main server.
     * If the command is a ConnectionCommand, it connects to the server calling
     * connectToMain and waits for the user info.
     * If the command is a ReconnectionCommand, it creates a new RMIMainView and
     * calls the reconnect method and waits for the user info.
     * In any other case, it sends the command to the server.
     * 
     * @param mainCommand the command to be sent
     * @return true if the command is sent, false otherwise
     */
    @Override
    public boolean sendToMainServer(MainCommand mainCommand) {
        if (mainCommand instanceof ConnectionCommand) {
            try {
                mainServerActions.connectToMain(((ConnectionCommand) mainCommand).username, this.clientMainView,
                        this.clientGameView);

                while (userInterface.getUserInfo() == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                this.lastKeepAliveTime = System.currentTimeMillis();
                //checkConnection();
                return true;
            } catch (IOException e) {
                // this.isConnected.set(false);
                // this.userInterface.handleDisconnection();
                // return false;
            }
        } else if (mainCommand instanceof ReconnectionCommand) {
            try {
                this.clientMainView = new RMIMainView(userInterface, this);

                mainServerActions.reconnect(((ReconnectionCommand) mainCommand).userInfo, this.clientMainView,
                        this.clientGameView);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                this.lastKeepAliveTime = System.currentTimeMillis();
                //checkConnection();
            } catch (IOException e) {
                // this.isConnected.set(false);
                // this.userInterface.handleDisconnection();
                // return false;
            }
        } else {
            try {
                mainServerActions.transmitCommand(mainCommand);
            } catch (Exception e) {
                // this.isConnected.set(false);
                // this.userInterface.handleDisconnection();
                // return false;
            }
        }

        return true;
    }

    /**
     * This method sends a game command to the game server.
     * 
     * @param gameCommand the command to be sent
     * @return true if the command is sent, false otherwise
     */
    @Override
    public boolean sendToGameServer(GameCommand gameCommand) {
        try {
            if (gameServerActions != null) {
                gameServerActions.transmitCommand(gameCommand);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // return false;
        }

        return true;
    }

    /**
     * This method tries to connect to the main server.
     * It simply forwards the command to the method sendToMainServer that will
     * handle it properly.ù
     * 
     * @return true if the the reconnection request is sent, false otherwise
     */
    @Override
    public boolean connect(ConnectionCommand connectionCommand) {
        return sendToMainServer(connectionCommand);
    }

    /**
     * This method tries to reconnect to the main server.
     * It creates a new ReconnectionCommand with the user info and sends it to the
     * server.
     * 
     * @return true if the the reconnection request is sent, false otherwise
     */
    @Override
    public boolean reconnect() {
        ReconnectionCommand reconnectionCommand = new ReconnectionCommand(userInterface.getUserInfo());
        return sendToMainServer(reconnectionCommand);
    }

    /**
     * This method sets the game server actions on the client once the game is
     * started.
     * 
     * @param gameServerActions the game server actions
     */
    public void setGameServerActions(GameServerActions gameServerActions) {
        this.gameServerActions = gameServerActions;
    }

    public BlockingQueue<MainCommand> getServerCommandQueue() {
        return serverCommandQueue;
    }

    public BlockingQueue<GameCommand> getGameCommandQueue() {
        return gameCommandQueue;
    }

    /**
     * {@inheritDoc}
     * This function checks if the timeout was exceeded, if true the method quits
     * the loop setting the state as disconnected.
     */
    @Override
    protected void checkConnection() {
        new Thread(
                () -> {
                    while (true) {
                        if (ConnectionHandler.MILLISEC_TIME_OUT < System.currentTimeMillis() - this.lastKeepAliveTime) {
                            this.isConnected.set(false);
                            this.userInterface.handleDisconnection();
                            break;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            this.isConnected.set(false);
                            this.userInterface.handleDisconnection();
                            break;
                        }
                    }
                }).start();
    }

}
