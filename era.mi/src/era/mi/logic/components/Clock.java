package era.mi.logic.components;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.WireArray;
import era.mi.logic.timeline.TimelineEvent;
import era.mi.logic.timeline.TimelineEventHandler;

public class Clock implements TimelineEventHandler
{
	private boolean toggle = false;
	private WireArray w;
	
	public Clock(WireArray w)
	{
		this.w = w;
	}

	@Override
	public void handle(TimelineEvent e)
	{
		Simulation.TIMELINE.addEvent(this, 50);
		w.feedSignals(new Bit[] { toggle ? Bit.ONE : Bit.ZERO });
		toggle = !toggle;
	}

	public WireArray getW()
	{
		return w;
	}
}
