package net.mograsim.machine.mi.parameters;

import static net.mograsim.logic.core.types.Bit.X;

import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;

public final class IntegerImmediate implements MicroInstructionParameter
{
	private IntegerClassification owner;
	private BitVector value;

	/**
	 * <code>value == null</code> means a vector consisting only of X
	 */
	public IntegerImmediate(IntegerClassification owner, BigInteger value, int bits)
	{
		this(owner, value == null ? BitVector.of(X, bits) : BitVector.from(value, bits));
	}

	public IntegerImmediate(IntegerClassification owner, BitVector value)
	{
		this.owner = owner;
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

	public boolean isX()
	{
		return value.equals(BitVector.of(X, value.length()));
	}

	/**
	 * @return The value of this IntegerImmediate as an unsigned BigInteger, or null, if the value is X.
	 */
	public BigInteger getValueAsBigInteger()
	{
		return isX() ? null : value.getUnsignedValue();
	}

	@Override
	public String toString()
	{
		return isX() ? "X" : getValueAsBigInteger().toString();
	}

	@Override
	public boolean isDefault()
	{
		return equals(owner.getDefault());
	}
}
