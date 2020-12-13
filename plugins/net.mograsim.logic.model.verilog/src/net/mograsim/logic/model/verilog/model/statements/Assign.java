package net.mograsim.logic.model.verilog.model.statements;

import java.util.Objects;
import java.util.Set;

import net.mograsim.logic.model.verilog.model.expressions.Expression;
import net.mograsim.logic.model.verilog.model.signals.NamedSignal;
import net.mograsim.logic.model.verilog.model.signals.Signal;
import net.mograsim.logic.model.verilog.utils.CollectionsUtils;

public class Assign extends Statement
{
	private final NamedSignal target;
	private final Expression source;

	public Assign(NamedSignal target, Expression source)
	{
		this.target = Objects.requireNonNull(target);
		this.source = Objects.requireNonNull(source);

		check();
	}

	private void check()
	{
		if (source.getWidth() != target.getWidth())
			throw new IllegalArgumentException("Signal widthes don't match");
	}

	public Signal getTarget()
	{
		return target;
	}

	public Expression getSource()
	{
		return source;
	}

	@Override
	public String toVerilogCode()
	{
		return "assign " + target.toReferenceVerilogCode() + " = " + source.toVerilogCode() + ";";
	}

	@Override
	public Set<String> getDefinedNames()
	{
		return Set.of();
	}

	@Override
	public Set<Signal> getDefinedSignals()
	{
		return Set.of();
	}

	@Override
	public Set<Signal> getReferencedSignals()
	{
		return CollectionsUtils.union(Set.of(target), source.getReferencedSignals());
	}

	@Override
	public String toString()
	{
		return target.getName() + " = " + source;
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
