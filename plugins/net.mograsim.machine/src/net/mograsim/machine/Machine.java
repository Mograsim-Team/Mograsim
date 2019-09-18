package net.mograsim.machine;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;

public interface Machine
{
	MachineDefinition getDefinition();

	void reset();

	LogicModel getModel();

	CoreClock getClock();

	BitVector getRegister(Register r);

	void setRegister(Register r, BitVector value);

	Timeline getTimeline();

	MainMemory getMainMemory();

	AssignableMicroInstructionMemory getMicroInstructionMemory();

}