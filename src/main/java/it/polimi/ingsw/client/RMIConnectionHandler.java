package it.polimi.ingsw.client;

import it.polimi.ingsw.CLITest;
import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.RMIMainView;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.SignUpCommand;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;
import it.polimi.ingsw.distributed.server.MainServerActions;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class RMIConnectionHandler extends ConnectionHandler implements MainViewActions {
    
    private MainServerActions mainServerActions;
    private final GameServerActions gameServerActions;

    private final BlockingQueue<MainCommand> serverCommandQueue;
    private final BlockingQueue<GameCommand> gameCommandQueue;


    public RMIConnectionHandler() {
        serverCommandQueue = new LinkedBlockingQueue<>();
        gameCommandQueue = new LinkedBlockingQueue<>();

        gameServerActions = null;

        try {
            Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
            mainServerActions = (MainServerActions) registry.lookup(Config.RMIServerName);

            MainViewActions clientMainView = new RMIMainView(new CLITest());

            UserInfo userInfo = new UserInfo(new User("rave"));
            mainServerActions.connectToMain(userInfo, clientMainView);

            mainServerActions.send(new SignUpCommand("test"));

            new Thread(new CommandConsumer<>(serverCommandQueue, this)).start();
            new Thread(new CommandConsumer<>(gameCommandQueue, this)).start();
        } catch (RemoteException | NotBoundException e) {

        }
    }

    public boolean connectToGame(String rmiConnection) {
        return false;
    }

    @Override
    public boolean sendToMainServer(MainCommand mainCommand) {
        try {
            mainServerActions.send(mainCommand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean sendToGameServer(GameCommand gameCommand) {
        try {
            if (gameServerActions != null) {
                gameServerActions.send(gameCommand);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        mainEventHandler.handleServerError(error);
    }


    @Override
    public void receiveEvent(MainEvent serverEvent) throws RemoteException {
        serverEvent.execute(mainEventHandler);
        // connectionHandler.handle(serverEvent);

        // connectionHandler will have handle method that wille execute event
    }

    @Override
    public boolean reconnect() {
        return false;
    }
}
