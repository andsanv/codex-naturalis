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

                    CLIPrinter.printLobbies(cli.getLobbies(), cli.getUserInfo());
                }),
                new CLICommand("join", Arrays.asList("id"), "to join the lobby with the given id", () -> {
                    CLI cli = sceneManager.cli;

                    if (args.length != 2) {
                        CLIPrinter.displayError("Invalid arguments");
                        return;
                    }

                    int id;

                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        CLIPrinter.displayError("The id must be an integer");
                        return;
                    }

                    cli.joiningLobby.set(true);
                    cli.getConnectionHandler().sendToMainServer(new JoinLobbyCommand(cli.getUserInfo(), id));
                    CLIPrinter.displayLoadingMessage("Joining the lobby", cli.joiningLobby);
                }),
                new CLICommand("create", "to create a new lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    cli.creatingLobby.set(true);
                    cli.getConnectionHandler().sendToMainServer(new CreateLobbyCommand(cli.getUserInfo()));
                    CLIPrinter.displayLoadingMessage("Creating the lobby", cli.creatingLobby);
                }),
                new CLICommand("leave", "to leave the current lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    int id = cli.lobbyId.get();

                    if (id == -1) {
                        CLIPrinter.displayError("You are not in a lobby");
                        return;
                    }

                    cli.leavingLobby.set(true);
                    cli.getConnectionHandler()
                            .sendToMainServer(new LeaveLobbyCommand(cli.getUserInfo(), id));
                    CLIPrinter.displayLoadingMessage("Leaving the lobby", cli.creatingLobby);
                }),
                new CLICommand("start", "to start a game in the current lobby", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    int id = cli.lobbyId.get();

                    if (id == -1) {
                        CLIPrinter.displayError("You are not in a lobby");
                        return;
                    }

                    cli.startingGame.set(true);
                    cli.getConnectionHandler()
                            .sendToMainServer(new StartGameCommand(cli.getUserInfo(), id));
                    CLIPrinter.displayLoadingMessage("Starting the game", cli.startingGame);
                }));
    }

    @Override
    public void onEntry() {
        // CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Lobbies", BLUE);
    }
}
