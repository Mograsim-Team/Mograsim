package net.mograsim.machine;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;

public interface BitVectorMemory extends Memory<BitVector>
{
	BigInteger getCellAsBigInteger(long address);

	public void setCellAsBigInteger(long address, BigInteger word);

	@Override
	BitVectorMemoryDefinition getDefinition();
}
