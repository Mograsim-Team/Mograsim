package net.mograsim.machine;

public class DefaultMainMemoryDefinition implements MainMemoryDefinition {
	private final int memoryAddressBits, cellWidth;
	private final long minimalAddress, maximalAddress;
	
	public DefaultMainMemoryDefinition(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
	{
		super();
		this.memoryAddressBits = memoryAddressBits;
		this.cellWidth = cellWidth;
		this.minimalAddress = minimalAddress;
		this.maximalAddress = maximalAddress;
	}

	public DefaultMainMemoryDefinition(MainMemoryDefinition definition)
	{
		this(definition.getMemoryAddressBits(), definition.getCellWidth(), definition.getMinimalAddress(), definition.getMaximalAddress());
	}

	@Override
	public int getMemoryAddressBits()
	{
		return memoryAddressBits;
	}

	@Override
	public int getCellWidth()
	{
		return cellWidth;
	}

	@Override
	public long getMinimalAddress()
	{
		return minimalAddress;
	}

	@Override
	public long getMaximalAddress()
	{
		return maximalAddress;
	}
	
}
