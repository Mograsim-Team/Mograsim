package net.mograsim.logic.model.am2900.machine;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public class Am2900MicroInstructionMemoryDefinition implements MicroInstructionMemoryDefinition
{

	@Override
	public int getMemoryAddressBits()
	{
		return 12;
	}

	@Override
	public long getMinimalAddress()
	{
		return 0;
	}

	@Override
	public long getMaximalAddress()
	{
		return 4096;
	}

	@Override
	public MicroInstructionDefinition getMicroInstructionDefinition()
	{
		return new Am2900MicroInstructionDefinition();
	}
}
