package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

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

                    cli.waitingUserInfo.set(true);
                    cli.getConnectionHandler().sendToMainServer(new ConnectionCommand(args[1]));
                    CLIPrinter.displayLoadingMessage("Creating an account", cli.waitingUserInfo);

                    sceneManager.transition(null);
                }),
                new CLICommand("login", Arrays.asList("username", "id"), "to use a previously created account", () -> {
                    if (args.length != 3) {
                        CLIPrinter.displayError("Invalid arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    String username = args[1];
                    int id;

                    try {
                        id = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        CLIPrinter.displayError("The id must be an integer");
                        return;
                    }

                    cli.setUserInfo(new UserInfo(username, id));
                    cli.getConnectionHandler().reconnect();

                    cli.waitingUserInfo.set(true);
                    CLIPrinter.displayLoadingMessage("Logging in", cli.waitingUserInfo);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.displaySceneTitle("Account Menu", BLUE);

        System.out.println("Do you want to create a new account or log in with a previously created one?");
    }
}
