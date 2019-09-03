package net.mograsim.logic.model.am2900.machine;

import net.mograsim.machine.MainMemoryDefinition;

public class Am2900MainMemoryDefinition implements MainMemoryDefinition
{

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

}
