package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.client.RMIConnectionHandler;
import it.polimi.ingsw.distributed.client.SocketConnectionHandler;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.view.gui.GUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Handles the initial configuration phase of the client.
 */
public class ConfigController extends Controller {

    @FXML private TextField nicknameTextField;
    @FXML private TextField idTextField;

    @FXML private Button submitButton;
    @FXML private RadioButton rmiButton;
    @FXML private RadioButton socketButton;

    public void initialize(GUI gui) {
        // setup gui
        this.gui = gui;

        // initialize toggle group for radio buttons
        ToggleGroup toggleGroup = new ToggleGroup();
        rmiButton.setToggleGroup(toggleGroup);
        rmiButton.setSelected(true);
        socketButton.setToggleGroup(toggleGroup);

        Platform.runLater(() -> System.out.println("hi"));

        // initialize submit button
        submitButton.setOnAction(this::handleSubmitClick);
    }


    /**
     * Allows to connect to server, creating a ConnectionHandler object.
     *
     * @param nickname nickname chosen by player
     * @param id eventual id for reconnection (null if first connection)
     * @param isRmi true if user chose rmi connection, false for socket
     * @return boolean on whether the operation was successful or not
     */
    public boolean connect(String nickname, Integer id, boolean isRmi) throws Exception {
        if (nickname.length() < 3) return false;

        ConnectionHandler connectionHandler;

        if (isRmi) connectionHandler = new RMIConnectionHandler(gui.mainController);
        else connectionHandler = new SocketConnectionHandler(gui.mainController);

        gui.connectionHandler = connectionHandler;

        if (id == -1) connectionHandler.connect(new ConnectionCommand(nickname));
        else connectionHandler.reconnect();

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
        gui.selfUserInfo = userInfo;

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
        String nickname = nicknameTextField.getText();
        if (nickname.length() < 3) return;

        Integer id;

        try {
            id = Integer.parseInt(idTextField.getText());
        } catch (NumberFormatException e) { id = -1; }

        try {
            connect(nickname, id, rmiButton.isSelected());
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
