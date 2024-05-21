package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

public class MainErrorEvent extends MainEvent {
    private final String message;

    public MainErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleServerError(message);
    }
}