package net.mograsim.machine;

/**
 * This interface provides a means to get information about the machines memory
 * architecture. It is not bound to any actual memory.
 *
 * @author Christian Femers
 *
 */
public interface MainMemoryDefinition {

	/**
	 * The number of bits that the main memory uses to address cells. Note that this
	 * does not need to equal {@link MachineDefinition#getAddressBits()}.
	 * 
	 * @return the number of bits used to address a memory cell
	 * @author Christian Femers
	 */
	int getMemoryAddressBits();

	/**
	 * The width in bits of an addressable memory cell/unit. This is often 8 (= one
	 * byte). The actual cells/lines of the memory may be a lot larger.
	 * 
	 * @return the addressable unit width in bits
	 * @author Christian Femers
	 */
	int getCellWidth();

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
}
