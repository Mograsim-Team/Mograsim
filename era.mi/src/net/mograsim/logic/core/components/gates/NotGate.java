package net.mograsim.logic.core.components.gates;

import java.util.List;

import net.mograsim.logic.core.components.BasicComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class NotGate extends BasicComponent
{
	private ReadEnd in;
	private ReadWriteEnd out;

	public NotGate(Timeline timeline, int processTime, ReadEnd in, ReadWriteEnd out)
	{
		super(timeline, processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		out.feedSignals(in.getValues().not());
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
