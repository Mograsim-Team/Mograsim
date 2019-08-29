package net.mograsim.machine.mi.parameters;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;

public final class IntegerImmediate implements MicroInstructionParameter
{
	private BitVector value;
	
	public IntegerImmediate(BigInteger value, int bits)
	{
		this.value = BitVector.from(value, bits);
	}
	
	public IntegerImmediate(BitVector value)
	{
		this.value = value;
	}
	
	@Override
	public BitVector getValue()
	{
		return value;
	}

	@Override
	public ParameterType getType()
	{
		return ParameterType.INTEGER_IMMEDIATE;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntegerImmediate))
			return false;
		IntegerImmediate other = (IntegerImmediate) obj;
		if (value == null)
		{
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * @return The value of this IntegerImmediate as an unsigned BigInteger
	 */
	public BigInteger getValueAsBigInteger()
	{
		return value.getUnsignedValue();
	}
	
	@Override
	public String toString()
	{
		return getValueAsBigInteger().toString();
	}
}
