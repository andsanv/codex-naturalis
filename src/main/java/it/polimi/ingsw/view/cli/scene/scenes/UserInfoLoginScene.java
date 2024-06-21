package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;
import java.util.Optional;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.view.UserInfoManager;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * Scene for logging in with the saved UserInfo.
 * Transitions early to account login/creation if no UserInfo is found.
 */
public class UserInfoLoginScene extends Scene {
    Optional<UserInfo> userInfo = UserInfoManager.retrieveUserInfo();

    public UserInfoLoginScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "Here you decide if you want the given account or not";
        this.commands = Arrays.asList(
                new CLICommand("yes", "to continue with the saved account", () -> {
                    CLI cli = sceneManager.cli;

                    cli.setUserInfo(userInfo.get());
                    
                    cli.waitingUserInfo.set(true);
                    cli.getConnectionHandler().reconnect();
                    CLIPrinter.displayLoadingMessage("Logging in", cli.waitingUserInfo);
                }),
                new CLICommand("no", "to create a new account or log in to an existing one", () -> {
                    sceneManager.transition(AccountScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();

        if (userInfo.isEmpty()){
            sceneManager.transition(AccountScene.class);
            return;
        }

        CLIPrinter.displaySceneTitle("Existing account found", BLUE);

        System.out.println("The account " + userInfo.get() + " has been found on this device, do you want to use it?");
    }
}
