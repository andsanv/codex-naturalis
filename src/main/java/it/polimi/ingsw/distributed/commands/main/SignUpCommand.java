package it.polimi.ingsw.distributed.commands.main;

import it.polimi.ingsw.controller.server.Server;

public class SignUpCommand extends MainCommand {

  private final String name;

  @Override
  public void execute() {
    Server.INSTANCE.signup(name);
  }

  public String getName() {
    return name;
  }

  public SignUpCommand(String name) {
    this.name = name;
  }
}
