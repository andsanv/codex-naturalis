package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;

public class GameView {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView mapView;

    private double scaleValue = 1.0;
    private final double SCALE_DELTA = 1.1;

    @FXML
    private void initialize() {
        mapView.addEventFilter(ScrollEvent.ANY, this::handleZoom);
    }


    private void handleZoom(ScrollEvent event) {

        if (event.getDeltaY() == 0) {
            return;
        }

        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
        double oldScale = scaleValue;
        scaleValue *= scaleFactor;
        mapView.setScaleX(scaleValue);
        mapView.setScaleY(scaleValue);

        double f = (scaleFactor - 1);

        // Calculate adjustment to keep the map centered
        double mouseX = event.getX();
        double mouseY = event.getY();
        double deltaX = mouseX - (scrollPane.getViewportBounds().getWidth() / 2);
        double deltaY = mouseY - (scrollPane.getViewportBounds().getHeight() / 2);
        double extraWidth = scrollPane.getContent().getLayoutBounds().getWidth() - scrollPane.getViewportBounds().getWidth();
        double extraHeight = scrollPane.getContent().getLayoutBounds().getHeight() - scrollPane.getViewportBounds().getHeight();

        // Prevent over-scrolling
        double newHValue = scrollPane.getHvalue() + deltaX / extraWidth;
        double newVValue = scrollPane.getVvalue() + deltaY / extraHeight;
        scrollPane.setHvalue(Math.min(Math.max(newHValue, 0), 1));
        scrollPane.setVvalue(Math.min(Math.max(newVValue, 0), 1));

        event.consume();
    }
}
