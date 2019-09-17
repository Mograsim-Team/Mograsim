package net.mograsim.logic.model.am2900.machine;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public class Am2900MicroInstructionMemoryDefinition implements MicroInstructionMemoryDefinition
{
	public static final Am2900MicroInstructionMemoryDefinition instance = new Am2900MicroInstructionMemoryDefinition();

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
		return 0xFFF;
	}

	@Override
	public MicroInstructionDefinition getMicroInstructionDefinition()
	{
		return Am2900MicroInstructionDefinition.instance;
	}

	private Am2900MicroInstructionMemoryDefinition()
	{
	}
}