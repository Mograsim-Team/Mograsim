package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;

public class BooleanImmediate implements MicroInstructionParameter
{
	private boolean value;
	
	public BooleanImmediate(boolean value)
	{
		this.value = value;
	}
	
	@Override
	public BitVector getValue()
	{
		return value ? BitVector.SINGLE_1 : BitVector.SINGLE_0;
	}
	
	public boolean getBooleanValue()
	{
		return value;
	}

	@Override
	public ParameterType getType()
	{
		return ParameterType.BOOLEAN_IMMEDIATE;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BooleanImmediate))
			return false;
		BooleanImmediate other = (BooleanImmediate) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return value ? "H" : "L";
	}
}
