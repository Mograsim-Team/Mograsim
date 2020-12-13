package net.mograsim.logic.model.verilog.model.signals;

public class Output extends IOPort
{
	public Output(String name, int width)
	{
		super(Type.IO_OUTPUT, name, width);
	}

	@Override
	public String toDeclarationVerilogCode()
	{
		return "output [" + (getWidth() - 1) + ":0] " + getName();
	}
}
