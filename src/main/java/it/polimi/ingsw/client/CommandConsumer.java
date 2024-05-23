package it.polimi.ingsw.client;

import it.polimi.ingsw.distributed.commands.GameCommand;
import it.polimi.ingsw.distributed.commands.ServerCommand;

import java.rmi.Remote;
import java.util.concurrent.BlockingQueue;

public class CommandConsumer<Command> implements Runnable {
    private final BlockingQueue<Command> commandQueue;
    private final ConnectionHandler connectionHandler;

    public CommandConsumer(BlockingQueue<Command> queue, ConnectionHandler connectionHandler) {
        this.commandQueue = queue;
        this.connectionHandler = connectionHandler;
    }

    public void run() {
        try {
            synchronized (commandQueue) {
                while (!commandQueue.isEmpty()) {
                    consume(commandQueue.take());
                }
                commandQueue.wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    void consume(Command command) {
        if(command instanceof ServerCommand) connectionHandler.sendToMainServer((ServerCommand) command);
        else connectionHandler.sendToGameServer((GameCommand) command);
    }
}