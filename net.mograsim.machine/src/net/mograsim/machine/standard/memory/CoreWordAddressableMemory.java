package net.mograsim.machine.standard.memory;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;

/**
 * A memory component that only allows access to words of a specific width
 */
public class CoreWordAddressableMemory extends BasicCoreComponent
{
	private final MainMemory memory;
	private final static Bit read = Bit.ONE;

	private ReadWriteEnd data;
	private ReadEnd rWBit, address;

	/**
	 * @param data    The bits of this ReadEnd are the value that is written to/read from memory; The bit width of this wire is the width of
	 *                a memory word
	 * @param rWBit   The value of the 0th bit dictates the mode: 0: Write, 1: Read
	 * @param address The bits of this ReadEnd address the memory cell to read/write
	 */
	public CoreWordAddressableMemory(Timeline timeline, int processTime, MainMemory memory, ReadWriteEnd data, ReadEnd rWBit,
			ReadEnd address)
	{
		super(timeline, processTime);
		MainMemoryDefinition definition = memory.getDefinition();
		if (data.width() != definition.getCellWidth())
			throw new IllegalArgumentException(
					String.format("Bit width of data wire does not match main memory definition. Expected: %d Actual: %d",
							definition.getCellWidth(), data.width()));
		if (rWBit.width() != 1)
			throw new IllegalArgumentException(
					String.format("Bit width of read/write mode select wire is unexpected. Expected: 1 Actual: %d", rWBit.width()));
		if (address.width() != definition.getMemoryAddressBits())
			throw new IllegalArgumentException(
					String.format("Bit width of address wire does not match main memory definition. Expected: %d Actual: %d",
							definition.getMemoryAddressBits(), address.width()));
		this.memory = memory;
		this.data = data;
		this.rWBit = rWBit;
		this.address = address;
		memory.registerObserver(a -> update());
		data.registerObserver(this);
		rWBit.registerObserver(this);
		address.registerObserver(this);
	}

	@Override
	protected TimelineEventHandler compute()
	{
		if (!address.getValues().isBinary())
		{
			if (read.equals(rWBit.getValue()))
				return e -> data.feedSignals(Bit.U.toVector(data.width()));// TODO don't always feed U, but decide to feed X or U.
			return e -> data.clearSignals();
		}
		long addressed = address.getValues().getUnsignedValueLong();
		if (read.equals(rWBit.getValue()))
		{
			BitVector storedData = memory.getCell(addressed);
			return e -> data.feedSignals(storedData);
		}
		BitVector transData = data.getValues();
		boolean isNewData = !transData.equals(memory.getCell(addressed));
		return e ->
		{
			data.clearSignals();
			if (isNewData)
				memory.setCell(addressed, transData);
		};
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(data, rWBit, address);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(data);
	}
}