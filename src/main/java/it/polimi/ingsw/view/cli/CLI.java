package it.polimi.ingsw.view.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
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
     * the userInfo.
     */
    public final AtomicBoolean waitingUserInfo = new AtomicBoolean(false);

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
     * Thread-safe boolean that is true while the user is creating a lobby.
     */
    public final AtomicBoolean creatingLobby = new AtomicBoolean(false);

    private CLI() {
        /*
         * Register scenes in the SceneManager
         */
        sceneManager.registerScene(new HomeScene(sceneManager));
        sceneManager.registerScene(new ConnectionScene(sceneManager));
        sceneManager.registerScene(new UserInfoLoginScene(sceneManager));
        sceneManager.registerScene(new AccountScene(sceneManager));

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
     * Lobbies getter.
     */
    public List<LobbyInfo> getLobbies() {
        synchronized (lobbiesLock) {
            return new ArrayList<>(lobbies);
        }
    }

    @Override
    public void handleUserInfo(UserInfo userInfo) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
            UserInfoManager.saveUserInfo(userInfo);
            waitingUserInfo.set(false);
        }
    }

    @Override
    public void handleServerError(String error) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleServerError'");
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        synchronized (lobbiesLock) {
            this.lobbies = lobbies;

            UserInfo userInfo = this.getUserInfo();

            lobbies.stream()
                    .filter(lobby -> lobby.users.contains(userInfo))
                    .findAny()
                    .ifPresentOrElse(
                            lobby -> {
                                lobbyId.set(lobby.id);

                                if (joiningLobby.get()) {
                                    joiningLobby.set(false);
                                }

                                if (creatingLobby.get() && lobby.manager.equals(userInfo)) {
                                    creatingLobby.set(false);
                                }
                            },
                            () -> {
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
    public void handleAlreadyInLobbyErrorEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleAlreadyInLobbyErrorEvent'");
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
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleResourceCardEvent'");
    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleGoldCardEvent'");
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
            return new UserInfo(userInfo);
        }
    }

    @Override
    public void connectionToGameResult(boolean connectedToGame) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectionToGameResult'");
    }
}