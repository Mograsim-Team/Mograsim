package net.mograsim.logic.core.wires;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public interface WireObserver
{
	public void update(ReadEnd initiator, BitVector oldValues);
}
