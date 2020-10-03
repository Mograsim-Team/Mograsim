package net.mograsim.machine.mi;

import net.mograsim.machine.BitVectorMemory;

public interface MPROM extends BitVectorMemory
{
	@Override
	public MPROMDefinition getDefinition();
}