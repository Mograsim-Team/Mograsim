package era.mi.logic.components;

import java.util.List;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.timeline.TimelineEvent;
import era.mi.logic.timeline.TimelineEventHandler;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class Clock extends Component implements TimelineEventHandler
{
	private boolean toggle = false;
	private ReadWriteEnd out;
	private int delta;

	/**
	 * 
	 * @param out   {@link Wire} the clock's impulses are fed into
	 * @param delta ticks between rising and falling edge
	 */
	public Clock(Timeline timeline, ReadWriteEnd out, int delta)
	{
		super(timeline);
		this.delta = delta;
		this.out = out;
		addToTimeline();
	}

	@Override
	public void handle(TimelineEvent e)
	{
		addToTimeline();
		out.feedSignals(toggle ? Bit.ONE : Bit.ZERO);
		toggle = !toggle;
	}

	public ReadWriteEnd getOut()
	{
		return out;
	}

	private void addToTimeline()
	{
		timeline.addEvent(this, delta);
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
