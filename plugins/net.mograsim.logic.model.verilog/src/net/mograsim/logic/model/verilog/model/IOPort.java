package net.mograsim.logic.model.verilog.model;

public abstract class IOPort extends NamedSignal
{
	public IOPort(Type type, String name, int width)
	{
		super(type, name, width);
	}

	public abstract String toDeclarationVerilogCode();
}
