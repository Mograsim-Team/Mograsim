package net.mograsim.logic.model.verilog.model.signals;

public class Wire extends Signal
{
	public Wire(String name, int width)
	{
		super(Type.WIRE, name, width);
	}

	public String toDeclarationVerilogCode()
	{
		return "wire [" + getWidth() + ":0] " + getName() + ";";
	}
}
