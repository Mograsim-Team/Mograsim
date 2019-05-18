package era.mi.logic.wires;

import era.mi.logic.Bit;

public interface WireArrayObserver {
	public void update(WireArray initiator, Bit[] oldValues);
}
