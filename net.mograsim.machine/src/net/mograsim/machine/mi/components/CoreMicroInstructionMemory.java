package net.mograsim.machine.mi.components;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.mi.MicroInstructionMemory;

public class CoreMicroInstructionMemory extends BasicCoreComponent
{
	private final ReadWriteEnd data;
	private final ReadEnd address;
	private final MicroInstructionMemory memory;

	public CoreMicroInstructionMemory(Timeline timeline, int processTime, MicroInstructionMemory memory, ReadWriteEnd data, ReadEnd address)
	{
		super(timeline, processTime);
		this.memory = memory;
		this.data = data;
		this.address = address;
		memory.registerObserver(a -> update());
		address.registerObserver(this);
	}

	public MicroInstructionMemory getMemory()
	{
		return memory;
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(address);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(data);
	}

	@Override
	protected TimelineEventHandler compute()
	{
		if (!address.hasNumericValue())
		{
			return e -> data.feedSignals(Bit.U.toVector(data.width()));// TODO don't always feed U, but decide to feed X or U.
		}
		long addressed = address.getUnsignedValue();
		BitVector storedData = memory.getCell(addressed).toBitVector();
		return e -> data.feedSignals(storedData);
	}
}
