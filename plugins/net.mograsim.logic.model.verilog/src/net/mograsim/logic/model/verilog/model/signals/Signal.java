package net.mograsim.logic.model.verilog.model.signals;

import java.util.Objects;

public abstract class Signal
{
	private final Type type;
	private final String name;
	private final int width;

	public Signal(Type type, String name, int width)
	{
		this.type = Objects.requireNonNull(type);
		this.name = Objects.requireNonNull(name);
		this.width = width;

		check();
	}

	private void check()
	{
		if (width <= 0)
			throw new IllegalArgumentException("Signal width is negative: " + width);
	}

	public Type getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public int getWidth()
	{
		return width;
	}

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
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + width;
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
		Signal other = (Signal) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	public static enum Type
	{
		WIRE, IO_INPUT, IO_OUTPUT;
	}
}
