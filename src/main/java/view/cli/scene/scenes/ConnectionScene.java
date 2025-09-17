package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.io.IOException;
import java.util.Arrays;

import distributed.client.ConnectionHandler;
import distributed.client.RMIConnectionHandler;
import distributed.client.SocketConnectionHandler;
import view.cli.CLICommand;
import view.cli.CLIPrinter;
import view.cli.scene.Scene;
import view.cli.scene.SceneManager;

/**
 * Scene for selecting the connection type.
 */
public class ConnectionScene extends Scene {
    public ConnectionScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "Here you can choose how to connect to the server.";
        this.commands = Arrays.asList(
                new CLICommand("socket", "to use a socket connection", () -> {
                    try {
                        ConnectionHandler connectionHandler = new SocketConnectionHandler(sceneManager.cli);
                        sceneManager.cli.setConnectionHandler(connectionHandler);
                        sceneManager.transition(UserInfoLoginScene.class);
                    } catch (IOException e) {
                        CLIPrinter.displayError(
                                "The server is currently unavailable, couldn't connect through sockets, please try later.");
                        sceneManager.stop();
                    }
                }),
                new CLICommand("rmi", "for an RMI connection", () -> {
                    try {
                        ConnectionHandler connectionHandler = new RMIConnectionHandler(sceneManager.cli);
                        sceneManager.cli.setConnectionHandler(connectionHandler);
                        sceneManager.transition(UserInfoLoginScene.class);
                    } catch (Exception e) {
                        CLIPrinter.displayError(
                                "The server is currently unavailable, couldn't connect through RMI, please try later.");
                        sceneManager.stop();
                    }
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();

        CLIPrinter.displaySceneTitle("Connection Menu", BLUE);

        System.out.println("Do you want to connect with Socket or RMI?");
    }
}
