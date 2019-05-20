package era.mi.logic.components;

import era.mi.logic.Simulation;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.WireObserver;

/**
 * A basic component that recomputes all outputs (with a delay), when it is updated.
 * 
 * @author Fabian Stemmler
 */
public abstract class BasicComponent implements WireObserver, Component
{
	private int processTime;

	/**
	 * 
	 * @param processTime Amount of time this component takes to update its outputs. Must be more than 0, otherwise 1 is assumed.
	 * 
	 * @author Fabian Stemmler
	 */
	public BasicComponent(int processTime)
	{
		this.processTime = processTime > 0 ? processTime : 1;
	}

	@Override
	public void update(Wire initiator, BitVector oldValues)
	{
		Simulation.TIMELINE.addEvent(e -> compute(), processTime);
	}

	protected abstract void compute();
}
