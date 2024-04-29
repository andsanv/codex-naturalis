package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;

public interface RemoteActions extends ServerActions, GameActions, ListenerActions, Remote {
}
