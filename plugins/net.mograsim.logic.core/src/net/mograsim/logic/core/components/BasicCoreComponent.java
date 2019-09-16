package net.mograsim.logic.core.components;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;

/**
 * A basic component that recomputes all outputs (with a delay), when it is updated.
 * 
 * @author Fabian Stemmler
 */
public abstract class BasicCoreComponent extends CoreComponent implements LogicObserver
{
	private int processTime;

	/**
	 * 
	 * @param processTime Amount of time this component takes to update its outputs. Must be more than 0, otherwise 1 is assumed.
	 * 
	 * @author Fabian Stemmler
	 */
	public BasicCoreComponent(Timeline timeline, int processTime)
	{
		super(timeline);
		this.processTime = processTime > 0 ? processTime : 1;
	}

	@Override
	public final void update(LogicObservable initiator)
	{
		update();
	}

	public void update()
	{
		TimelineEventHandler delayedUpdates = compute();
		if (delayedUpdates != null)
			timeline.addEvent(delayedUpdates, processTime);
	}

	protected abstract TimelineEventHandler compute();
}
