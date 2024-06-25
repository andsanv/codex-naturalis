package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * In this scene the user decides if he wants to log-in or create a new account.
 */
public class AccountScene extends Scene {
    public AccountScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("new", Arrays.asList("username"), "to create a new account", () -> {
                    if (args.length != 2) {
                        CLIPrinter.displayError("There can't be spaces in the name");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitinLogin.set(true);
                    connectionHandler.connect(new ConnectionCommand(args[1]));

                    if (CLIPrinter.displayLoadingMessage("Creating an account", cli.waitinLogin,
                            connectionHandler.isConnected, cli.waitingLoginError))
                        sceneManager.transition(LobbiesScene.class);
                    else
                        sceneManager.transition(ConnectionLostScene.class);
                }),
                new CLICommand("login", Arrays.asList("username", "id"), "to use a previously created account", () -> {
                    if (args.length != 3) {
                        CLIPrinter.displayError("Invalid arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    String username = args[1];
                    int id;

                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        CLIPrinter.displayError("The id must be an integer");
                        return;
                    }

                    cli.setUserInfo(new UserInfo(username, id));
                    connectionHandler.reconnect();

                    cli.waitinLogin.set(true);
                    if (CLIPrinter.displayLoadingMessage("Logging in", cli.waitinLogin,
                            connectionHandler.isConnected, cli.waitingLoginError))
                        sceneManager.transition(LobbiesScene.class);
                    else
                        sceneManager.transition(ConnectionLostScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Account Menu", BLUE);

        System.out.println("Do you want to create a new account or log in with a previously created one?");
    }
}
