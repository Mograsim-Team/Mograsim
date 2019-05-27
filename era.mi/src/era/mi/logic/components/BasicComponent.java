package era.mi.logic.components;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.WireObserver;

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
