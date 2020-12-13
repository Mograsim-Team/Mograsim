package net.mograsim.logic.model.verilog.converter;

import java.util.Objects;

public class PinNameBit
{
	private final String name;
	private final int bit;

	public PinNameBit(String name, int bit)
	{
		this.name = Objects.requireNonNull(name);
		this.bit = bit;

		check();
	}

	private void check()
	{
		if (bit < 0)
			throw new IllegalArgumentException("Bit out of range: " + bit);
	}

	public String getName()
	{
		return name;
	}

	public int getBit()
	{
		return bit;
	}

	@Override
	public String toString()
	{
		return name + "[" + bit + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + bit;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PinNameBit other = (PinNameBit) obj;
		if (bit != other.bit)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
