package net.mograsim.machine.standard.memory;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;

/**
 * A memory component that only allows access to words of a specific width
 */
public class CoreBitVectorMemory<M extends BitVectorMemory> extends BasicCoreComponent
{
	private final static Bit read = Bit.ONE;

	private final ReadWriteEnd data;
	private final ReadEnd rWBit, address;
	private final boolean readonly;
	private final MemoryCellModifiedListener memObs;
	private final BitVectorMemoryDefinition definition;
	private M memory;

	/**
	 * @param data    The bits of this ReadEnd are the value that is written to/read from memory; The bit width of this wire is the width of
	 *                a memory word
	 * @param rWBit   The value of the 0th bit dictates the mode: 0: Write, 1: Read
	 * @param address The bits of this ReadEnd address the memory cell to read/write
	 */
	public CoreBitVectorMemory(Timeline timeline, int processTime, BitVectorMemoryDefinition definition, ReadWriteEnd data, ReadEnd rWBit,
			ReadEnd address, boolean readonly)
	{
		super(timeline, processTime);
		if (data.width() != definition.getCellWidth())
			throw new IllegalArgumentException(
					String.format("Bit width of data wire does not match main memory definition. Expected: %d Actual: %d",
							definition.getCellWidth(), data.width()));
		if (!readonly && rWBit.width() != 1)
			throw new IllegalArgumentException(
					String.format("Bit width of read/write mode select wire is unexpected. Expected: 1 Actual: %d", rWBit.width()));
		if (address.width() != definition.getMemoryAddressBits())
			throw new IllegalArgumentException(
					String.format("Bit width of address wire does not match main memory definition. Expected: %d Actual: %d",
							definition.getMemoryAddressBits(), address.width()));
		this.data = data;
		this.rWBit = rWBit;
		this.address = address;
		this.readonly = readonly;
		this.definition = definition;
		this.memObs = a -> update();
		data.registerObserver(this);
		if (!readonly)
			rWBit.registerObserver(this);
		address.registerObserver(this);
	}

	public void setMemory(M memory)
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

	public M getMemory()
	{
		return memory;
	}

	@Override
	protected TimelineEventHandler compute()
	{
		if (memory == null)
			return e -> data.feedSignals(Bit.U.toVector(data.width()));
		boolean isReading = readonly || read.equals(rWBit.getValue());
		if (!address.getValues().isBinary())
		{
			if (isReading)
				return e -> data.feedSignals(Bit.U.toVector(data.width()));// TODO don't always feed U, but decide to feed X or U.
			return e -> data.clearSignals();
		}
		long addressed = address.getValues().getUnsignedValueLong();
		if (isReading)
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
		return readonly ? List.of(data, address) : List.of(data, rWBit, address);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(data);
	}
}