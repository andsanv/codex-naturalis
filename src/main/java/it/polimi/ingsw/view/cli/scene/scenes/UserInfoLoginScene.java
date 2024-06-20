package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;
import java.util.Optional;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.view.UserInfoManager;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class UserInfoLoginScene extends Scene {
    Optional<UserInfo> userInfo;

    public UserInfoLoginScene(SceneManager sceneManager) {
        super(sceneManager);
        userInfo = UserInfoManager.retrieveUserInfo();
        
        this.commands = Arrays.asList(
            new CLICommand("yes", "to continue with the saved account", null),
            new CLICommand("no", "to create a new account or log in manually to an existing one", null)
        );
    }

    @Override
    public void onEntry() {
        if(userInfo.isEmpty())
            sceneManager.transition(LoginMethodScene.class);

        CLIPrinter.displaySceneTitle("Account found on this device", BLUE);

        System.out.println("The account " + userInfo + " has been found on this device, do you want to use it?");
    }

    @Override
    public void handleCommand(String[] args) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommand'");
    }
    
}
