package net.mograsim.logic.model.verilog.model;

import net.mograsim.logic.core.types.BitVector;

public class Constant extends Signal
{
	private final BitVector constant;

	public Constant(BitVector constant)
	{
		super(Type.CONSTANT, constant.length());
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
	public String toReferenceVerilogCode()
	{
		return getWidth() + "'b" + constant.toBitstring();
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
