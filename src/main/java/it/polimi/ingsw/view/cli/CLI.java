package it.polimi.ingsw.view.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.UserInfoManager;
import it.polimi.ingsw.view.cli.scene.SceneManager;
import it.polimi.ingsw.view.cli.scene.scenes.ConnectionScene;
import it.polimi.ingsw.view.cli.scene.scenes.HomeScene;
import it.polimi.ingsw.view.cli.scene.scenes.LobbiesScene;
import it.polimi.ingsw.view.cli.scene.scenes.AccountScene;
import it.polimi.ingsw.view.cli.scene.scenes.UserInfoLoginScene;
import it.polimi.ingsw.view.connection.ConnectionHandler;

public class CLI implements UI {
    public static void main(String[] args) {
        new CLI();
    }

    /**
     * The connection handler used to communicate with the server
     */
    private ConnectionHandler connectionHandler = null;

    /**
     * The manager of the CLI scenes
     */
    private final SceneManager sceneManager = new SceneManager(this);

    /**
     * The only input scanner used by the CLI
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * The UserInfo of the user that is logged-in.
     */
    private UserInfo userInfo;

    /**
     * An Object for synchronizing userInfo
     */
    private final Object userInfoLock = new Object();

    /**
     * A list of all active lobbies.
     */
    private List<LobbyInfo> lobbies;

    /**
     * An Object for synchronizing lobbies
     */
    private final Object lobbiesLock = new Object();

    /**
     * The id of the current lobby
     */
    public final AtomicInteger lobbyId = new AtomicInteger(-1);

    /**
     * Thread-safe boolean that is true if the CLI is waiting for the server to send
     * the UserInfo used to make the following requests.
     */
    public final AtomicBoolean waitinLogin = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message that the user can receive
     * while logging-in.
     */
    public final AtomicReference<String> waitingLoginError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true if the CLI is waiting for the server to send
     * the list of available lobbies.
     */
    public final AtomicBoolean waitingLobbies = new AtomicBoolean(false);

    /**
     * Thread-safe boolean that is true while the user is joining a lobby.
     */
    public final AtomicBoolean joiningLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if the user couldn't join
     * the lobby.
     */
    public final AtomicReference<String> joiningLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is creating a lobby.
     */
    public final AtomicBoolean creatingLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if creating the lobby went
     * wrong.
     */
    public final AtomicReference<String> creatingLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is leaving a lobby.
     */
    public final AtomicBoolean leavingLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if the user couldn't leave
     * the lobby.
     */
    public final AtomicReference<String> leavingLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is starting a game.
     */
    public final AtomicBoolean startingGame = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if start game failed.
     */
    public final AtomicReference<String> startingGameError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true if the user is in a match.
     */
    public final AtomicBoolean inGame = new AtomicBoolean(false);

    private CLI() {
        /*
         * Register scenes in the SceneManager
         */
        sceneManager.registerScene(new HomeScene(sceneManager));
        sceneManager.registerScene(new ConnectionScene(sceneManager));
        sceneManager.registerScene(new UserInfoLoginScene(sceneManager));
        sceneManager.registerScene(new AccountScene(sceneManager));
        sceneManager.registerScene(new LobbiesScene(sceneManager));

        /*
         * Init and start the SceneManager
         */
        sceneManager.setInitialScene(HomeScene.class);
        sceneManager.start();

        /*
         * Thread for handling the user input
         */
        new Thread(() -> {
            while (sceneManager.isRunning.get()) {
                System.out.print("> ");
                System.out.flush();
                String input = scanner.nextLine();
                sceneManager.handleInput(input);
            }
            scanner.close();
        }).start();
    }

    /**
     * ConnectionHandler setter.
     * 
     * @param connectionHandler the ConnectionHandler that the CLI will use
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * ConnectionHandler getter.
     * 
     * @return the ConnectionHandler used by the CLI.
     */
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    /**
     * UserInfo setter.
     * 
     * @param userInfo the new UserInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
        }
    }

    /**
     * Lobbies getter as copy.
     */
    public List<LobbyInfo> getLobbies() {
        synchronized (lobbiesLock) {
            return lobbies != null ? new ArrayList<>(lobbies) : null;
        }
    }

    @Override
    public void handleLoginEvent(UserInfo userInfo, String error) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
            UserInfoManager.saveUserInfo(userInfo);
        }

        waitingLoginError.set(error);
        waitinLogin.set(false);
    }

    @Override
    public void handleServerError(String error) {
        CLIPrinter.displayError(error);
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        synchronized (lobbiesLock) {
            this.lobbies = lobbies;

            UserInfo userInfo = this.getUserInfo();

            lobbies.stream()
                    .filter(lobby -> lobby.contains(userInfo))
                    .findAny()
                    .ifPresentOrElse(
                            lobby -> {
                                lobbyId.set(lobby.id);

                                joiningLobby.set(false);

                                if (lobby.manager.equals(userInfo)) {
                                    creatingLobby.set(false);
                                }
                            },
                            () -> {
                                leavingLobby.set(false);
                                lobbyId.set(-1);
                            });
        }
    }

    @Override
    public void handleReceivedConnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleReceivedConnection'");
    }

    @Override
    public void handleRefusedReconnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleRefusedReconnection'");
    }

    @Override
    public void handleReconnetionToGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleReconnetionToGame'");
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
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
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
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCardsPlayabilityEvent'");
    }

    @Override
    public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLimitPointsReachedEvent'");
    }

    @Override
    public UserInfo getUserInfo() {
        synchronized (userInfoLock) {
            return userInfo != null ? new UserInfo(userInfo) : null;
        }
    }

    @Override
    public void connectionToGameResult(boolean connectedToGame) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectionToGameResult'");
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedInitializationPhaseEvent'");
    }

    @Override
    public void handleJoinLobbyError(String message) {
        joiningLobbyError.set(message);
        joiningLobby.set(false);
    }

    @Override
    public void handleStartGameError(String message) {
        startingGameError.set(message);
        startingGame.set(false);
    }

    @Override
    public void handleCreateLobbyError(String message) {
        creatingLobbyError.set(message);
        creatingLobby.set(false);
    }

    @Override
    public void handleLeaveLobbyError(String message) {
        leavingLobbyError.set(message);
        leavingLobby.set(false);
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleResourceCardEvent'");
    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleGoldCardEvent'");
    }
}