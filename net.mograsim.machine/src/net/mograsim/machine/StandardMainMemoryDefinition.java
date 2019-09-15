package net.mograsim.machine;

class StandardMainMemoryDefinition extends StandardMemoryDefinition implements MainMemoryDefinition
{
	private final int cellWidth;

	StandardMainMemoryDefinition(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
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
