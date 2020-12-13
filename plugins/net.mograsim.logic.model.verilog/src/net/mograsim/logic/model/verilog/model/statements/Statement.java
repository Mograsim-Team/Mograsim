package net.mograsim.logic.model.verilog.model.statements;

import java.util.Set;

import net.mograsim.logic.model.verilog.model.signals.Signal;

public abstract class Statement
{
	public abstract String toVerilogCode();

	public abstract Set<String> getDefinedNames();

	public abstract Set<Signal> getDefinedSignals();

	public abstract Set<Signal> getReferencedSignals();
}
