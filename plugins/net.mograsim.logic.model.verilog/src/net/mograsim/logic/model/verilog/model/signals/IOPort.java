package net.mograsim.logic.model.verilog.model.signals;

public abstract class IOPort extends NamedSignal
{
	public IOPort(Type type, String name, int width)
	{
		super(type, name, width);
	}

	public abstract String toDeclarationVerilogCode();
}
