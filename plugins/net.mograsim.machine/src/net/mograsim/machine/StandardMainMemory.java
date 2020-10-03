package net.mograsim.machine;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.standard.memory.StandardBitVectorMemory;

public class StandardMainMemory extends StandardBitVectorMemory<MainMemoryDefinition> implements MainMemory
{
	public StandardMainMemory(MainMemoryDefinition definition)
	{
		super(definition);
	}

	@Override
	protected BitVector getDefaultValue(long address)
	{
		return BitVector.of(Bit.ZERO, getDefinition().getCellWidth());
	}
}
