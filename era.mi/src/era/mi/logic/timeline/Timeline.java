package era.mi.logic.timeline;

import java.util.PriorityQueue;

/**
 * Orders Events by the time they are due to be executed. Can execute Events individually.
 * @author Fabian Stemmler
 *
 */
public class Timeline
{
	private PriorityQueue<InnerEvent> events;
	private long currentTime = 0;
	
	public Timeline(int initCapacity)
	{
		events = new PriorityQueue<InnerEvent>(initCapacity, (a, b) -> {
			//Is this really necessary? If only ints are allowed as relative timing, the difference should always be an int
			long difference = a.getTiming() - b.getTiming();
			if(difference == 0)
				return 0;
			return difference < 0 ? -1 : 1;
		});
	}
	
	public boolean hasNext()
	{
		return !events.isEmpty();
	}

	public void executeNext()
	{
		InnerEvent first = events.poll();
		currentTime = first.getTiming();
		first.run();
	}

	public long getSimulationTime()
	{
		return currentTime;
	}
	
	public void reset()
	{
		events.clear();
		currentTime = 0;
	}
	
	/**
	 * Adds an Event to the {@link Timeline}
	 * @param function The {@link TimelineEventHandler} that will be executed, when the {@link InnerEvent} occurs on the timeline.
	 * @param relativeTiming The amount of MI ticks in which the {@link InnerEvent} is called, starting from the current time.
	 */
	public void addEvent(TimelineEventHandler function, int relativeTiming)
	{
		long timing = currentTime + relativeTiming;
		events.add(new InnerEvent(function, new TimelineEvent(timing), timing));
	}
	
	private class InnerEvent
	{

		private final long timing;
		private final TimelineEventHandler function;
		private final TimelineEvent event;
		
		/**
		 * Creates an {@link InnerEvent}
		 * @param function {@link TimelineEventHandler} to be executed when the {@link InnerEvent} occurs
		 * @param timing Point in the MI simulation {@link Timeline}, at which the {@link InnerEvent} is executed;
		 */
		InnerEvent(TimelineEventHandler function, TimelineEvent event, long timing)
		{
			this.function = function;
			this.event = event;
			this.timing = timing;
		}

		public long getTiming()
		{
			return timing;
		}
		
		public void run()
		{
			function.handle(event);
		}
		
	}
}