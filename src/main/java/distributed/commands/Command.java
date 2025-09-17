package distributed.commands;

import java.io.Serializable;

/**
 * This abstract class represents a command.
 * The destination of the commands is the server.
 * The commands represent client actions.
 * 
 * This class implements the Serializable interface to allow the commands to be
 * sent over the network.
 */
public abstract class Command implements Serializable {
}
