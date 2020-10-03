package net.mograsim.machine.mi;

import net.mograsim.machine.standard.memory.StandardBitVectorMemoryDefinition;

class StandardMPROMDefinition extends StandardBitVectorMemoryDefinition implements MPROMDefinition
{
	StandardMPROMDefinition(int opcodeBits, int microInstructionMemoryAddressBits)
	{
		super(opcodeBits, microInstructionMemoryAddressBits, 0, 1 << opcodeBits);
	}
}
