package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tempController {
    @FXML private AnchorPane chatAnchorPane;
    @FXML private Button sendMessageButton;
    @FXML private VBox globalMessagesVBox;
    @FXML private TextField messageTextField;

    @FXML private ScrollPane currentChatScrollPane;
    private ScrollPane globalChatScrollPane;
    @FXML Button globalChatButton;
    private Map<UserInfo, ScrollPane> userToChatScrollPane = new HashMap<>();


    @FXML private VBox chatPlayersVBox;

    public void initialize() {
        globalMessagesVBox.setAlignment(Pos.TOP_CENTER);

        sendMessageButton.setOnAction(this::sendMessage);

        initializeChat();

        // receiveMessage();
    }

    public void initializeChat() {
        globalChatScrollPane = currentChatScrollPane;
        globalChatButton.setOnAction(actionEvent -> {
            currentChatScrollPane = globalChatScrollPane;
            chatAnchorPane.getChildren().set(0, currentChatScrollPane);
        });

        List<UserInfo> players = new ArrayList<>();
        UserInfo self = new UserInfo(new User("Andrea"));
        players.add(self);
        players.add(new UserInfo(new User("Maradona")));
        players.add(new UserInfo(new User("Giulio")));

        players.stream().filter(player -> player != self).forEach(player -> {
            ScrollPane scrollPane = new ScrollPane();
            AnchorPane.setTopAnchor(scrollPane, 10.0);
            AnchorPane.setLeftAnchor(scrollPane, 10.0);
            AnchorPane.setBottomAnchor(scrollPane, 235.0);
            AnchorPane.setLeftAnchor(scrollPane, 10.0);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox vbox = new VBox();
            vbox.setPrefWidth(254);
            vbox.setPrefHeight(530);
            vbox.setSpacing(5);
            Pane pane = new Pane();
            pane.setPrefHeight(5);
            vbox.getChildren().add(pane);
            scrollPane.setContent(vbox);

            System.out.println(player + ", " + scrollPane);
            userToChatScrollPane.put(player, scrollPane);

            StackPane stackPane = new StackPane();
            stackPane.setPrefHeight(185);
            Button button = new Button();
            button.setText(player.name);
            button.setOnAction(actionEvent -> {
                currentChatScrollPane = userToChatScrollPane.get(player);
                chatAnchorPane.getChildren().set(0, currentChatScrollPane);
            });
            button.setMaxHeight(Double.MAX_VALUE);
            button.setMaxWidth(Double.MAX_VALUE);
            stackPane.getChildren().add(button);

            chatPlayersVBox.getChildren().add(stackPane);
        });
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

        ((VBox) currentChatScrollPane.getContent()).getChildren().add(textFlow);
        currentChatScrollPane.layout();
        currentChatScrollPane.setVvalue(currentChatScrollPane.getVmax());

        if (currentChatScrollPane == globalChatScrollPane) System.out.println("global message");
        else System.out.println("dm message");
    }

    public void receiveMessage() {
        Text text = new Text("test message");
        TextFlow textFlow = new TextFlow(new Text("ao"));

        textFlow.setMaxWidth(160);
        textFlow.setTextAlignment(TextAlignment.LEFT);
        textFlow.getStyleClass().add("single-message");
        VBox.setMargin(textFlow, new Insets(10, 80, 0, 10));
        textFlow.setPadding(new Insets(3, 10, 3, 10));

        globalMessagesVBox.getChildren().add(textFlow);

        updateChatScrollPane();
    }

    public void updateChatScrollPane() {
        currentChatScrollPane.layout();
        currentChatScrollPane.setVvalue(currentChatScrollPane.getVmax());
    }
}
