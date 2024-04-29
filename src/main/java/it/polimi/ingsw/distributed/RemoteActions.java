package it.polimi.ingsw.distributed;

import java.rmi.Remote;

import it.polimi.ingsw.distributed.actions.GameActions;
import it.polimi.ingsw.distributed.actions.ListenerActions;
import it.polimi.ingsw.distributed.actions.ServerActions;

public interface RemoteActions extends ServerActions, GameActions, ListenerActions, Remote {
}
