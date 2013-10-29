
public class SecurityEvent implements ManagementEvent {

	public static enum SecurityLevel {
		UNAUTHORIZED_ACCESS, AUTHORIZATION_FAILED, AUTHORIZATION_TRIGGERED;
	}

	private final SecurityLevel level;

	public SecurityEvent(SecurityLevel level) {
		this.level = level;
	}

	public SecurityLevel getLevel() {
		return level;
	}

}