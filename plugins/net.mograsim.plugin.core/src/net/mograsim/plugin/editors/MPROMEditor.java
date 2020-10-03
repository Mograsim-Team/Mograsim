package net.mograsim.plugin.editors;

import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.mi.StandardMPROM;

public class MPROMEditor extends AbstractMemoryEditor
{
	@Override
	protected BitVectorMemory createEmptyMemory(MachineDefinition activeMachineDefinition)
	{
		return new StandardMPROM(activeMachineDefinition.getMPROMDefinition());
	}
}
