package net.mograsim.machine;

/**
 * This interface provides a means to get information about the machines memory architecture. It is not bound to any actual memory.
 *
 * @author Christian Femers
 *
 */
public interface MainMemoryDefinition extends BitVectorMemoryDefinition
{
	public static MainMemoryDefinition create(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
	{
		return new StandardMainMemoryDefinition(memoryAddressBits, cellWidth, minimalAddress, maximalAddress);
	}
}
