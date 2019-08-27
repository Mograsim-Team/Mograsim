package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class IntegerClassification implements ParameterClassification
{
	private final int bits;
	
	public IntegerClassification(int bits)
	{
		this.bits = bits;
	}

	@Override
	public ParameterType getExpectedType()
	{
		return ParameterType.INTEGER_IMMEDIATE;
	}

	@Override
	public int getExpectedBits()
	{
		return bits;
	}
}
