package net.mograsim.logic.model.verilog.model.expressions;

import java.util.Objects;
import java.util.Set;

import net.mograsim.logic.model.verilog.model.signals.Signal;

public class SignalReference extends Expression
{
	private final Signal referencedSignal;

	public SignalReference(Signal referencedSignal)
	{
		super(referencedSignal.getWidth());
		this.referencedSignal = Objects.requireNonNull(referencedSignal);
	}

	@Override
	public String toVerilogCode()
	{
		return referencedSignal.toReferenceVerilogCode();
	}

	@Override
	public Set<Signal> getReferencedSignals()
	{
		return Set.of(referencedSignal);
	}
}
