
public class FinancialEvent implements ManagementEvent {

	public static enum FinancialLevel {
		ABOVE_100k, ABOVE_500K, ABOVE_1M, ABOVE_1M500K;
	}

	private final FinancialLevel level;

	public FinancialEvent(FinancialLevel level) {
		this.level = level;
	}

	public FinancialLevel getLevel() {
		return level;
	}

}