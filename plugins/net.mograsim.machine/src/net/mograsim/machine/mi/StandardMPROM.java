package net.mograsim.machine.mi;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.standard.memory.StandardBitVectorMemory;

public class StandardMPROM extends StandardBitVectorMemory<MPROMDefinition> implements MPROM
{
	public StandardMPROM(MPROMDefinition definition)
	{
		super(definition);
	}

	@Override
	protected BitVector getDefaultValue(long address)
	{
		return BitVector.from(address * 16, getDefinition().getCellWidth());
	}
}
