package net.mograsim.logic.model.verilog.model;

import java.util.Objects;

public class Assign
{
	private final Signal source;
	private final NamedSignal target;

	public Assign(Signal source, NamedSignal target)
	{
		this.source = Objects.requireNonNull(source);
		this.target = Objects.requireNonNull(target);

		check();
	}

	private void check()
	{
		if (source.getWidth() != target.getWidth())
			throw new IllegalArgumentException("Signal widthes don't match");
	}

	public Signal getSource()
	{
		return source;
	}

	public Signal getTarget()
	{
		return target;
	}

	public String toVerilogCode()
	{
		return "assign " + target.toReferenceVerilogCode() + " = " + source.toReferenceVerilogCode() + ";";
	}

	@Override
	public String toString()
	{
		return target.getName() + " = " + source.toReferenceVerilogCode();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		Assign other = (Assign) obj;
		if (source == null)
		{
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null)
		{
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
