package net.mograsim.machine.mi;

import net.mograsim.machine.BitVectorMemoryDefinition;

/**
 * This interface provides a means to get information about the machines memory architecture. It is not bound to any actual memory.
 *
 * @author Christian Femers
 *
 */
public interface MPROMDefinition extends BitVectorMemoryDefinition
{
	public static MPROMDefinition create(int opcodeBits, int microInstructionMemoryAddressBits)
	{
		return new StandardMPROMDefinition(opcodeBits, microInstructionMemoryAddressBits);
	}
}
