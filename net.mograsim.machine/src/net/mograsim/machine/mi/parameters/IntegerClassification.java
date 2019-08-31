package net.mograsim.machine.mi.parameters;

import java.math.BigInteger;

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
	
	@Override
	public IntegerImmediate parse(String toParse)
	{
		return new IntegerImmediate(new BigInteger(toParse), bits);
	}
}
