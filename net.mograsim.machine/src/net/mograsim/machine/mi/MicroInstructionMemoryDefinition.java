package net.mograsim.machine.mi;

import net.mograsim.machine.MemoryDefinition;

public interface MicroInstructionMemoryDefinition extends MemoryDefinition
{
	MicroInstructionDefinition getMicroInstructionDefinition();
}
