package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class BooleanClassification implements ParameterClassification
{
	@Override
	public ParameterType getExpectedType()
	{
		return ParameterType.BOOLEAN_IMMEDIATE;
	}

	@Override
	public int getExpectedBits()
	{
		return 1;
	}
	
	@Override
	public BooleanImmediate parse(String toParse)
	{
		return new BooleanImmediate("H".equals(toParse));
	}
}
