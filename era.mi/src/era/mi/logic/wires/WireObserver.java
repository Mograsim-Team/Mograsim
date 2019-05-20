package era.mi.logic.wires;

import era.mi.logic.types.Bit;

public interface WireObserver
{
	public void update(Wire initiator, Bit[] oldValues);
}
