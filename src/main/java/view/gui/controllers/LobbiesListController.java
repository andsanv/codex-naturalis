package view.gui.controllers;

import controller.usermanagement.LobbyInfo;
import controller.usermanagement.UserInfo;
import distributed.commands.main.JoinLobbyCommand;
import view.gui.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Allows a player to visualize all available lobbies.
 */
public class LobbiesListController extends Controller {
    public AtomicReference<List<LobbyInfo>> activeLobbies = null;

    @FXML private StackPane mainStackPane;
    @FXML private StackPane lobbiesStackPane;
    @FXML private StackPane headerStackPane;
    @FXML private ScrollPane scrollPane;
    @FXML private Button backButton;
    @FXML Rectangle rectangle;

    @FXML private VBox lobbiesListVBox;
    @FXML private Button updateLobbiesButton;

    private final Map<LobbyInfo, Button> lobbyToJoinButton = new HashMap<>();

    /**
     * Atomic boolean that allows to understand whether a user was trying to join a lobby.
     */
    private final AtomicBoolean joiningLobby = new AtomicBoolean(false);

    /**
     * Controller initializer
     *
     * @param gui the GUI application
     */
    public void initialize(GUI gui) {
        applyCss();

        this.gui = gui;
        this.selfUserInfo = gui.selfUserInfo;
        this.activeLobbies = gui.availableLobbies;
        this.connectionHandler = gui.connectionHandler;

        backButton.setOnAction(this::handleBackButton);

        updateLobbiesButton.setDisable(true);
        updateLobbiesButton.setOnAction(this::updateLobbies);

        buildList();
    }

    public void applyCss() {
        mainStackPane.getStyleClass().add("main-pane");
        lobbiesStackPane.getStyleClass().add("lobbies-pane");
        headerStackPane.getStyleClass().add("header-pane");
        scrollPane.getStyleClass().add("scroll-pane");
        backButton.getStyleClass().add("back-button");
        updateLobbiesButton.getStyleClass().add("update-button");
        rectangle.getStyleClass().add("dark-overlay-rectangle");
    }

    /**
     * Allows to dynamically build the lobby lists.
     */
    public void buildList() {
        for (int i = lobbiesListVBox.getChildren().size() - 1; i > 0; i++)
            lobbiesListVBox.getChildren().remove(lobbiesListVBox.getChildren().get(i));

        activeLobbies.get().forEach(lobbyInfo -> {
            StackPane lobbyStackPane = new StackPane();
            lobbiesListVBox.getChildren().add(lobbyStackPane);

            if (lobbyStackPane.equals(lobbiesListVBox.getChildren().get(1)))
                VBox.setMargin(lobbyStackPane, new Insets(20, 40, 0, 30));
            else VBox.setMargin(lobbyStackPane, new Insets(5, 40, 0, 30));

            HBox hbox = new HBox();

            StackPane idStackPane = new StackPane();
            idStackPane.setPrefWidth(60);
            idStackPane.setPrefHeight(50);
            idStackPane.getChildren().add(new Text(String.valueOf(lobbyInfo.id)));
            hbox.getChildren().add(idStackPane);

            StackPane managerStackPane = new StackPane();
            managerStackPane.setPrefWidth(120);
            managerStackPane.setPrefHeight(50);
            HBox.setMargin(managerStackPane, new Insets(0, 0, 0, 85));
            managerStackPane.getChildren().add(new Text(lobbyInfo.manager.name));
            hbox.getChildren().add(managerStackPane);

            List<UserInfo> users = lobbyInfo.users.stream()
                    .filter(userInfo -> !userInfo.equals(lobbyInfo.manager))
                    .toList();

            for (int i = 0; i < 3; i++) {
                StackPane playerStackPane = new StackPane();
                playerStackPane.setPrefWidth(120);
                playerStackPane.setPrefHeight(50);
                VBox.setMargin(playerStackPane, new Insets(0, 0, 0, 0));
                playerStackPane.getChildren().add(new Text(i < users.size() ? users.get(i).name : "-"));

                hbox.getChildren().add(playerStackPane);
            }

            StackPane joinableStackPane = new StackPane();
            HBox.setMargin(joinableStackPane, new Insets(0, 0, 0, 85));
            joinableStackPane.setPrefWidth(80);
            joinableStackPane.setPrefHeight(50);
            ImageView joinableImageView = new ImageView(new Image(lobbyInfo.gameStarted ? "/images/icons/lobbyNonJoinable.png" : "/images/icons/lobbyJoinable.png"));
            joinableImageView.setFitWidth(20);
            joinableImageView.setFitHeight(20);
            joinableStackPane.getChildren().add(joinableImageView);
            hbox.getChildren().add(joinableStackPane);

            StackPane joinStackPane = new StackPane();
            HBox.setMargin(joinStackPane, new Insets(0, 0, 0, 0));
            joinStackPane.setPrefWidth(80);
            joinStackPane.setPrefHeight(50);
            Button joinButton = new Button("Join");
            joinStackPane.getChildren().add(joinButton);
            joinButton.setOnAction(this::joinLobby);
            hbox.getChildren().add(joinStackPane);

            lobbyStackPane.getChildren().add(hbox);

            lobbyToJoinButton.put(lobbyInfo, joinButton);
        });
    }

