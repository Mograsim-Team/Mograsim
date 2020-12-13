package net.mograsim.logic.model.verilog.model.statements;

import java.util.Objects;
import java.util.Set;

import net.mograsim.logic.model.verilog.model.signals.Signal;
import net.mograsim.logic.model.verilog.model.signals.Wire;

public class WireDeclaration extends Statement
{
	private final Wire wire;

	public WireDeclaration(Wire wire)
	{
		this.wire = Objects.requireNonNull(wire);
	}

	public Wire getWire()
	{
		return wire;
	}

	@Override
	public String toVerilogCode()
	{
		return wire.toDeclarationVerilogCode();
	}

	@Override
	public Set<String> getDefinedNames()
	{
		return Set.of(wire.getName());
	}

	@Override
	public Set<Signal> getDefinedSignals()
	{
		return Set.of(wire);
	}

	@Override
	public Set<Signal> getReferencedSignals()
	{
		return Set.of();
	}

	@Override
	public String toString()
	{
		return "decl[" + wire.toString() + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wire == null) ? 0 : wire.hashCode());
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
		WireDeclaration other = (WireDeclaration) obj;
		if (wire == null)
		{
			if (other.wire != null)
				return false;
		} else if (!wire.equals(other.wire))
			return false;
		return true;
	}
}
