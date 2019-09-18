package net.mograsim.machine.mi;

import net.mograsim.machine.Memory;

public interface MicroInstructionMemory extends Memory<MicroInstruction>
{
	@Override
	public MicroInstructionMemoryDefinition getDefinition();

	public void registerActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener ob);

	public void deregisterActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener ob);

	public void setActiveInstruction(long address);

	public static interface ActiveMicroInstructionChangedListener
	{
		public void activeMicroInstructionChanged(long address);
	}
}
