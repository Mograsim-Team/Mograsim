package net.mograsim.logic.core.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Orders Events by the time they are due to be executed. Can execute Events individually.
 * 
 * @author Fabian Stemmler
 *
 */
public class Timeline
{
	private PriorityQueue<InnerEvent> events;
	private TimeFunction time;
	private long lastTimeUpdated = 0;
	private long eventCounter = 0;

	private final List<Consumer<TimelineEvent>> eventAddedListener;

	public final TimeFunction stepByStepExec = new TimeFunction()
	{

		@Override
		public void setTime(long time)
		{
			lastTimeUpdated = time;
		}

		@Override
		public long getTime()
		{
			return lastTimeUpdated;
		}
	};
	public final TimeFunction realTimeExec = new TimeFunction()
	{
		private long offset = 0;

		@Override
		public void setTime(long time)
		{
			offset = time;
		}

		@Override
		public long getTime()
		{
			return System.currentTimeMillis() - offset;
		}
	};
	private boolean isWorking;

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
		working();
		int checkStop = 0;
		while (hasNext() && !condition.getAsBoolean())
		{
			InnerEvent event;
			synchronized (events)
			{
				event = events.remove();
			}
			lastTimeUpdated = event.getTiming();
			event.run();
			// Don't check after every run
			checkStop = (checkStop + 1) % 10;
			if (checkStop == 0 && System.currentTimeMillis() >= stopMillis)
			{
				notWorking();
				lastTimeUpdated = getSimulationTime();
				return ExecutionResult.EXEC_OUT_OF_TIME;
			}
		}
		notWorking();
		lastTimeUpdated = getSimulationTime();
		return hasNext() ? ExecutionResult.EXEC_UNTIL_EMPTY : ExecutionResult.EXEC_UNTIL_CONDITION;
	}

	/**
	 * Sets the function, which defines the current simulation time at any time.
	 * 
	 * @param time The return value of calling this function is the current simulation time.
	 */
	public void setTimeFunction(TimeFunction time)
	{
		time.setTime(this.time.getTime());
		this.time = time;
	}

	/**
	 * Calculates the current simulation time.
	 * 
	 * @return The simulation time as defined by the time function.
	 */
	public long getSimulationTime()
	{
		return isWorking() ? lastTimeUpdated : time.getTime();
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
		synchronized (events)
		{
			events.clear();
		}
		lastTimeUpdated = time.getTime();
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
		synchronized (events)
		{
			events.add(new InnerEvent(function, event, eventCounter++));
		}
		eventAddedListener.forEach(l -> l.accept(event));
	}

	//@formatter:off
	private void working() { isWorking = true; }
	private void notWorking() { isWorking = false; }
	private boolean isWorking() { return isWorking; }
	//@formatter:on

	private class InnerEvent implements Runnable, Comparable<InnerEvent>
	{
		private final TimelineEventHandler function;
		private final TimelineEvent event;
		private final long id;

		/**
		 * Creates an {@link InnerEvent}
		 * 
		 * @param function {@link TimelineEventHandler} to be executed when the {@link InnerEvent} occurs
		 * @param timing   Point in the MI simulation {@link Timeline}, at which the {@link InnerEvent} is executed;
		 */
		InnerEvent(TimelineEventHandler function, TimelineEvent event, long id)
		{
			this.function = function;
			this.event = event;
			this.id = id;
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
			int c1;
			return (c1 = timeCmp(getTiming(), o.getTiming())) == 0 ? timeCmp(id, o.id) : c1;
		}
	}

	public static int timeCmp(long a, long b)
	{
		return Long.signum(a - b);
	}

	@Override
	public String toString()
	{
		String eventsString;
		synchronized (events)
		{
			eventsString = events.toString();
		}
		return String.format("Simulation time: %s, Last update: %d, Events: %s", getSimulationTime(), lastTimeUpdated, eventsString);
	}

	public static enum ExecutionResult
	{
		NOTHING_DONE, EXEC_UNTIL_EMPTY, EXEC_UNTIL_CONDITION, EXEC_OUT_OF_TIME
	}

	public static interface TimeFunction
	{
		long getTime();

		void setTime(long time);
	}
}