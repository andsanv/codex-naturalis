package distributed.commands.main;

import controller.usermanagement.UserInfo;

/** 
 * This command is used to reconnect to the server. 
 *  This command is to be intended as simply informational.
*/
public class ReconnectionCommand extends MainCommand {

  /** The client's user info */
  public final UserInfo userInfo;

  /** The last event id */
  public final int lastEventId;

  /**
   * This constructor creates the command starting from the user info.
   * This is used when the client cannot retrieve the last received event id.
   * 
   * @param userInfo the user info.
   */
  public ReconnectionCommand(UserInfo userInfo) {
    this.userInfo = userInfo;
    this.lastEventId = -1;
  }

  /**
   * This constructor creates the command starting from the user info and the last event id.
   * 
   * @param userInfo
   * @param lastEventId
   */
  public ReconnectionCommand(UserInfo userInfo, int lastEventId) {
    this.userInfo = userInfo;
    this.lastEventId = lastEventId;
  }

  /**
   * This method is not implemented since the server will need more information to reconnect a player.
   * This command is to be intended as simply informational.
   */
  @Override
  public void execute() {}
}
