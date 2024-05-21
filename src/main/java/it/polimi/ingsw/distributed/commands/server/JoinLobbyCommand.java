package it.polimi.ingsw.distributed.commands.server;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

public class JoinLobbyCommand extends ServerCommand {
    private final UserInfo userInfo;
    private final int lobbyId;

    @Override
    public void execute() {
        Server.INSTANCE.joinLobby(userInfo, lobbyId);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public JoinLobbyCommand(UserInfo userInfo, int lobbyId) {
        this.userInfo = userInfo;
        this.lobbyId = lobbyId;
    }

}
