package net.mograsim.logic.model.verilog.model;

public class Input extends IOPort
{
	public Input(String name, int width)
	{
		super(Type.IO_INPUT, name, width);
	}

	@Override
	public String toDeclarationVerilogCode()
	{
		return "input [" + getWidth() + ":0] " + getName();
	}
}
