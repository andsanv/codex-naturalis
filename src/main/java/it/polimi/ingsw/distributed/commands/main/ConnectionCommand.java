package it.polimi.ingsw.distributed.commands.main;

public class ConnectionCommand extends MainCommand {

  public final String username;

  @Override
  public void execute() {}

  public ConnectionCommand(String username) {
    this.username = username;
  }
}
