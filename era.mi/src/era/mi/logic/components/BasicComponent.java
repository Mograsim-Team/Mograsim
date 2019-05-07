package era.mi.logic.components;

import era.mi.logic.Simulation;
import era.mi.logic.WireArray;
import era.mi.logic.WireArrayObserver;

public abstract class BasicComponent implements WireArrayObserver
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
	public void update(WireArray initiator)
	{
		Simulation.TIMELINE.addEvent((e) -> {compute();}, processTime);
	}
	
	protected abstract void compute();
}
