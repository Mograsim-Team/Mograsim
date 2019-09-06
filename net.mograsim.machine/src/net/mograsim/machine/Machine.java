package net.mograsim.machine;

import net.mograsim.logic.core.components.Clock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.machine.mi.MicroInstructionMemory;

public interface Machine {
	MachineDefinition getDefinition();
	
	void reset();
	
	ViewModel getModel();
	
	Clock getClock();
	
	BitVector getRegister(Register r);
	
	void setRegister(Register r, BitVector value);

	Timeline getTimeline();
	
	MainMemory getMainMemory();
	
	MicroInstructionMemory getMicroInstructionMemory();
	
}
