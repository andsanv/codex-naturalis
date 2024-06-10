package it.polimi.ingsw.view.tui;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.jansi.AnsiConsole;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.JoinLobbyCommand;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.connection.ConnectionHandler;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;

public class TUI implements UI {
    private static String CODEX_NATURALIS = " _____           _             _   _       _                   _ _     \n"
            + //
            "/  __ \\         | |           | \\ | |     | |                 | (_)    \n" + //
            "| /  \\/ ___   __| | _____  __ |  \\| | __ _| |_ _   _ _ __ __ _| |_ ___ \n" + //
            "| |    / _ \\ / _` |/ _ \\ \\/ / | . ` |/ _` | __| | | | '__/ _` | | / __|\n" + //
            "| \\__/\\ (_) | (_| |  __/>  <  | |\\  | (_| | |_| |_| | | | (_| | | \\__ \\\n" + //
            " \\____/\\___/ \\__,_|\\___/_/\\_\\ \\_| \\_/\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/\n" + //
            "                                                                       \n" + //
            "                                                                       ";

    public static void main(String[] args) {
        new TUI().start();
    }

    private UserInfo userInfo = null;
    private Scanner input = new Scanner(System.in);
    private ConnectionHandler connectionHandler = null;
    private State state = State.HOME;
    private boolean running = true;

    private List<LobbyInfo> availableLobbies = null;
    private Object lobbiesLock;

    private AtomicBoolean waitingForLobbyCreation = new AtomicBoolean(false);
    private AtomicBoolean waitingUserInfo = new AtomicBoolean(false);

    public TUI() {
    }

    public void start() {
        new Thread(this::userHandler).start();
    }

    private void userHandler() {
        AnsiConsole.systemInstall();

        System.out.println(ansi().eraseScreen().cursor(0, 0));

        while (running) {
            switch (state) {
                case HOME:
                    homeScreen();
                    break;
                case LOBBY:
                    lobbyScreen();
                    break;
                case END:
                default:
                    running = false;
                    break;
            }
        }

        AnsiConsole.systemUninstall();
    }

