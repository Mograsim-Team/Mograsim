package era.mi.logic.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * Orders Events by the time they are due to be executed. Can execute Events individually.
 * 
 * @author Fabian Stemmler
 *
 */
public class Timeline
{
	private PriorityQueue<InnerEvent> events;
	private LongSupplier time;
	private long lastTimeUpdated = 0;

	private final List<Consumer<TimelineEvent>> eventAddedListener;

	public Timeline(int initCapacity)
	{
		events = new PriorityQueue<InnerEvent>(initCapacity);

		eventAddedListener = new ArrayList<>();
		time = () -> lastTimeUpdated;
	}

	/**
	 * @param timestamp exclusive
	 * @return true if the first event is later than the timestamp
	 */
	public BooleanSupplier laterThan(long timestamp)
	{
		return () -> timeCmp(events.peek().getTiming(), timestamp) > 0;
	}

	public boolean hasNext()
	{
		return !events.isEmpty();
	}

	/**
	 * Executes all events at the next timestamp, at which there are any
	 */
	public void executeNext()
	{
		InnerEvent first = events.peek();
		if (first != null)
			executeUntil(laterThan(first.getTiming()), -1);
	}

	public void executeAll()
	{
		while (hasNext())
			executeNext();
	}

	/**
	 * Executes all events until a given condition is met. The simulation process can be constrained by a real world timestamp.
	 * 
	 * @param condition  the condition until which the events are be processed
	 * @param stopMillis the System.currentTimeMillis() when simulation definitely needs to stop. A value of -1 means no timeout.
	 * @return State of the event execution
	 * @formatter:off
	 * <code>NOTHING_DONE</code> if the {@link Timeline} was already empty
	 * <code>EXEC_OUT_OF_TIME</code> if the given maximum time was reached
	 * <code>EXEC_UNTIL_CONDITION</code> if the condition was met
	 * <code>EXEC_UNTIL_EMPTY</code> if events were executed until the {@link Timeline} was empty
	 * @formatter:on
	 * @author Christian Femers, Fabian Stemmler
	 */
	public ExecutionResult executeUntil(BooleanSupplier condition, long stopMillis)
	{
		if (events.isEmpty())
		{
			lastTimeUpdated = getSimulationTime();
			return ExecutionResult.NOTHING_DONE;
		}
		int checkStop = 0;
		InnerEvent first = events.peek();
		while (hasNext() && !condition.getAsBoolean())
		{
			events.remove();
			lastTimeUpdated = first.getTiming();
			first.run();
			// Don't check after every run
			checkStop = (checkStop + 1) % 10;
			if (checkStop == 0 && System.currentTimeMillis() >= stopMillis)
				return ExecutionResult.EXEC_OUT_OF_TIME;
			first = events.peek();
		}
		lastTimeUpdated = getSimulationTime();
		return hasNext() ? ExecutionResult.EXEC_UNTIL_EMPTY : ExecutionResult.EXEC_UNTIL_CONDITION;
	}

	public void setTimeFunction(LongSupplier time)
	{
		this.time = time;
	}

	public long getSimulationTime()
	{
		return time.getAsLong();
	}

	public long nextEventTime()
	{
		if (!hasNext())
			return -1;
		return events.peek().getTiming();
	}

	public void reset()
	{
		events.clear();
		lastTimeUpdated = 0;
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
	 * 
	 * @param function       The {@link TimelineEventHandler} that will be executed, when the {@link InnerEvent} occurs on the timeline.
	 * @param relativeTiming The amount of MI ticks in which the {@link InnerEvent} is called, starting from the current time.
	 */
	public void addEvent(TimelineEventHandler function, int relativeTiming)
	{
		long timing = getSimulationTime() + relativeTiming;
		TimelineEvent event = new TimelineEvent(timing);
		events.add(new InnerEvent(function, event));
		eventAddedListener.forEach(l -> l.accept(event));
	}

	private class InnerEvent implements Runnable, Comparable<InnerEvent>
	{
		private final TimelineEventHandler function;
		private final TimelineEvent event;

		/**
		 * Creates an {@link InnerEvent}
		 * 
		 * @param function {@link TimelineEventHandler} to be executed when the {@link InnerEvent} occurs
		 * @param timing   Point in the MI simulation {@link Timeline}, at which the {@link InnerEvent} is executed;
		 */
		InnerEvent(TimelineEventHandler function, TimelineEvent event)
		{
			this.function = function;
			this.event = event;
		}

		public long getTiming()
		{
			return event.getTiming();
		}

		@Override
		public void run()
		{
			function.handle(event);
		}

		@Override
		public String toString()
		{
			return event.toString();
		}

		@Override
		public int compareTo(InnerEvent o)
		{
			return timeCmp(getTiming(), o.getTiming());
		}
	}

	public static int timeCmp(long a, long b)
	{
		return Long.signum(a - b);
	}

	@Override
	public String toString()
	{
		return String.format("Simulation time: %s, Last update: %d, Events: %s", getSimulationTime(), lastTimeUpdated, events.toString());
	}

	public enum ExecutionResult
	{
		NOTHING_DONE, EXEC_UNTIL_EMPTY, EXEC_UNTIL_CONDITION, EXEC_OUT_OF_TIME
	}
}