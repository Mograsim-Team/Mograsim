package net.mograsim.machine.mi;

import net.mograsim.machine.Memory;
import net.mograsim.machine.MemoryDefinition;

public interface MicroInstructionMemory extends Memory<MicroInstruction>
{
	public static MicroInstructionMemory create(MemoryDefinition def)
	{
		return new StandardMicroInstructionMemory(def);
	}
}
