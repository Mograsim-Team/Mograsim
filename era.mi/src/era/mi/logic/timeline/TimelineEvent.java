package era.mi.logic.timeline;

/**
 * A class that stores all relevant information about an event in the {@link Timeline}. Currently, there is not much relevant information to
 * store.
 * 
 * @author Fabian Stemmler
 *
 */
public class TimelineEvent {
	private final long timing;

	TimelineEvent(long timing) {
		super();
		this.timing = timing;
	}

	public long getTiming() {
		return timing;
	}

	public String toString() {
		return "timestamp: " + timing;
	}
}