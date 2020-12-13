package net.mograsim.logic.model.verilog.model.expressions;

import java.util.Set;

import net.mograsim.logic.model.verilog.model.signals.Signal;

public abstract class Expression
{
	private final int width;

	public Expression(int width)
	{
		this.width = width;
	}

	public int getWidth()
	{
		return width;
	}

	public abstract String toVerilogCode();

	public abstract Set<Signal> getReferencedSignals();
}
