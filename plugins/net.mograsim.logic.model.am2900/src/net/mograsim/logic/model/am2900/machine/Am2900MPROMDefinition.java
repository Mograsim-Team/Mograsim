package net.mograsim.logic.model.am2900.machine;

import net.mograsim.machine.mi.MPROMDefinition;

public class Am2900MPROMDefinition implements MPROMDefinition
{
	public static final Am2900MPROMDefinition instance = new Am2900MPROMDefinition();

	@Override
	public int getMemoryAddressBits()
	{
		return 8;
	}

	@Override
	public long getMinimalAddress()
	{
		return 0;
	}

	@Override
	public long getMaximalAddress()
	{
		return 0xFF;
	}

	@Override
	public int getMicroInstructionMemoryAddressBits()
	{
		return 12;
	}

	private Am2900MPROMDefinition()
	{
	}
}