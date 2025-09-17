package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;
import java.util.Optional;

import controller.usermanagement.UserInfo;
import distributed.client.ConnectionHandler;
import view.UserInfoManager;
import view.cli.CLI;
import view.cli.CLICommand;
import view.cli.CLIPrinter;
import view.cli.scene.Scene;
import view.cli.scene.SceneManager;

/**
 * Scene for logging in with the saved UserInfo.
 * Transitions early to account login/creation if no UserInfo is found.
 */
public class UserInfoLoginScene extends Scene {
    Optional<UserInfo> userInfo;

    public UserInfoLoginScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "Here you decide if you want to use a saved account or not";
        this.commands = Arrays.asList(
                new CLICommand("yes", "to continue with the saved account", () -> {
                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.setUserInfo(userInfo.get());

                    cli.waitinLogin.set(true);
                    connectionHandler.reconnect();
                    
                    if(CLIPrinter.displayLoadingMessage("Logging in", cli.waitinLogin, connectionHandler.isConnected, cli.waitingLoginError))
                        sceneManager.transition(LobbiesScene.class);
                    else
                        sceneManager.transition(ConnectionLostScene.class);
                }),
                new CLICommand("no", "to create a new account or log in to an existing one", () -> {
                    sceneManager.transition(AccountScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();

        userInfo = UserInfoManager.retrieveUserInfo();

        if (userInfo.isEmpty()) {
            sceneManager.transition(AccountScene.class);
            return;
        }

        CLIPrinter.displaySceneTitle("Existing account found", BLUE);

        System.out.println(ansi()
                .a("The account ")
                .fg(BLUE).a(userInfo.get()).reset()
                .a(" has been found on this device, do you want to use it?"));
    }
}
