package it.polimi.ingsw.distributed.commands.server;

import it.polimi.ingsw.controller.server.Server;

public class SignUpCommand extends ServerCommand {

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
