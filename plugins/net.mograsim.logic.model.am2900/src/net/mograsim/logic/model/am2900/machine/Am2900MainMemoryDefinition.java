package net.mograsim.logic.model.am2900.machine;

import net.mograsim.machine.MainMemoryDefinition;

public class Am2900MainMemoryDefinition implements MainMemoryDefinition
{
	public static final Am2900MainMemoryDefinition instance = new Am2900MainMemoryDefinition();

	@Override
	public int getMemoryAddressBits()
	{
		return 16;
	}

	@Override
	public long getMinimalAddress()
	{
		return 0;
	}

	@Override
	public long getMaximalAddress()
	{
		return 0xFFFF;
	}

	@Override
	public int getCellWidth()
	{
		return 16;
	}

	private Am2900MainMemoryDefinition()
	{
	}
}