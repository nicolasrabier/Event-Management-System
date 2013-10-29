/**
 * EventListener interface that specifies the notify method.
 */
public interface EventListener {

	/**
	 * Called by the {@link EventService} to notify the current listener that an event was triggered.
	 * 
	 * @param event
	 *            The event that was triggered.
	 */
	public void notify(Event event);
}