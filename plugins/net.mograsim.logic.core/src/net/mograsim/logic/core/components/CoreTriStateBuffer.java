package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreTriStateBuffer extends BasicCoreComponent
{
	ReadEnd in, enable;
	ReadWriteEnd out;

	public CoreTriStateBuffer(Timeline timeline, int processTime, ReadEnd in, ReadWriteEnd out, ReadEnd enable)
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
	protected TimelineEventHandler compute()
	{
		if (enable.getValue() == Bit.ONE)
		{
			BitVector inValues = in.getValues();
			return e -> out.feedSignals(inValues);
		}
		return e -> out.clearSignals();
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
