package net.mograsim.machine.mi;

import net.mograsim.machine.Memory;

public interface MicroInstructionMemory extends Memory<MicroInstruction>
{
	@Override
	public MicroInstructionMemoryDefinition getDefinition();
}
