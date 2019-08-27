package net.mograsim.machine;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;

public interface MainMemory extends Memory<BitVector> {
	
	public BigInteger getCellAsBigInteger(long address);
	public void setCellAsBigInteger(long address, BigInteger word);
	public MainMemoryDefinition getDefinition();
	public default long size()
	{
		return getDefinition().size();
	}
}
