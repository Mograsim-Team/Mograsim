package net.mograsim.plugin.editors;

import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.mi.StandardMPROM;

public class MPROMEditor extends AbstractMemoryEditor
{
	public MPROMEditor()
	{
		super("Opcode", "\u00b5PC"); // 00b5 = mu / micro symbol
	}

	@Override
	protected BitVectorMemory createEmptyMemory(MachineDefinition activeMachineDefinition)
	{
		return new StandardMPROM(activeMachineDefinition.getMPROMDefinition());
	}
}
