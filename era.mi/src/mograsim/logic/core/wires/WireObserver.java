package mograsim.logic.core.wires;

import mograsim.logic.core.types.BitVector;
import mograsim.logic.core.wires.Wire.ReadEnd;

public interface WireObserver
{
	public void update(ReadEnd initiator, BitVector oldValues);
}
