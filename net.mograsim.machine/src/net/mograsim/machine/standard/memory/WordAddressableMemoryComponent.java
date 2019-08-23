package net.mograsim.machine.standard.memory;

import java.util.List;

import net.mograsim.logic.core.components.BasicComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

/**
 * A memory component that only allows access to words of a specific length
 */
public class WordAddressableMemoryComponent extends BasicComponent
{
	private final WordAddressableMemory memory;
	private final static Bit read = Bit.ONE;

	private ReadWriteEnd data;
	private ReadEnd rWBit, address;

	/**
	 * @param data    The bits of this ReadEnd are the value that is written to/read from memory; The bit width of this wire is the width of
	 *                a memory word
	 * @param rWBit   The value of the 0th bit dictates the mode: 0: Write, 1: Read
	 * @param address The bits of this ReadEnd address the memory cell to read/write
	 */
	public WordAddressableMemoryComponent(Timeline timeline, int processTime, long minimalAddress, long maximalAddress, ReadWriteEnd data,
			ReadEnd rWBit, ReadEnd address)
	{
		super(timeline, processTime);
		this.data = data;
		this.rWBit = rWBit;
		this.address = address;
		data.registerObserver(this);
		rWBit.registerObserver(this);
		address.registerObserver(this);

		memory = new WordAddressableMemory(data.length(), minimalAddress, maximalAddress);
	}

	@Override
	protected void compute()
	{
		if (!address.hasNumericValue())
		{
			if (read.equals(rWBit.getValue()))
				data.feedSignals(BitVector.of(Bit.U, data.length()));
			else
				data.clearSignals();
			return;
		}
		long addressed = address.getUnsignedValue();
		if (read.equals(rWBit.getValue()))
			data.feedSignals(memory.getCell(addressed));
		else
		{
			data.clearSignals();
			memory.setCell(addressed, data.getValues());
		}
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