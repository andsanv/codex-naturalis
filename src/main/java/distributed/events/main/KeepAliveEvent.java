
package distributed.events.main;

import view.interfaces.MainEventHandler;

/**
 * This event is used to notify that the connection is still alive.
 */
public class KeepAliveEvent extends MainEvent {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(MainEventHandler mainEventHandler) {
    }
}
