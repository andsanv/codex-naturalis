package it.polimi.ingsw.distributed.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.game.MessageCommand;
import it.polimi.ingsw.distributed.interfaces.GameServerActions;
import it.polimi.ingsw.distributed.interfaces.GameViewActions;

/**
 * This class represents the RMI Game server to connect to.
 * The game server is used in in-game communications.
 * The class implements runnable so that when the server controller creates the
 * RMIGameServer can delegate its execution to a new thread.
 */
public class RMIGameServer extends UnicastRemoteObject implements GameServerActions {

	/**
	 * This is a reference to the gameFlowManager the client is playing the game
	 * with.
	 * It needs to execute the commands received from the client.
	 */
	private final GameFlowManager gameFlowManager;

	/**
	 * This map the GameViewAction for each client.
	 */
	private ConcurrentHashMap<UserInfo, GameViewActions> clients;

	/**
	 * This executor service is used to make rmi function calls async.
	 * The execution of the functions on the server.java instace are done in
	 * separate threads.
	 */
	private final ExecutorService executorService;

	/**
	 * The constructor initializes the attributes and assign the parameters.
	 * 
	 * @param gameFlowManager   the gameflowmanager of the started game
	 * @throws RemoteException
	 */
	public RMIGameServer(GameFlowManager gameFlowManager)
			throws RemoteException {
		executorService = Executors.newCachedThreadPool();
		this.gameFlowManager = gameFlowManager;
	}

	/**
	 * {@inheritDoc}
	 * This method is used from the client to send a command to the server.
	 * The command is added to the command queue in the gameFlowManager.
	 */
	@Override
	public void transmitCommand(GameCommand command) throws RemoteException {
		executorService.submit(() -> {
			if (command instanceof MessageCommand)
				command.execute(gameFlowManager);
			else
				gameFlowManager.addCommand(command);
		});
	}

	/**
	 * {@inheritDoc}
	 * This method is used from the client to connect to the game server.
	 * It is simply added to the clients map along with its GameViewActions.
	 */
	@Override
	public void connectToGame(UserInfo userInfo, GameViewActions clientGameView)
			throws RemoteException {
		clients.put(userInfo, clientGameView);
	}
}