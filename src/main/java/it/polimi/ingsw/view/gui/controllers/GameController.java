package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.gui.cache.ActionCache;
import it.polimi.ingsw.view.gui.cache.PlayCardAction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {

    public String sideSelected = "fronts";
    public String cardSelectedPath;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox playerHand;

    @FXML
    private GridPane gridPane;

    @FXML
    private VBox playerListPane;

    @FXML
    private VBox decksPane;

    @FXML
    private HBox itemsPane;

    @FXML
    private VBox secObjPane;

    @FXML
    private VBox commObjPane;

    @FXML
    private Button startButton;

    @FXML
    private AnchorPane anchorPane;


    private Scale scaleTransform;
    private double scaleValue = 1;
    private final double scaleIncrement = 0.1;

    private static final int numRows = 81;
    private static final int numColumns = 81;
    private static final double cellWidth = 387.5;
    private static final double cellHeight = 199.0;
    private static final double additionalWidth = 100.0;
    private static final double additionalHeight = 100.0;
    private static final double gridScale = 0.2;

    @FXML
    public void initialize() throws IOException {
        initializeFXML();
        initializeGridCells();
        initializeScrollPane();
        initializeScaleTransform();
        initializePlayerHand();
        initializeGridPane();
        initializePlayerListPane();
        initializeSceneKeyEvents();
        initializeCss();
    }

    private void initializeFXML() throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/gui/gameView.fxml"));
    }

    public void initializeGridCells() {
        // Create and set row constraints for the gridPane
        for (int i = 0; i < numRows; i++) { // 81 rows
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(199);
            rowConstraints.setMinHeight(199);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.NEVER);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Create and set column constraints for the gridPane
        for (int i = 0; i < numColumns; i++) { // 81 columns
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(387.5);
            columnConstraints.setMinWidth(387.5);
            columnConstraints.setHgrow(javafx.scene.layout.Priority.NEVER);
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        // Compute and set the size of the gridPane
        double gridWidth = numColumns * cellWidth;
        double gridHeight = numRows * cellHeight;
        gridPane.setPrefSize(gridWidth, gridHeight);
        gridPane.setMaxSize(gridWidth, gridHeight);
        gridPane.setMinSize(gridWidth, gridHeight);

        // Set grid lines visible
        gridPane.setGridLinesVisible(true);

        // Set the scale of the gridPane
        gridPane.setScaleX(gridScale);
        gridPane.setScaleY(gridScale);

        // Set the size of the stackPane to be slightly larger than the gridPane
        double stackWidth = gridWidth * gridScale + additionalWidth;
        double stackHeight = gridHeight * gridScale + additionalHeight;
        stackPane.setPrefSize(stackWidth, stackHeight);
        stackPane.setMaxSize(stackWidth, stackHeight);
        stackPane.setMinSize(stackWidth, stackHeight);
    }

    public static int[] coordsToCells(int row, int col) {
        int center = (numRows - 1) / 2;
        int translatedX = center + row;
        int translatedY = center - col;
        return new int[]{translatedX, translatedY};
    }

    public static int[] cellsToCoords(int x, int y) {
        //todo fix with example
        int center = (numRows - 1) / 2;
        int originalCol = x + center;
        int originalRow = center - y;
        return new int[]{originalRow, originalCol};
    }

    private void initializeScrollPane() {
        //todo fix doesnt work
        scrollPane.prefViewportWidthProperty().bind(gridPane.widthProperty());
        scrollPane.prefViewportHeightProperty().bind(gridPane.heightProperty());
        StackPane.setAlignment(scrollPane, Pos.CENTER);

    }

    private void initializeScaleTransform() {
        scaleTransform = new Scale(scaleValue, scaleValue);
        stackPane.getTransforms().add(scaleTransform);
    }

    private void initializePlayerHand() {
        for (Node node : playerHand.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackCard = (StackPane) node;
                stackCard.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }
    }

    private void initializeGridPane() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                StackPane cell = createGridCell(i, j);
                gridPane.add(cell, i, j);
                GridPane.setHalignment(cell, HPos.CENTER);
                GridPane.setValignment(cell, VPos.CENTER);
                GridPane.setHgrow(cell, Priority.NEVER);
                GridPane.setVgrow(cell, Priority.NEVER);
            }
        }

        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                setGridCellEventHandlers(node);
            }
        }
    }

    private StackPane createGridCell(int i, int j) {
        StackPane cell = new StackPane();
        if ((i + j) % 2 == 0) {
            ImageView imageView = new ImageView();
            imageView.setFitHeight(331);
            imageView.setFitWidth(496.5);
            imageView.setPreserveRatio(true);
            cell.getChildren().add(imageView);
        }
        return cell;
    }

    private void setGridCellEventHandlers(Node node) {
        node.setOnMouseEntered(this::boardCellHoverEnter);
        node.setOnMouseExited(this::boardCellHoverExit);
        if (!((StackPane) node).getChildren().isEmpty() && ((StackPane) node).getChildren().get(0) instanceof ImageView) {
            node.setOnMouseClicked(this::setCardOnClick);
        }
    }

    private void initializePlayerListPane() {
        for (int i = 0; i < 4; i++) {
            HBox playerPane = createPlayerPane(i);
            playerListPane.getChildren().add(playerPane);
        }
        playerListPane.setSpacing(15);
    }

    private HBox createPlayerPane(int i) {
        HBox playerPane = new HBox();
        ImageView token = createTokenImageView(i);
        ImageView view = createViewIconImageView();
        Text nickname = new Text("Player " + i);
        Text score = new Text(0 + " pts");
        Button button = new Button();
        button.setGraphic(view);
        button.setOnAction(this::handleButtonViewClick);
        nickname.setFont(javafx.scene.text.Font.font("System", 28));
        score.setFont(javafx.scene.text.Font.font("System", 28));
        playerPane.setSpacing(20);
        playerPane.setAlignment(Pos.CENTER);
        playerPane.setPadding(new Insets(10));
        playerPane.getChildren().addAll(token, nickname, score, button);

        return playerPane;
    }


    private ImageView createTokenImageView(int i) {
        ImageView token = new ImageView();
        token.setFitHeight(40);
        token.setFitWidth(40);
        token.setPreserveRatio(true);
        switch (i) {
            case 0:
                token.setImage(new Image("/images/tokens/token_blue.png"));
                break;
            case 1:
                token.setImage(new Image("/images/tokens/token_green.png"));
                break;
            case 2:
                token.setImage(new Image("/images/tokens/token_yellow.png"));
                break;
            case 3:
                token.setImage(new Image("/images/tokens/token_red.png"));
                break;
            default:
                break;
        }
        return token;
    }

    private ImageView createViewIconImageView() {
        ImageView view = new ImageView();
        view.setFitHeight(40);
        view.setFitWidth(40);
        view.setPreserveRatio(true);
        view.setImage(new Image("/images/icons/view_icon.png"));
        return view;
    }

    private void initializeSceneKeyEvents() {
        Scene scene = scrollPane.getScene();
        if (scene != null) {
            setupKeyEvents(scene);
        } else {
            scrollPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    setupKeyEvents(newScene);
                }
            });
        }
    }

    private void initializeCss() {
        itemsPane.getStyleClass().add("itemsPane");
        playerListPane.getStyleClass().add("playerListPane");
        playerHand.getStyleClass().add("playerHand");
        decksPane.getStyleClass().add("decksPane");
        secObjPane.getStyleClass().add("secObjPane");
        commObjPane.getStyleClass().add("commObjPane");
    }

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case PLUS:
                case EQUALS:
                    zoomIn();
                    break;
                case MINUS:
                    zoomOut();
                    break;
                default:
                    break;
            }
        });
    }

    private void zoomIn() {
        scaleValue += scaleIncrement;
        applyScale();
    }

    private void zoomOut() {
        scaleValue -= scaleIncrement;
        applyScale();
    }

    private void applyScale() {
        //todo: fix
        double oldHValue = scrollPane.getHvalue();
        double oldVValue = scrollPane.getVvalue();

        scaleTransform.setX(scaleValue);
        scaleTransform.setY(scaleValue);

        double contentWidth = gridPane.getBoundsInLocal().getWidth();
        double contentHeight = gridPane.getBoundsInLocal().getHeight();

        double newHValue = (oldHValue + 0.5 * scrollPane.getViewportBounds().getWidth() / contentWidth) *
                (contentWidth - scrollPane.getViewportBounds().getWidth()) /
                (contentWidth - scaleValue * scrollPane.getViewportBounds().getWidth());
        double newVValue = (oldVValue + 0.5 * scrollPane.getViewportBounds().getHeight() / contentHeight) *
                (contentHeight - scrollPane.getViewportBounds().getHeight()) /
                (contentHeight - scaleValue * scrollPane.getViewportBounds().getHeight());

        scrollPane.setHvalue(newHValue);
        scrollPane.setVvalue(newVValue);
    }


    //Handles the click of view icon in player list pane
    private void handleButtonViewClick(ActionEvent actionEvent) {
        secObjPane.setVisible(false);
        playerListPane.setVisible(false);
        playerHand.setVisible(false);
        decksPane.setVisible(false);
        setGridCells();
        setupBackButton();
    }

    //todo add player id or token, something
    private void setGridCells() {
        gridPane.getChildren().clear();
        setBoardOnGrid(createMockCache());
    }

    private void setBoardOnGrid(ActionCache cache) {
        for (int i = 0; i < cache.size(); i++) {
            setCellImageFromAction(cache.getAction(i));
        }
    }

    private void setCellImageFromAction(PlayCardAction action) {
        String side = action.getCardSide().equals(CardSide.FRONT) ? "fronts" : "backs";
        String id = "" + (action.getCardSide().equals(CardSide.FRONT) ? action.getCardId() : getBackCardId(action.getCardId()));
        String path = "/images/cards/" + side + "/" + id + ".png";
        int x = action.getCoords().x;
        int y = action.getCoords().y;
        int row = coordsToCells(x,y)[0];
        int col = coordsToCells(x,y)[1];
        Image image = new Image(path);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(331);
        imageView.setFitWidth(496.5);
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        gridPane.add(imageView, col, row);
    }

    private void setupBackButton() {
        Button backButton = new Button("Back");
        anchorPane.getChildren().add(backButton);
        AnchorPane.setLeftAnchor(backButton, 10.0);
        AnchorPane.setBottomAnchor(backButton, 10.0);
        backButton.setOnMouseClicked(this::backButtonClickHandler);
    }

    public void backButtonClickHandler(MouseEvent mouseEvent) {
        secObjPane.setVisible(true);
        playerListPane.setVisible(true);
        playerHand.setVisible(true);
        decksPane.setVisible(true);
        returnToNormalView();
    }

    private void returnToNormalView() {
        //todo add cache for current client player
        initializeGridCells();
    }

    @FXML
    public void selectCardOnClick(MouseEvent mouseEvent) {
        StackPane stackPane = (StackPane) mouseEvent.getSource();
        ImageView imageView = (ImageView) stackPane.getChildren().get(0);
        String imagePath = imageView.getImage().getUrl();
        cardSelectedPath = imagePath.substring(imagePath.indexOf("/images"));
    }

    @FXML
    public void flipHandCard(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        StackPane stackPane = (StackPane) ((VBox) button.getParent()).getChildren().get(1);
        ImageView imageView = (ImageView) stackPane.getChildren().get(0);

        String filepath = imageView.getImage().getUrl();
        filepath = filepath.contains("fronts") ? filepath.replace("fronts", "backs") : filepath.replace("backs", "fronts");

        cardSelectedPath = filepath;
        imageView.setImage(new Image(cardSelectedPath));
    }

    @FXML
    public void handCardHoverEnter(MouseEvent mouseEvent) {
        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: yellow; -fx-border-width: 10;");
    }

    @FXML
    public void handCardHoverExit(MouseEvent mouseEvent) {
        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
    }

    @FXML
    public void boardCellHoverEnter(MouseEvent mouseEvent) {
        StackPane stackPane = (StackPane) mouseEvent.getSource();
        int row = GridPane.getRowIndex(stackPane);
        int col = GridPane.getColumnIndex(stackPane);

        stackPane.setStyle("-fx-border-color: " + ((row + col) % 2 == 0 ? "blue" : "red") + "; -fx-border-width: 2;");
    }

    @FXML
    public void boardCellHoverExit(MouseEvent mouseEvent) {
        StackPane stackPane = (StackPane) mouseEvent.getSource();
        stackPane.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
    }

    @FXML
    public void setCardOnClick(MouseEvent mouseEvent) {
        bringStackPaneToFront(gridPane, (StackPane) mouseEvent.getSource());
        ImageView imageView = (ImageView) (((StackPane) mouseEvent.getSource()).getChildren().get(0));
        imageView.setImage(new Image(cardSelectedPath));
    }

    @FXML
    private void openPopupButton(MouseEvent mouseEvent) throws IOException {
        //Load popup starter stage
        try {
            FXMLLoader popupStarterLoader = new FXMLLoader(getClass().getResource("/view/gui/popupStarter.fxml"));
            Parent popupStarterContent = popupStarterLoader.load();

            Stage popupStarterStage = new Stage();
            popupStarterStage.setTitle("Popup Starter Window");
            popupStarterStage.setScene(new Scene(popupStarterContent));
            popupStarterStage.initOwner((Stage) startButton.getScene().getWindow());  // Set the owner to the main stage
            popupStarterStage.initModality(Modality.WINDOW_MODAL);  // Make the popup modal
            popupStarterStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void bringStackPaneToFront(GridPane gridPane, StackPane stackPaneToFront) {
        ObservableList<Node> children = gridPane.getChildren();
        if (children.contains(stackPaneToFront)) {
            children.remove(stackPaneToFront);
            children.add(stackPaneToFront);
        } else {
            System.out.println("The specified StackPane is not a child of the GridPane.");
        }
    }

    private String getBackCardId(int id) {
        if (0 <=id && id < 10) {
            return "0";
        } else if (10 <=id && id < 20) {
            return "10";
        }
        else if (20 <=id && id < 30) {
            return "20";
        }
        else if (30 <=id && id < 40) {
            return "30";
        }
        else if (40 <=id && id < 50) {
            return "40";
        }
        else if (50 <=id && id < 65) {
            return "50";
        }
        else if (65 <=id && id < 72) {
            return "65";
        }
        else if (72 <=id && id < 80) {
            return "72";
        }
        else if ( 80 <=id && id < 86) {
            return  "" +  id;
        }
        else if ( 86 <=id && id < 98) {
            return "86";
        }
        else if ( 98 <=id && id <= 101) {
            return "98";
        }
        else {
            return null;
        }
    }

    private ActionCache createMockCache() {
        ActionCache actionCache = new ActionCache();
        PlayerToken playerToken = PlayerToken.BLUE;

        PlayCardAction action1 = new PlayCardAction(playerToken, new Coords(0,0), 12, CardSide.FRONT);
        PlayCardAction action2 = new PlayCardAction(playerToken, new Coords(1,1), 34, CardSide.FRONT);
        PlayCardAction action3 = new PlayCardAction(playerToken, new Coords(2, 2), 56, CardSide.BACK);
        PlayCardAction action4 = new PlayCardAction(playerToken, new Coords(3,1), 1, CardSide.FRONT);
        PlayCardAction action5 = new PlayCardAction(playerToken, new Coords(0,2), 23, CardSide.BACK);

        actionCache.addAction(action1);
        actionCache.addAction(action2);
        actionCache.addAction(action3);
        actionCache.addAction(action4);
        actionCache.addAction(action5);

        return actionCache;
    }

    private ActionCache createMockCache2() {
        ActionCache actionCache = new ActionCache();
        PlayerToken playerToken = PlayerToken.BLUE;

        PlayCardAction action1 = new PlayCardAction(playerToken, new Coords(0,0), 12, CardSide.FRONT);
        PlayCardAction action2 = new PlayCardAction(playerToken, new Coords(-1,-1), 34, CardSide.FRONT);
        PlayCardAction action3 = new PlayCardAction(playerToken, new Coords(1, 1), 56, CardSide.BACK);
        PlayCardAction action4 = new PlayCardAction(playerToken, new Coords(2,2), 1, CardSide.FRONT);
        PlayCardAction action5 = new PlayCardAction(playerToken, new Coords(0,2), 23, CardSide.BACK);

        actionCache.addAction(action1);
        actionCache.addAction(action2);
        actionCache.addAction(action3);
        actionCache.addAction(action4);
        actionCache.addAction(action5);

        return actionCache;
    }


}
