package net.mograsim.machine.mi;

import net.mograsim.machine.MemoryDefinition;

/**
 * This interface provides a means to get information about the machines memory architecture. It is not bound to any actual memory.
 *
 * @author Christian Femers
 *
 */
public interface MPROMDefinition extends MemoryDefinition
{
	/**
	 * The width in bits of an addressable memory cell/unit. This is often 8 (= one byte). The actual cells/lines of the memory may be a lot
	 * larger.
	 * 
	 * @return the addressable unit width in bits
	 * @author Christian Femers
	 */
	int getMicroInstructionMemoryAddressBits();

	public static MPROMDefinition create(int opcodeBits, int microInstructionMemoryAddressBits)
	{
		return new StandardMPROMDefinition(opcodeBits, microInstructionMemoryAddressBits);
	}
}
