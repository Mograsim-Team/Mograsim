package net.mograsim.machine.mi;

import net.mograsim.machine.standard.memory.AbstractAssignableBitVectorMemory;

public class AssignableMPROM extends AbstractAssignableBitVectorMemory<MPROM> implements MPROM
{
	public AssignableMPROM(MPROM memory)
	{
		super(memory);
	}

	@Override
	public MPROMDefinition getDefinition()
	{
		return getReal().getDefinition();
	}
}
