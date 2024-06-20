package it.polimi.ingsw.view.cli.scene;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.io.IOException;
import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.connection.ConnectionHandler;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;

public class ConnectionScene extends Scene {
    public ConnectionScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "Here you can choose how to connect to the server.\nDo you want to connect with Socket or RMI?";
        this.commands = Arrays.asList(
                new CLICommand("socket", "to use a socket connection"),
                new CLICommand("rmi", "for an RMI connection"));
    }

    @Override
    public void onEntry() {
        System.out.println(ansi().reset().eraseScreen().cursor(0, 0));

        CLIPrinter.displayScreenTitle("Connection Menu", BLUE);

        System.out.println("Choose a connection type:");
    }

    public void onExit() {
    }

    public void handleCommand(String[] args) {
        String command = args[0].toLowerCase();

        if (args.length != 1) {
            CLIPrinter.displayError("Invalid connection type");
            return;
        }

        switch (command) {
            case "socket":
                try {
                    ConnectionHandler connectionHandler = new SocketConnectionHandler(sceneManager.cli);
                    sceneManager.cli.setConnectionHandler(connectionHandler);
                } catch (IOException e) {
                    CLIPrinter.displayError(
                            "The server is currently unavailable, couldn't connect through sockets, please try later.");
                    sceneManager.stop();
                }
                break;
            case "rmi":
                try {
                    ConnectionHandler connectionHandler = new RMIConnectionHandler(sceneManager.cli);
                    sceneManager.cli.setConnectionHandler(connectionHandler);
                } catch (Exception e) {
                    CLIPrinter.displayError(
                            "The server is currently unavailable, couldn't connect through RMI, please try later.");
                    sceneManager.stop();
                }
                break;
            default:
                CLIPrinter.displayError("Invalid connection type");
                break;
        }
    }
}
