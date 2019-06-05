package net.mograsim.logic.core.timeline;

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

	public final LongSupplier stepByStepExec = () -> lastTimeUpdated;
	public final LongSupplier realTimeExec = () -> System.currentTimeMillis();

	/**
	 * Constructs a Timeline object. Per default the time function is set to step by step execution.
	 * 
	 * @param initCapacity The initial capacity of the event queue.
	 */
	public Timeline(int initCapacity)
	{
		events = new PriorityQueue<>(initCapacity);
		eventAddedListener = new ArrayList<>();
		time = stepByStepExec;
	}

	/**
	 * @param timestamp exclusive
	 * @return <code>true</code> if the first event in queue is later than the given timestamp
	 */
	public BooleanSupplier laterThan(long timestamp)
	{
		return () -> timeCmp(events.peek().getTiming(), timestamp) > 0;
	}

	/**
	 * @return <code>true</code> if there is at least one event enqueued. <code>false</code> otherwise
	 */
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

	/**
	 * Executes all events enqueued in the {@link Timeline}. Use very carefully! Events may generate new events, causing an infinite loop.
	 */
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

	/**
	 * Sets the function, which defines the current simulation time at any time.
	 * 
	 * @param time The return value of calling this function is the current simulation time.
	 */
	public void setTimeFunction(LongSupplier time)
	{
		this.time = time;
	}

	/**
	 * Calculates the current simulation time.
	 * 
	 * @return The simulation time as defined by the time function.
	 */
	public long getSimulationTime()
	{
		return time.getAsLong();
	}

	/**
	 * Retrieves the timestamp of the next event.
	 * 
	 * @return The timestamp of the next enqueued event, if the {@link Timeline} is not empty, -1 otherwise.
	 */
	public long nextEventTime()
	{
		if (!hasNext())
			return -1;
		return events.peek().getTiming();
	}

	/**
	 * Clears the {@link Timeline} of enqueued events.
	 */
	public void reset()
	{
		events.clear();
		lastTimeUpdated = 0;
	}

	/**
	 * Adds a listener, that is called when a {@link TimelineEvent} is added.
	 */
	public void addEventAddedListener(Consumer<TimelineEvent> listener)
	{
		eventAddedListener.add(listener);
	}

	/**
	 * Removes the listener, if possible. It will no longer be called when a {@link TimelineEvent} is added.
	 */
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