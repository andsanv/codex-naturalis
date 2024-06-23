package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.Command;

/**
 * This abstract class represents a game command.
 * Game commands are sent from the client to the server during the game phases.
 * They represents user actions in the game.
 */
public abstract class GameCommand extends Command {

  /**
   * This method executes the command on the server model.
   * 
   * @param gameFlowManager the game flow manager to propagate the command to.
   * @return true if the command is executed correctly, false otherwise.
   */
  public abstract boolean execute(GameFlowManager gameFlowManager);
}
