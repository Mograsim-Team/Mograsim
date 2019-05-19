package era.mi.logic.components;

import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.timeline.TimelineEvent;
import era.mi.logic.timeline.TimelineEventHandler;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;

public class Clock implements TimelineEventHandler, Component
{
	private boolean toggle = false;
	private WireArrayEnd outI;
	private int delta;

	/**
	 * 
	 * @param out   {@link WireArray} the clock's impulses are fed into
	 * @param delta ticks between rising and falling edge
	 */
	public Clock(WireArray out, int delta)
	{
		this.delta = delta;
		this.outI = out.createInput();
		Simulation.TIMELINE.addEvent(this, delta);
	}

	@Override
	public void handle(TimelineEvent e)
	{
		addToTimeline();
		outI.feedSignals(toggle ? Bit.ONE : Bit.ZERO);
		toggle = !toggle;
	}

	public WireArray getOut()
	{
		return outI.owner;
	}

	private void addToTimeline()
	{
		Simulation.TIMELINE.addEvent(this, delta);
	}

	@Override
	public List<WireArray> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return List.of(outI.owner);
	}
}
