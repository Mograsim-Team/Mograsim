package net.mograsim.machine.mi.parameters;

import net.mograsim.logic.core.types.BitVector;

public final class Mnemonic implements MicroInstructionParameter
{
	private final String text;
	private final BitVector vector;
	
	public Mnemonic(String text, BitVector vector)
	{
		super();
		this.text = text;
		this.vector = vector;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public BitVector getValue()
	{
		return vector;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((vector == null) ? 0 : vector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Mnemonic))
			return false;
		Mnemonic other = (Mnemonic) obj;
		if (text == null)
		{
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (vector == null)
		{
			if (other.vector != null)
				return false;
		} else if (!vector.equals(other.vector))
			return false;
		return true;
	}

	@Override
	public ParameterType getType()
	{
		return ParameterType.MNEMONIC;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
