package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.SelectTokenCommand;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * Scene where the player selects his token. If the time to select the token
 * runs out the token is randomly selected.
 */
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
        CLIPrinter.displaySceneTitle("Match Started", YELLOW);

        System.out.println("Welcome to the match! The players in the game are:");
        sceneManager.cli.usersInGame.get().stream().forEach(p -> {
            if (!p.equals(sceneManager.cli.getUserInfo()))
                System.out.println(ansi().reset().fg(CYAN).a(p).reset());
            else
                System.out.println(ansi().reset().fg(CYAN).a(p).reset() + " (YOU)");
        });

        System.out.println("\nDuring the match you can use:");
        CLIPrinter.displayCommands(null, Arrays.asList(
                new CLICommand("dm", Arrays.asList("user", "message"), "to send a direct message", null),
                new CLICommand("printdm", Arrays.asList("user"), "to see messages with another user", null),
                new CLICommand("gm", Arrays.asList("message"), "to send a group message", null),
                new CLICommand("printgm", "to see messages in the group", null)));

        System.out.println("\nYou have to pick a token to play:");
    }

    @Override
    public void handleCommand(String[] args) {
        CLI cli = sceneManager.cli;
        ConnectionHandler connectionHandler = cli.getConnectionHandler();

        if (args.length != 1) {
            CLIPrinter.displayError("Invalid option");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            cli.getAvailableTokens().forEach(System.out::println);
            return;
        }

        List<String> tokensAsStrings = Arrays.asList(PlayerToken.values()).stream()
                .map(PlayerToken::toString).collect(Collectors.toList());

        if (tokensAsStrings.stream().noneMatch(t -> t.equalsIgnoreCase(args[0]))) {
            CLIPrinter.displayError("Invalid option " + args[0]);
            return;
        }

        PlayerToken token = PlayerToken.valueOf(args[0].toUpperCase());

        if (!sceneManager.cli.isTokenAvailable(token)) {
            CLIPrinter.displayError(token + " token already selected by another player.");
            return;
        }

        cli.waitingGameEvent.set(true);

        connectionHandler.sendToGameServer(new SelectTokenCommand(cli.getUserInfo(), token));

        if (!CLIPrinter.displayLoadingMessage("Selecting " + token.toString().toLowerCase() + " token",
                cli.waitingGameEvent, connectionHandler.isConnected, null)) {
            sceneManager.transition(ConnectionLostScene.class);
            return;
        }

        if (cli.lastGameError.get() != null) {
            CLIPrinter.displayError(cli.lastGameError.get());
            cli.lastGameError.set(null);
            return;
        }

        try {
            cli.tokenPhaseLatch.await();
        } catch (InterruptedException e) {
            sceneManager.stop();
        }

        if (sceneManager.getCurrentScene() != StarterCardScene.class)
            sceneManager.transition(StarterCardScene.class);
    }
}
