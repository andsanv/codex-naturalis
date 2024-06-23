package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.distributed.commands.Command;

/**
 * This abstract class represents a main command.
 * Main commands are sent from the client to the server while in the main menu.
 * They represents user actions in the main menu.
  */
public abstract class MainCommand extends Command {
  
  /**
   * This method is exectuted on the server side, propagating the request to the server controller.
   */
  public abstract void execute();
}
