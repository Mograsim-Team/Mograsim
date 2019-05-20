package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.timeline.TimelineEvent;
import era.mi.logic.timeline.TimelineEventHandler;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;

public class Clock implements TimelineEventHandler, Component
{
	private boolean toggle = false;
	private WireEnd out;
	private int delta;

	/**
	 * 
	 * @param out   {@link Wire} the clock's impulses are fed into
	 * @param delta ticks between rising and falling edge
	 */
	public Clock(WireEnd out, int delta)
	{
		this.delta = delta;
		this.out = out;
		addToTimeline();
	}

	@Override
	public void handle(TimelineEvent e)
	{
		addToTimeline();
		out.feedSignals(new Bit[] { toggle ? Bit.ONE : Bit.ZERO });
		toggle = !toggle;
	}

	public WireEnd getOut()
	{
		return out;
	}

	private void addToTimeline()
	{
		Simulation.TIMELINE.addEvent(this, delta);
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList());
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(out));
	}
}
