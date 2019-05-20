package era.mi.logic.wires;

import era.mi.logic.Bit;

public interface WireObserver
{
	public void update(Wire initiator, Bit[] oldValues);
}
