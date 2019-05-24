package era.mi.logic.wires;

import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.ReadEnd;

public interface WireObserver
{
	public void update(ReadEnd initiator, BitVector oldValues);
}
