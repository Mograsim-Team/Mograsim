package net.mograsim.plugin.editors;

import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.StandardMainMemory;

public class MainMemoryEditor extends AbstractMemoryEditor
{
	@Override
	protected BitVectorMemory createEmptyMemory(MachineDefinition activeMachineDefinition)
	{
		return new StandardMainMemory(activeMachineDefinition.getMainMemoryDefinition());
	}
}
