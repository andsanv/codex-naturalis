package view.gui.controllers;

import controller.usermanagement.UserInfo;
import distributed.client.ConnectionHandler;
import distributed.client.RMIConnectionHandler;
import distributed.client.SocketConnectionHandler;
import distributed.commands.main.ConnectionCommand;
import distributed.commands.main.ReconnectionCommand;
import view.gui.GUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the initial configuration phase of the client.
 * It allows the user to choose a username, and eventually enter an id if he was already signed in.
 * User can also choose between RMI and Socket connection to the server.
 */
public class ConfigController extends Controller {
    /**
     * The main controller that will be used by the connection handler
     */
    private MainController mainController = null;

    /**
     * The field where user places his nickname.
     */
    @FXML private TextField nicknameTextField;

    /**
     * The field where user can choose an eventual id.
     */
    @FXML private TextField idTextField;

    /**
     * Submit button to start the connection with the server.
     */
    @FXML private Button submitButton;

    /**
     * Radio button, selected if user wants to use an RMI connection.
     */
    @FXML private RadioButton rmiButton;

    /**
     * Radio button, selected if user wants to use a Socket connection.
     */
    @FXML private RadioButton socketButton;

    /**
     * Controller initializer.
     *
     * @param gui the GUI application
     */
    public void initialize(GUI gui, MainController mainController) {
        // setup gui
        this.gui = gui;
        this.connectionHandler = gui.connectionHandler;
        this.selfUserInfo = gui.selfUserInfo;
        this.mainController = mainController;

        // initialize toggle group for radio buttons
        ToggleGroup toggleGroup = new ToggleGroup();
        rmiButton.setToggleGroup(toggleGroup);
        rmiButton.setSelected(true);
        socketButton.setToggleGroup(toggleGroup);

        // initialize submit button
        submitButton.setOnAction(this::handleSubmitClick);
    }


    /**
     * Allows to connect to server, creating a ConnectionHandler object.
     * Nickname must be longer than 2 characters.
     *
     * @param nickname nickname chosen by player
     * @param id eventual id for reconnection (null if first connection)
     * @param isRmi true if user chose rmi connection, false for socket
     * @return boolean on whether the operation was successful or not
     */
    public boolean connect(String nickname, Integer id, boolean isRmi) throws Exception {
        if (nickname.length() < 3) return false;

        ConnectionHandler connectionHandler;

        if (isRmi) connectionHandler = new RMIConnectionHandler(mainController);
        else connectionHandler = new SocketConnectionHandler(mainController);

        this.connectionHandler.set(connectionHandler);

        gui.submitToExecutorService(() -> {
            if (id == -1) connectionHandler.connect(new ConnectionCommand(nickname));
            else {
                selfUserInfo.set(new UserInfo(nickname, id));
                connectionHandler.reconnect();
            }
        });

        return true;
    }


    /* NETWORK */

    /**
     * Allows to handle a login event.
     *
     * @param userInfo the received UserInfo
     * @param error an eventual error
     */
    @Override
    public void handleLoginEvent(UserInfo userInfo, String error) {
        if (userInfo == null) System.exit(1);

        selfUserInfo.set(userInfo);

        System.out.println("[INFO] Successfully connected to server.");

        if (error != null) System.out.println("[ERROR] " + error);

        System.out.println("[INFO] Changing scene to: Config");
        gui.changeToMenuScene();
    }



    /* GUI */

    /**
     * Handles the pressing of the submit button.
     * If nickname is valid, based on the chosen protocol, it tries to connect to server.
     *
     * @param event the ActionEvent
     */
    private void handleSubmitClick(ActionEvent event) {
        int id = -1;
        String nickname = nicknameTextField.getText();

        if (nickname.length() < 3) {
            System.out.println("[ERROR] Nickname too short: enter at least 3 characters");
            return;
        }

        if (!idTextField.getText().isEmpty()) {
            try {
                id = Integer.parseInt(idTextField.getText());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Id must be an integer");
            }
        }

        try {
            connect(nickname, id, rmiButton.isSelected());
        } catch (Exception e) {
            System.out.println("[ERROR] Connection failed");
        }
    }
}
