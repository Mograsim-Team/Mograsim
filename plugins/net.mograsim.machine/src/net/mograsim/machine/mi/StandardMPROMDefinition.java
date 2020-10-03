package net.mograsim.machine.mi;

import net.mograsim.machine.StandardMemoryDefinition;

class StandardMPROMDefinition extends StandardMemoryDefinition implements MPROMDefinition
{
	private final int microInstructionMemoryAddressBits;

	StandardMPROMDefinition(int opcodeBits, int microInstructionMemoryAddressBits)
	{
		super(opcodeBits, 0, 1 << opcodeBits);
		this.microInstructionMemoryAddressBits = microInstructionMemoryAddressBits;
	}

	@Override
	public int getMicroInstructionMemoryAddressBits()
	{
		return microInstructionMemoryAddressBits;
	}
}
