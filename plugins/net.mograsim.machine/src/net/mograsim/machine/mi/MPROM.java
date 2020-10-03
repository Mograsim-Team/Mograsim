package net.mograsim.machine.mi;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.Memory;

public interface MPROM extends Memory<BitVector>
{
	public BigInteger getCellAsBigInteger(long address);

	@Override
	public MPROMDefinition getDefinition();
}