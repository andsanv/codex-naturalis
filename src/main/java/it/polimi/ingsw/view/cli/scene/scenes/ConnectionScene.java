package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;
import it.polimi.ingsw.view.connection.ConnectionHandler;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;

public class ConnectionScene extends Scene {
    public ConnectionScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "Here you can choose how to connect to the server.\nDo you want to connect with Socket or RMI?";
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
        System.out.println(ansi().reset().eraseScreen().cursor(0, 0));

        CLIPrinter.displaySceneTitle("Connection Menu", BLUE);

        System.out.println("Choose a connection type:");
    }

    @Override
    public void handleCommand(String[] args) {
        Optional<CLICommand> command;

        if (args.length != 1) {
            command = Optional.empty();
        } else {
            command = commands.stream()
                    .filter(c -> c.name.equalsIgnoreCase(args[0]))
                    .findFirst();
        }

        command.ifPresentOrElse(CLICommand::execute,
                () -> CLIPrinter.displayError("Invalid connection type"));
    }
}
