package distributed.client.rmi;

import distributed.client.ConnectionHandler;
import distributed.commands.game.GameCommand;
import distributed.commands.main.MainCommand;

import java.util.concurrent.BlockingQueue;

/**
 * This class is a consumer of the command queues in the RMIConnectionHandler
 * 
 * @param <Command> the type of the command to be consumed.
 */
public class CommandConsumer<Command> implements Runnable {

  /**
   * This is the queue of the commands to be consumed.
   */
  private final BlockingQueue<Command> commandQueue;

  /**
   * This a reference of the connection handler to consume the commands.
   */
  private final ConnectionHandler connectionHandler;

  public CommandConsumer(BlockingQueue<Command> queue, ConnectionHandler connectionHandler) {
    this.commandQueue = queue;
    this.connectionHandler = connectionHandler;
  }

  /**
   * This method consumes the commands in the queue while it is not empty.
   */
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

  /**
   * The consume function simply is responsible for sending the commands.
   * @param command the command to be sent
   */
  void consume(Command command) {
    if (command instanceof MainCommand) connectionHandler.sendToMainServer((MainCommand) command);
    else connectionHandler.sendToGameServer((GameCommand) command);
  }
}
