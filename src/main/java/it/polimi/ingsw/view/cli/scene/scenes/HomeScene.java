package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class HomeScene extends Scene {
    /**
     * Codex CLI logo
     */
    private static String CODEX_NATURALIS = " _____           _             _   _       _                   _ _     \n"
            + //
            "/  __ \\         | |           | \\ | |     | |                 | (_)    \n" + //
            "| /  \\/ ___   __| | _____  __ |  \\| | __ _| |_ _   _ _ __ __ _| |_ ___ \n" + //
            "| |    / _ \\ / _` |/ _ \\ \\/ / | . ` |/ _` | __| | | | '__/ _` | | / __|\n" + //
            "| \\__/\\ (_) | (_| |  __/>  <  | |\\  | (_| | |_| |_| | | | (_| | | \\__ \\\n" + //
            " \\____/\\___/ \\__,_|\\___/_/\\_\\ \\_| \\_/\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/\n" + //
            "                                                                       \n" + //
            "                                                                       ";

    public HomeScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commandsDescription = "This is the main menu, press enter to contine or:";
        this.commands = Arrays.asList(
                new CLICommand("help", "to get the list of available commands", null),
                new CLICommand("quit", "to exit the game", null));
    }

    @Override
    public void onEntry() {
        System.out.println(ansi().eraseScreen().cursor(0, 0));

        System.out.println(CODEX_NATURALIS + "\n");

        CLIPrinter.displaySceneTitle("Main Menu", BLUE);

        System.out.println(
                ansi()
                        .a("Welcome to Codex Naturalis!\n\nIf you don't know how to play, type ")
                        .fg(YELLOW).a("help").reset()
                        .a(" to get the list of available commands.\nWhen you want to close the game, you can enter ")
                        .fg(YELLOW).a("quit").reset()
                        .a(" to exit.\n\nEnter anything (except the two commands listed above) to continue to the game.\n\nHave fun!\n"));
    }

    @Override
    public void handleCommand(String[] args) {
        sceneManager.transition(ConnectionScene.class);
    }
}
