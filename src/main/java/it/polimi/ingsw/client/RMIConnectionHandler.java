package it.polimi.ingsw.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import it.polimi.ingsw.CLITest;
import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.client.RMIMainView;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.SignUpCommand;
import it.polimi.ingsw.distributed.server.GameServerActions;
import it.polimi.ingsw.distributed.server.MainServerActions;

public class RMIConnectionHandler extends ConnectionHandler {
    
    private MainServerActions mainServerActions;
    private final GameServerActions gameServerActions;

    private final BlockingQueue<MainCommand> serverCommandQueue;
    private final BlockingQueue<GameCommand> gameCommandQueue;


    public RMIConnectionHandler(UI userInterface) {
        super(userInterface);

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
    public boolean reconnect() {
        return false;
    }
}
