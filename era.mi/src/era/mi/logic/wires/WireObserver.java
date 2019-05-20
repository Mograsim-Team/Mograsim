package era.mi.logic.wires;

import era.mi.logic.types.BitVector;

public interface WireObserver
{
	public void update(Wire initiator, BitVector oldValues);
}
