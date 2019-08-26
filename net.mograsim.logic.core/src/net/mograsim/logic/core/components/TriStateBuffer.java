package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class TriStateBuffer extends BasicComponent
{
	ReadEnd in, enable;
	ReadWriteEnd out;

	public TriStateBuffer(Timeline timeline, int processTime, ReadEnd in, ReadWriteEnd out, ReadEnd enable)
	{
		super(timeline, processTime);
		if (in.width() != out.width())
			throw new IllegalArgumentException(
					"Tri-state output must have the same amount of bits as the input. Input: " + in.width() + " Output: " + out.width());
		if (enable.width() != 1)
			throw new IllegalArgumentException("Tri-state enable must have exactly one bit, not " + enable.width() + ".");
		this.in = in;
		in.registerObserver(this);
		this.enable = enable;
		enable.registerObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		if (enable.getValue() == Bit.ONE)
			out.feedSignals(in.getValues());
		else
			out.clearSignals();
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in, enable);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}

}
