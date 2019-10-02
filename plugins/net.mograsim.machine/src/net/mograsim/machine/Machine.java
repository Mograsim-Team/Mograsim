package net.mograsim.machine;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.machine.standard.memory.AssignableMainMemory;

public interface Machine
{
	MachineDefinition getDefinition();

	void reset();

	LogicModel getModel();

	// TODO replace with HLS references
	CoreClock getClock();

	BitVector getRegister(Register r);

	void setRegister(Register r, BitVector value);

	Timeline getTimeline();

	AssignableMainMemory getMainMemory();

	AssignableMicroInstructionMemory getMicroInstructionMemory();

	void addActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener listener);

	void removeActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener listener);

	long getActiveMicroInstructionAddress();

	public interface ActiveMicroInstructionChangedListener
	{
		public void instructionChanged(long oldAddress, long newAddress);
	}
}