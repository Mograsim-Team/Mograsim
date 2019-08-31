package net.mograsim.machine;

public interface MemoryDefinition {

	/**
	 * The number of bits that the main memory uses to address cells. Note that this
	 * does not need to equal {@link MachineDefinition#getAddressBits()}.
	 * 
	 * @return the number of bits used to address a memory cell
	 * @author Christian Femers
	 */
	int getMemoryAddressBits();

	/**
	 * The minimal address possible to address/use. This is usually 0.
	 * 
	 * @return the minimal possible address.
	 * @author Christian Femers
	 */
	long getMinimalAddress();

	/**
	 * The maximal address possible to address/use.
	 * 
	 * @return the maximal possible address as <b>unsigned long</b>
	 * @author Christian Femers
	 */
	long getMaximalAddress();
	
	/**
	 * The size of the MainMemory as the amount of addressable memory cells.
	 * 
	 * @return the amount of addressable memory cells
	 */
	default long size()
	{
		return getMaximalAddress() - getMinimalAddress();
	}
	
	public static MemoryDefinition create(int memoryAddressBits, long minimalAddress, long maximalAddress)
	{
		return new StandardMemoryDefinition(memoryAddressBits, minimalAddress, maximalAddress);
	}
}
