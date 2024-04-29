package it.polimi.ingsw.distributed;

public class RMIConnectionInfo extends ConnectionInfo {
    public final String remoteName;

    RMIConnectionInfo(String remoteName) {
        this.remoteName = remoteName;
    }
}
