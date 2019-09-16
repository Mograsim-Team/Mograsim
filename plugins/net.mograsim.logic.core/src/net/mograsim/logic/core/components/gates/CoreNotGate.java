package net.mograsim.logic.core.components.gates;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreNotGate extends BasicCoreComponent
{
	private ReadEnd in;
	private ReadWriteEnd out;

	public CoreNotGate(Timeline timeline, int processTime, ReadEnd in, ReadWriteEnd out)
	{
		super(timeline, processTime);
		this.in = in;
		in.registerObserver(this);
		this.out = out;
	}

	@Override
	protected TimelineEventHandler compute()
	{
		BitVector values = in.getValues().not();
		return e -> out.feedSignals(values);
	}

	public ReadEnd getIn()
	{
		return in;
	}

	public ReadEnd getOut()
	{
		return out;
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
