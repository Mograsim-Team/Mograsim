package net.mograsim.logic.model.verilog.model.expressions;

import java.util.Set;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.verilog.model.signals.Signal;

public class Constant extends Expression
{
	private final BitVector constant;

	public Constant(BitVector constant)
	{
		super(constant.length());
		this.constant = constant;

		check();
	}

	private void check()
	{
		if (!constant.isBinary())
			throw new IllegalArgumentException("Constant is not binary: " + constant);
	}

	public BitVector getConstant()
	{
		return constant;
	}

	@Override
	public String toVerilogCode()
	{
		return getWidth() + "'b" + constant.toBitstring();
	}

	@Override
	public Set<Signal> getReferencedSignals()
	{
		return Set.of();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((constant == null) ? 0 : constant.hashCode());
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
		Constant other = (Constant) obj;
		if (constant == null)
		{
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}
}
