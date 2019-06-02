package net.mograsim.logic.core.components;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;

/**
 * A basic component that recomputes all outputs (with a delay), when it is updated.
 * 
 * @author Fabian Stemmler
 */
public abstract class BasicComponent extends Component implements LogicObserver
{
	private int processTime;

	/**
	 * 
	 * @param processTime Amount of time this component takes to update its outputs. Must be more than 0, otherwise 1 is assumed.
	 * 
	 * @author Fabian Stemmler
	 */
	public BasicComponent(Timeline timeline, int processTime)
	{
		super(timeline);
		this.processTime = processTime > 0 ? processTime : 1;
	}

	@Override
	public void update(LogicObservable initiator)
	{
		timeline.addEvent(e -> compute(), processTime);
	}

	protected abstract void compute();
}
