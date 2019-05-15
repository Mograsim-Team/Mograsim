package era.mi.logic.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 * Orders Events by the time they are due to be executed. Can execute Events individually.
 * @author Fabian Stemmler
 *
 */
public class Timeline
{
	private PriorityQueue<InnerEvent> events;
	private long currentTime = 0;
	
	private final List<Consumer<TimelineEvent>> eventAddedListener;
	
	public Timeline(int initCapacity)
	{
		events = new PriorityQueue<InnerEvent>(initCapacity, (a, b) -> {
			long difference = a.getTiming() - b.getTiming();
			if(difference == 0)
				return 0;
			return difference < 0 ? -1 : 1;
		});
		
		eventAddedListener = new ArrayList<>();
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
	
	public void executeAll()
	{
		while (hasNext())
			executeNext();
	}
	
	/**
	 * Executes all events up to a given simulation timestamp. The simulation
	 * process can be constrained by a real world timestamp.
	 * 
	 * @param timestamp  the simulation timestamp up to which the events will be
	 *                   processed
	 * @param stopMillis the System.currentTimeMillis() when simulation definitely
	 *                   needs to stop.
	 * @return if it was possible to fulfil the goal in the given real world time.
	 * @author Christian Femers
	 */
	public ExecutionResult executeUpTo(long timestamp, long stopMillis)
	{
		if (events.isEmpty())
		{
			currentTime = timestamp;
			return ExecutionResult.NOTHING_DONE;
		}
		int checkStop = 0;
		InnerEvent first = events.peek();
		while (first != null && first.getTiming() <= timestamp)
		{
			events.remove();
			currentTime = first.getTiming();
			first.run();
			// Don't check after every run
			checkStop = (checkStop + 1) % 10;
			if (checkStop == 0 && System.currentTimeMillis() >= stopMillis)
				return ExecutionResult.RAN_OUT_OF_TIME;
			first = events.peek();
		}
		currentTime = timestamp;
		return ExecutionResult.DONE_IN_TIME;
	}

	public long getSimulationTime()
	{
		return currentTime;
	}

	public long nextEventTime()
	{
		if(!hasNext())
			return -1;
		else
			return events.peek().timing;
	}
	
	public void reset()
	{
		events.clear();
		currentTime = 0;
	}

	public void addEventAddedListener(Consumer<TimelineEvent> listener)
	{
		eventAddedListener.add(listener);
	}
	public void removeEventAddedListener(Consumer<TimelineEvent> listener)
	{
		eventAddedListener.remove(listener);
	}
	
	/**
	 * Adds an Event to the {@link Timeline}
	 * @param function The {@link TimelineEventHandler} that will be executed, when the {@link InnerEvent} occurs on the timeline.
	 * @param relativeTiming The amount of MI ticks in which the {@link InnerEvent} is called, starting from the current time.
	 */
	public void addEvent(TimelineEventHandler function, int relativeTiming)
	{
		long timing = currentTime + relativeTiming;
		TimelineEvent event = new TimelineEvent(timing);
		events.add(new InnerEvent(function, event, timing));
		eventAddedListener.forEach(l -> l.accept(event));
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
		
		@Override
		public String toString()
		{
			return event.toString();
		}
	}
	
	@Override
	public String toString()
	{
		return "simulation time: " + currentTime + ", " + events.toString();
	}
	
	public static long toNanoseconds(long ticks)
	{
		return ticks; //TODO: Alter this when it has been determined how ticks should relate to real time.
	}
	
	public enum ExecutionResult
	{
		NOTHING_DONE, DONE_IN_TIME, RAN_OUT_OF_TIME 
	}
}