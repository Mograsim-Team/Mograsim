package net.mograsim.logic.model.verilog.model;

import java.util.Objects;

public abstract class NamedSignal extends Signal
{
	private final String name;

	public NamedSignal(Type type, String name, int width)
	{
		super(type, width);
		this.name = Objects.requireNonNull(name);
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toReferenceVerilogCode()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name + "[" + getWidth() + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedSignal other = (NamedSignal) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
