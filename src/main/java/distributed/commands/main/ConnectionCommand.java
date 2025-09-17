package distributed.commands.main;

/** 
 * This command is used to connect to the server to get a valid UserInfo assigned 
 * This command is to be intended as simply informational.
 * */
public class ConnectionCommand extends MainCommand {

  /** The client's chosen username */
  public final String username;

  /**
   * This constructor creates the command starting from the username.
   * 
   * @param username the chosen username.
   */
  public ConnectionCommand(String username) {
    this.username = username;
  }

  /**
   * This method is not implemented since the server will need more information to connect a player.
   * This command is to be intended as simply informational.
   */
  @Override
  public void execute() {}
}
