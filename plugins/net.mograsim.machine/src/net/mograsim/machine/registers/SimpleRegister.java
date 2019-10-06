package net.mograsim.machine.registers;

public class SimpleRegister implements Register
{
	private final String id;
	private final int width;

	public SimpleRegister(String id, int width)
	{
		this.id = id;
		this.width = width;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public int getWidth()
	{
		return width;
	}
}