    /**
     * Attempts to read UserInfo from file.
     * The saved UserInfo is the last one used on the same machine in the same
     * location.
     * 
     * @return true if found, false if not found or if an error occurred
     */
    private boolean retrieveUserInfo() {
        // Check if a backup exists, open the stream and try to deserialize it
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("userInfo.ser"));
            userInfo = (UserInfo) in.readObject();
            in.close();
            return true;
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            return false;
        }
    }

    /**
     * Attempts to save UserInfo to file, to use it for future logins.
     */
    private void saveUserInfo() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userInfo.ser"));
            out.writeObject(userInfo);
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * Prints the prompt and reads user input.
     * 
     * @return the user's input
     */
    private String prompt() {
        System.out.print(ansi().fg(BLUE).a("> ").reset());
        System.out.flush();
        return input.nextLine();
    }

    /**
     * Prints an error message with formatting.
     * 
     * @param message
     */
    private void displayError(String message) {
        System.out.println(ansi().fg(RED).a("ERROR: ").reset().a(message));
    }

    /**
     * This function is called while waiting for a server event.
     * It shows a loading message until the given condition becomes false.
     * The condition is set to true before returning.
     * 
     * @param msg       message of loading animation
     * @param condition an AtomicBoolean that stops the loading animation when set
     *                  to false
     */
    private void displayLoadingMessage(String msg, AtomicBoolean condition) {
        String[] sequence = { "", ".", "..", "..." };
        int curr = 0;
        while (condition.get()) {
            System.out.print(ansi().eraseLine().cursorToColumn(0).a(msg + sequence[curr++ % sequence.length]));
            System.out.flush();

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
            }
        }
        System.out.println();
        condition.set(true);
    }

    /**
     * In the home screen, the user can choose wether to use a previously created
     * account or a new one and the connection type (Socket or RMI).
     * If he isn't in a game, he will see the lobby screen, otherwise he will
     * reconnect to the game.
     */
    private void homeScreen() {
        System.out.println(ansi().a(CODEX_NATURALIS).reset());
        System.out.println(ansi().bg(BLUE).a("\n┄┄ HOME ┄┄").reset());

        System.out.println(ansi().a("Do you want to connect with Socket or RMI? (enter ").fg(YELLOW).a("s").reset()
                .a(" for Socket, anything else for RMI)"));

        if (prompt() == "s") {
            // connectionHandler = new SocketConnectionHandler(this);
        } else {
            connectionHandler = new RMIConnectionHandler(this);
        }

        String command = "";

        // Check if the user already has an account
        if (retrieveUserInfo()) {
            System.out.println(ansi()
                    .a("The user " + userInfo + " has been found, do you want to continue with this account? (enter ")
                    .fg(YELLOW).a("y").reset().a(" to continue, anything else otherwise)"));
            command = prompt();
        }

        if (command != "y") {
            System.out.println("Choose a username:");
            String username = prompt();
        }

        // Attempt connection to the server and check if the client is already connected
        // to a game

        state = State.LOBBY;

        // TODO handle reconnection
    }

    private void lobbyScreen() {
        System.out.println(ansi().bg(BLUE).a("┄┄MAIN MENU┄┄").reset());
        System.out.println(ansi().a("You can do the following actions:"));
        System.out.println(ansi().fg(YELLOW).a("  1").reset().a(" to create a lobby"));
        System.out.println(ansi().fg(YELLOW).a("  2").reset().a(" to see available lobbies"));
        System.out.println(ansi().fg(YELLOW).a("  3").reset().a(" to see join a lobby"));

        String command = prompt();

        switch (command) {
            case "1":
                waitingForLobbyCreation.set(true);
                connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));
                displayLoadingMessage("Waiting for lobby creation", waitingForLobbyCreation);
                System.out.println("The lobby has been created!");
                printLobbies();
                break;
            case "2":
                printLobbies();
                break;
            case "3":
                connectionHandler.sendToMainServer(new JoinLobbyCommand(userInfo, 0));
                break;
            default:
                displayError("Invalid option");
                state = State.LOBBY;
                break;
        }
    }

    /**
     * Prints available lobbies using the following format
     * 
     * ╒═══════════════╕
     * │ Lobby 13      │
     * ├───────────────┤
     * │ user#21       │
     * │ anotherUser#3 │
     * │ lastUser#1    │
     * ├───────────────┤
     * │ In-Game? X    │
     * ╘═══════════════╛
     */
    private void printLobbies() {
        synchronized (lobbiesLock) {
            if (this.availableLobbies == null || this.availableLobbies.isEmpty())
                System.out.println("No lobbies available");
            else
                for (LobbyInfo lobby : availableLobbies) {
                    final int length = Math.max(15,
                            lobby.users.stream().mapToInt(user -> user.toString().length()).max().orElse(0));

                    System.out.println("╒" + "═".repeat(length) + "╕");
                    System.out.println(
                            "│ Lobby " + lobby.id + "".repeat(length - 9 - Integer.toString(lobby.id).length()) + "│");
                    System.out.println("├" + "─".repeat(length) + "┤");

                    lobby.users.stream().forEach(user -> {
                        boolean inLobby = user.equals(userInfo);
                        if (user.equals(lobby.manager)) {
                            System.out.println(ansi().a("│ ").bg(inLobby ? YELLOW : BLACK).fg(CYAN).a(user).reset()
                                    .a(" ".repeat(length - 2 - user.toString().length()) + "│"));
                        } else {
                            System.out.println(ansi().a("│ ").bg(inLobby ? YELLOW : BLACK).a(user).reset().a(" ".repeat(length - 2 - user.toString().length()) + "│"));
                        }
                    });

                    System.out.println("├" + "─".repeat(length) + "┤");
                    System.out.println(
                            ansi().a("│ In-Game? ").fg(lobby.gameStarted ? GREEN : RED).a(lobby.gameStarted ? "V" : "X")
                                    + " ".repeat(length - 11) + "│");
                    System.out.println("╘" + "═".repeat(length) + "╛");
                }
        }
    }

    /**
     * This method is used to draw a card on screen.
     * 
     * The cards will be printed using the following shape:
     * 
     * ╭─┬─────┬─╮
     * │Q│ 3 C │X│
     * ├─┤ F   ├─┤
     * │A│ FFA │ │
     * ╰─┴─────┴─╯
     * 
     * @param h      horizontal starting position
     * @param v      vertical starting position
     * @param cardId the id of the card to place
     */
    private void printCard(int h, int v, int cardId) {

    }

    /**
     *
     * 
     * 
     * @param lobby
     */
    private void printLobby(LobbyInfo lobby) {
        // Generate the code to print a box containing the lobby information
        System.out.println(lobby);
    }

    @Override
    public void handleUserInfo(UserInfo userInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleUserInfo'");
    }

    @Override
    public void handleServerError(String error) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleServerError'");
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        synchronized (lobbiesLock) {
            this.availableLobbies = lobbies;

            if (lobbies.stream().map(lobby -> lobby.manager).anyMatch(manager -> manager.equals(userInfo))) {
                waitingForLobbyCreation.set(false);
            }
        }
    }

    @Override
    public void handleReceivedConnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleReceivedConnection'");
    }

    @Override
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleScoreTrackEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleResourceCardEvent'");
    }

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnStarterCardEvent'");
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleChosenStarterCardSideEvent'");
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedStarterCardPhaseEvent'");
    }

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnObjectiveCardsEvent'");
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleChosenObjectiveCardEvent'");
    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedObjectiveCardPhaseEvent'");
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommonObjectiveEvent'");
    }

    @Override
    public void handleDirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDirectMessageEvent'");
    }

    @Override
    public void handleGroupMessageEvent(PlayerToken senderToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGroupMessageEvent'");
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleTokenAssignmentEvent'");
    }

    @Override
    public void handleEndedTokenPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedTokenPhaseEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayerElementsEvent'");
    }

    @Override
    public void handleGameError(String error) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGameError'");
    }

    @Override
    public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGameResultsEvent'");
    }

    @Override
    public void handleCardsPlayabilityEvent(PlayerToken playerToken,
            Map<Integer, Pair<CardSide, List<Coords>>> cardsPlayability) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCardsPlayabilityEvent'");
    }

    @Override
    public UserInfo getUserInfo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserInfo'");
    }

    @Override
    public void connectionToGameResult(boolean connectedToGame) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectionToGameResult'");
    }
}

enum State {
    HOME,
    LOBBY,
    END,
}