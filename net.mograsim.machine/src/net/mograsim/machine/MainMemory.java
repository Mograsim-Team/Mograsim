package net.mograsim.machine;

import net.mograsim.logic.core.types.BitVector;

public interface MainMemory {
	
	public BitVector getCell(long address);
	public void setCell(long address, BitVector word);
	public MainMemoryDefinition getDefinition();
}
