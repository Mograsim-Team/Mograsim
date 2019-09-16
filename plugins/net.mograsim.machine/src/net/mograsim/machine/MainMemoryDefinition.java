package net.mograsim.machine;

/**
 * This interface provides a means to get information about the machines memory architecture. It is not bound to any actual memory.
 *
 * @author Christian Femers
 *
 */
public interface MainMemoryDefinition extends MemoryDefinition
{
	/**
	 * The width in bits of an addressable memory cell/unit. This is often 8 (= one byte). The actual cells/lines of the memory may be a lot
	 * larger.
	 * 
	 * @return the addressable unit width in bits
	 * @author Christian Femers
	 */
	int getCellWidth();

	public static MainMemoryDefinition create(int memoryAddressBits, int cellWidth, long minimalAddress, long maximalAddress)
	{
		return new StandardMainMemoryDefinition(memoryAddressBits, cellWidth, minimalAddress, maximalAddress);
	}
}
