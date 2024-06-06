package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.commands.Command;

/**
 * Every move by the clients is represented through a command, using the command pattern
 */
public abstract class GameCommand extends Command {
  public abstract boolean execute(GameFlowManager gameFlowManager);
}
