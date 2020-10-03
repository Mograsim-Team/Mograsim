package net.mograsim.machine;

import net.mograsim.machine.standard.memory.AbstractAssignableBitVectorMemory;

public class AssignableMainMemory extends AbstractAssignableBitVectorMemory<MainMemory> implements MainMemory
{
	public AssignableMainMemory(MainMemory memory)
	{
		super(memory);
	}

	@Override
	public MainMemoryDefinition getDefinition()
	{
		return getReal().getDefinition();
	}
}
