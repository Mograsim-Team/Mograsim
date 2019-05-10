package era.mi.logic.components;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.timeline.TimelineEvent;
import era.mi.logic.timeline.TimelineEventHandler;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

public class Clock implements TimelineEventHandler
{
	private boolean toggle = false;
	private WireArrayInput outI;
	
	public Clock(WireArray out)
	{
		this.outI = out.createInput();
	}

	@Override
	public void handle(TimelineEvent e)
	{
		Simulation.TIMELINE.addEvent(this, 50);
		outI.feedSignals(new Bit[] { toggle ? Bit.ONE : Bit.ZERO });
		toggle = !toggle;
	}

	public WireArray getOut()
	{
		return outI.owner;
	}
}
