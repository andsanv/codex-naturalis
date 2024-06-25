package it.polimi.ingsw.distributed.events.main;

import java.util.Map;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * This event is used to notify that the user is reconnecting to a game.
 * It constains the slim model of the game as its necessary to recreate the game
 * state.
 */
public class ReconnectToGameEvent extends MainEvent {
    /**
     * The slim game model
     */
    public final SlimGameModel slimModel;

    /**
     * Mapping from UserInfo to PlayerToken
     */
    public final Map<UserInfo, PlayerToken> userToToken;

    /**
     * This constructor creates the event starting from the slim model of the game.
     * 
     * @param slimModel the slim model of the game
     * @param userToToken mapping from UserInfo to the respective PlayerToken
     */
    public ReconnectToGameEvent(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken) {
        this.slimModel = slimModel;
        this.userToToken = userToToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReconnetionToGame(slimModel, userToToken);
    }
}
