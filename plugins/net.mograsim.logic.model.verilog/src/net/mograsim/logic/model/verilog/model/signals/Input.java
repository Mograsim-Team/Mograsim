package net.mograsim.logic.model.verilog.model.signals;

public class Input extends IOPort
{
	public Input(String name, int width)
	{
		super(Type.IO_INPUT, name, width);
	}

	@Override
	public String toDeclarationVerilogCode()
	{
		return "input [" + (getWidth() - 1) + ":0] " + getName();
	}
}
