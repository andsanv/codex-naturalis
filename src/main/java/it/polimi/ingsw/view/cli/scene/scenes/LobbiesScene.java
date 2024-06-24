package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.JoinLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.LeaveLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.StartGameCommand;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;
import it.polimi.ingsw.view.connection.ConnectionHandler;

/**
 * In this scene the user can join, leave and create lobbies.
 * Moreover, he can decide to start a game if he is the manager of the lobby and
 * if there are enough players.
 */
public class LobbiesScene extends Scene {
    public LobbiesScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("list", "to list active lobbies", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    if (!cli.getConnectionHandler().isConnected.get()) {
                        sceneManager.transition(ConnectionLostScene.class);
                        return;
                    }

                    CLIPrinter.printLobbies(cli.getLobbies(), cli.getUserInfo());
                }),
                new CLICommand("join", Arrays.asList("id"), "to join the lobby with the given id", () -> {
                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    if (args.length != 2) {
                        CLIPrinter.displayError("Invalid arguments");
                        return;
                    }

                    int id;

                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        CLIPrinter.displayError("The id must be an integer");
                        return;
                    }

                    cli.joiningLobby.set(true);
                    cli.getConnectionHandler().sendToMainServer(new JoinLobbyCommand(cli.getUserInfo(), id));

                    if (!CLIPrinter.displayLoadingMessage("Joining the lobby", cli.joiningLobby,
                            connectionHandler.isConnected, cli.joiningLobbyError))
                        sceneManager.transition(ConnectionLostScene.class);
                }),
                new CLICommand("create", "to create a new lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.creatingLobby.set(true);
                    cli.getConnectionHandler().sendToMainServer(new CreateLobbyCommand(cli.getUserInfo()));

                    if (!CLIPrinter.displayLoadingMessage("Creating the lobby", cli.creatingLobby,
                            connectionHandler.isConnected, cli.creatingLobbyError))
                        sceneManager.transition(ConnectionLostScene.class);
                }),
                new CLICommand("leave", "to leave the current lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    int id = cli.lobbyId.get();

                    if (id == -1) {
                        CLIPrinter.displayError("You are not in a lobby");
                        return;
                    }

                    cli.leavingLobby.set(true);
                    cli.getConnectionHandler()
                            .sendToMainServer(new LeaveLobbyCommand(cli.getUserInfo(), id));

                    if (!CLIPrinter.displayLoadingMessage("Leaving the lobby", cli.creatingLobby,
                            connectionHandler.isConnected, cli.leavingLobbyError))
                        sceneManager.transition(ConnectionLostScene.class);
                }),
                new CLICommand("start", "to start a game in the current lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    int id = cli.lobbyId.get();

                    if (id == -1) {
                        CLIPrinter.displayError("You are not in a lobby");
                        return;
                    }

                    cli.startingGame.set(true);
                    cli.getConnectionHandler()
                            .sendToMainServer(new StartGameCommand(cli.getUserInfo(), id));

                    if (CLIPrinter.displayLoadingMessage("Starting the game", cli.startingGame,
                            connectionHandler.isConnected, cli.startingGameError)) {
                        if (cli.inGame.get())
                            sceneManager.transition(TokenSelectionScene.class);
                    } else
                        sceneManager.transition(ConnectionLostScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Lobbies", BLUE);
    }
}
