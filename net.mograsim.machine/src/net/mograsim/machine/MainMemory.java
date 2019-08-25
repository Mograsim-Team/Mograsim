package net.mograsim.machine;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;

public interface MainMemory {
	
	public BitVector getCell(long address);
	public void setCell(long address, BitVector word);
	public BigInteger getCellAsBigInteger(long address);
	public void setCellAsBigInteger(long address, BigInteger word);
	public MainMemoryDefinition getDefinition();
}
