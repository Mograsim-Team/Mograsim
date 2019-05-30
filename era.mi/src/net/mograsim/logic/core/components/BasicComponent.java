package net.mograsim.logic.core.components;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.WireObserver;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

/**
 * A basic component that recomputes all outputs (with a delay), when it is updated.
 * 
 * @author Fabian Stemmler
 */
public abstract class BasicComponent extends Component implements WireObserver
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
	public void update(ReadEnd initiator, BitVector oldValues)
	{
		timeline.addEvent(e -> compute(), processTime);
	}

	protected abstract void compute();
}