    /**
     * Allows a user to join a lobby.
     * Handler of the "Join" button.
     *
     * @param actionEvent the ActionEven event
     */
    public void joinLobby(ActionEvent actionEvent) {
        System.out.println("[INFO] Registered user will to join a game");

        Optional<LobbyInfo> lobbyToJoin = lobbyToJoinButton.entrySet().stream()
                .filter(entry -> entry.getValue()
                .equals(actionEvent.getSource()))
                .findFirst()
                .map(Map.Entry::getKey);

        lobbyToJoin.ifPresent(lobbyInfo -> {
            joiningLobby.set(true);

            gui.submitToExecutorService(() -> {
                connectionHandler.get().sendToMainServer(new JoinLobbyCommand(selfUserInfo.get(), lobbyInfo.id));
                System.out.println("[INFO] Submitted the JoinLobbyCommand");
            });
        });

        if (lobbyToJoin.isEmpty()) {
            System.out.println("[ERROR] Selected lobby does not exist");
            showPopUp("Lobby does not exist.");
        }
    }

    /**
     * If player was joining a lobby, it allows him to join.
     * Otherwise, it makes it possible for the player to update current view of lists, by enabling a button.
     *
     * @param lobbies list of lobbies
     */
    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        activeLobbies.set(lobbies);

        lobbies.stream()
                .filter(lobby -> lobby.contains(gui.selfUserInfo.get()))
                .findFirst()
                .ifPresentOrElse(
                        lobby -> {
                            joiningLobby.set(false);
                            gui.currentLobby.set(lobby);

                            gui.changeToLobbyScene();
                        },
                        () -> {
                            updateLobbiesButton.setDisable(false);
                        }
                );
    }

    /**
     * Allows to show a more recent version of the lobby list.
     * Handler for the updateLobbiesButton.
     *
     * @param actionEvent the ActionEvent event
     */
    public void updateLobbies (ActionEvent actionEvent) {
        updateLobbiesButton.setDisable(true);

        buildList();
    }

    /**
     * This method is called when the user tries to join a lobby or create one while
     * being in another.
     *
     * @param message the error message
     */
    @Override
    public void handleJoinLobbyError(String message) {
        joiningLobby.set(false);

        showPopUp(message);
    }

    /**
     * Allows the user to go back to the menu.
     *
     * @param actionEvent the ActionEvent event
     */
    public void handleBackButton(ActionEvent actionEvent) {
        gui.changeToMenuScene();
    }

    /**
     * Allows to print messages (errors).
     *
     * @param message the error message
     */
    public void showPopUp(String message) {
        System.out.println(message);
    }
}
