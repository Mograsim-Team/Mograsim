package net.mograsim.machine.standard.memory;

import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.StandardMemoryDefinition;

public class StandardBitVectorMemoryDefinition extends StandardMemoryDefinition implements BitVectorMemoryDefinition
{
	private final int cellWidth;

	public StandardBitVectorMemoryDefinition(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
	{
		super(memoryAddressBits, minimalAddress, maximalAddress);
		this.cellWidth = cellWidth;
	}

	@Override
	public int getCellWidth()
	{
		return cellWidth;
	}
}
