package net.mograsim.machine.mi;

import net.mograsim.machine.Memory;
import net.mograsim.machine.MemoryDefinition;

public interface MicroprogramMemory extends Memory<MicroInstruction>
{
	public static MicroprogramMemory create(MemoryDefinition def)
	{
		return new StandardMicroprogramMemory(def);
	}
}
