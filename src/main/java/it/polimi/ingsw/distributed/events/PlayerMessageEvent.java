package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.client.ClientCache;

public class PlayerMessageEvent extends Event {
    public final String sender;
    public final String content;

    public PlayerMessageEvent(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public void execute(ClientCache clientCache) {
        clientCache.addDirectMessage(sender, content);
    }
}
