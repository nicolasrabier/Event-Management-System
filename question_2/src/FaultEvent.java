
public class FaultEvent implements ManagementEvent {

	public static enum EventLevel {
		LOW, MODERATE, CRITICAL;
	}

	private final EventLevel level;

	public FaultEvent(EventLevel level) {
		this.level = level;
	}

	public EventLevel getEventLevel() {
		return level;
	}

}