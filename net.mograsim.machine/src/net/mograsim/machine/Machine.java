package net.mograsim.machine;

import net.mograsim.logic.core.components.Clock;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModel;

public interface Machine {
	MachineDefinition getDefinition();
	
	void reset();
	
	ViewModel getModel();
	
	Clock getClock();
	
	BitVector getRegister(Register r);
	
	void setRegister(Register r, BitVector value);
}
