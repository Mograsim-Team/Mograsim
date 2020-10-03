package net.mograsim.machine;

public class StandardMemoryDefinition implements MemoryDefinition
{
	private final int memoryAddressBits;
	private final long minimalAddress, maximalAddress;

	public StandardMemoryDefinition(int memoryAddressBits, long minimalAddress, long maximalAddress)
	{
		super();
		this.memoryAddressBits = memoryAddressBits;
		this.minimalAddress = minimalAddress;
		this.maximalAddress = maximalAddress;
	}

	public StandardMemoryDefinition(MainMemoryDefinition definition)
	{
		this(definition.getMemoryAddressBits(), definition.getMinimalAddress(), definition.getMaximalAddress());
	}

	@Override
	public int getMemoryAddressBits()
	{
		return memoryAddressBits;
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

	public static MemoryDefinition create(int memoryAddressBits, long minimalAddress, long maximalAddress)
	{
		return new StandardMemoryDefinition(memoryAddressBits, minimalAddress, maximalAddress);
	}
}
