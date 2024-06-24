package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;
import it.polimi.ingsw.view.connection.ConnectionHandler;

public class TokenSelectionScene extends Scene {

    public TokenSelectionScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("list", "to print available tokens", null),
                new CLICommand("blue", "to select the blue token", null),
                new CLICommand("green", "to select the green token", null),
                new CLICommand("red", "to select the red token", null),
                new CLICommand("yellow", "to select the yellow token", null));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Match Started - Token Selection", YELLOW);

        System.out.println("Welcome to the match! You are playing with:");
        System.out.println(ansi().fg(YELLOW));
        sceneManager.cli.usersInGame.get().stream().forEach(System.out::println);
        System.out.println(ansi().reset());

        System.out.println("You have to pick a token to play:");
    }

    @Override
    public void handleCommand(String[] args) {
        CLI cli = sceneManager.cli;
        ConnectionHandler connectionHandler = cli.getConnectionHandler();

        if (args.length != 1)
            CLIPrinter.displayError("Invalid option");

        if (args[0].equalsIgnoreCase("list")) {
            cli.getAvailableTokens().forEach(System.out::println);
            return;
        }

        if (Arrays.asList(PlayerToken.values()).stream()
                .map(PlayerToken::toString)
                .noneMatch(t -> args[0].equalsIgnoreCase(t)))
            CLIPrinter.displayError("Invalid option");

        PlayerToken token = PlayerToken.valueOf(args[0]);

        if (!sceneManager.cli.isTokenAvailable(token)) {
            CLIPrinter.displayError(token + " token already selected by another player.");
            return;
        }

        if (!CLIPrinter.displayLoadingMessage("Selecting " + token.toString().toLowerCase() + " token",
                cli.waitingGameEvent, connectionHandler.isConnected, cli.lastGameError)) {
            sceneManager.transition(ConnectionLostScene.class);
            return;
        }

        if (!CLIPrinter.displayLoadingMessage("Waiting for all users to select their token",
                cli.waitingGameEvent, connectionHandler.isConnected, cli.lastGameError)) {
            sceneManager.transition(ConnectionLostScene.class);
            return;
        }

        sceneManager.transition(StarterCardScene.class);
    }
}
