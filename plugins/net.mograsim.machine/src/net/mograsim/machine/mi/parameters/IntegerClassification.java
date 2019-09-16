package net.mograsim.machine.mi.parameters;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class IntegerClassification implements ParameterClassification
{
	private final int bits;
	private final IntegerImmediate defaultValue;

	public IntegerClassification(int defaultValue, int bits)
	{
		this.bits = bits;
		this.defaultValue = new IntegerImmediate(BitVector.from(defaultValue, bits));
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

	@Override
	public MicroInstructionParameter getDefault()
	{
		return defaultValue;
	}
}
