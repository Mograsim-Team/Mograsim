package net.mograsim.machine.mi.components;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public class CoreMicroInstructionMemory extends BasicCoreComponent
{
	private final ReadWriteEnd data;
	private final ReadEnd address;
	private final MicroInstructionMemoryDefinition definition;
	private final MemoryCellModifiedListener memObs;
	private MicroInstructionMemory memory;

	public CoreMicroInstructionMemory(Timeline timeline, int processTime, MicroInstructionMemoryDefinition definition, ReadWriteEnd data,
			ReadEnd address)
	{
		super(timeline, processTime);
		if (data.width() != definition.getMicroInstructionDefinition().sizeInBits())
			throw new IllegalArgumentException(
					String.format("Bit width of data wire does not match microinstruction memory definition. Expected: %d Actual: %d",
							definition.getMicroInstructionDefinition().sizeInBits(), data.width()));
		if (address.width() != definition.getMemoryAddressBits())
			throw new IllegalArgumentException(
					String.format("Bit width of address wire does not match microinstruction memory definition. Expected: %d Actual: %d",
							definition.getMemoryAddressBits(), address.width()));

		this.data = data;
		this.address = address;
		this.definition = definition;
		this.memObs = a -> update();
		address.registerObserver(this);
	}

	public void setMemory(MicroInstructionMemory memory)
	{
		if (memory != null && !memory.getDefinition().equals(definition))
			throw new IllegalArgumentException("Memory of incorrect memory definition given");
		if (this.memory != null)
			this.memory.registerCellModifiedListener(memObs);
		this.memory = memory;
		if (memory != null)
			memory.registerCellModifiedListener(memObs);
		update();
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
		if (memory == null || !address.getValues().isBinary())
			return e -> data.feedSignals(Bit.U.toVector(data.width()));// TODO don't always feed U, but decide to feed X or U.
		long addressed = address.getValues().getUnsignedValueLong();
		BitVector storedData = memory.getCell(addressed).toBitVector();
		memory.setActiveInstruction(addressed);
		return e -> data.feedSignals(storedData);
	}
}