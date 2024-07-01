package it.polimi.ingsw.view.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import javax.swing.*;

public class tempController {
    @FXML private Button sendMessageButton;
    @FXML private VBox messagesVBox;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private TextField messageTextField;

    public void initialize() {
        messagesVBox.setAlignment(Pos.TOP_CENTER);

        sendMessageButton.setOnAction(this::sendMessage);

        receiveMessage();
    }

    public void sendMessage(ActionEvent event) {
        if (messageTextField.getText().isEmpty()) return;
        Text text = new Text(messageTextField.getText());
        TextFlow textFlow = new TextFlow(text);
        messageTextField.clear();

        textFlow.setMaxWidth(160);
        textFlow.setTextAlignment(TextAlignment.RIGHT);
        textFlow.getStyleClass().add("single-message");
        VBox.setMargin(textFlow, new Insets(0, 10, 0, 80));
        textFlow.setPadding(new Insets(3, 10, 3, 10));

        messagesVBox.getChildren().add(textFlow);
        messagesScrollPane.layout();
        messagesScrollPane.setVvalue(messagesScrollPane.getVmax());
    }

    public void receiveMessage() {
        Text text = new Text("test message");
        TextFlow textFlow = new TextFlow(new Text("ao"));

        textFlow.setMaxWidth(160);
        textFlow.setTextAlignment(TextAlignment.LEFT);
        textFlow.getStyleClass().add("single-message");
        VBox.setMargin(textFlow, new Insets(10, 80, 0, 10));
        textFlow.setPadding(new Insets(3, 10, 3, 10));

        messagesVBox.getChildren().add(textFlow);

        updateChatScrollPane();
    }

    public void updateChatScrollPane() {
        messagesScrollPane.layout();
        messagesScrollPane.setVvalue(messagesScrollPane.getVmax());
    }
}
