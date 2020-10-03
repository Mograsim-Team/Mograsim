package net.mograsim.machine;

import net.mograsim.machine.standard.memory.StandardBitVectorMemoryDefinition;

class StandardMainMemoryDefinition extends StandardBitVectorMemoryDefinition implements MainMemoryDefinition
{
	StandardMainMemoryDefinition(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
	{
		super(memoryAddressBits, cellWidth, minimalAddress, maximalAddress);
	}
}
