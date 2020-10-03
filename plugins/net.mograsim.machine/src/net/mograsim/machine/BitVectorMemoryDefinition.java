package net.mograsim.machine;

public interface BitVectorMemoryDefinition extends MemoryDefinition
{

	/**
	 * The width in bits of an addressable memory cell/unit. This is often 8 (= one byte). The actual cells/lines of the memory may be a lot
	 * larger.
	 * 
	 * @return the addressable unit width in bits
	 * @author Christian Femers
	 */
	int getCellWidth();
}
