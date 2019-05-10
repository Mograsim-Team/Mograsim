package era.mi.logic.timeline;

public class TimelineEvent
{
	private final long timing;
	
	TimelineEvent(long timing)
	{
		super();
		this.timing = timing;
	}

	public long getTiming()
	{
		return timing;
	}
}