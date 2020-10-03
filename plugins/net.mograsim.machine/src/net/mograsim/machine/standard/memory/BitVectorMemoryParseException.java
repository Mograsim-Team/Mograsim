package net.mograsim.machine.standard.memory;

import net.mograsim.machine.MachineException;

public class BitVectorMemoryParseException extends MachineException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6820101808901789906L;

	public BitVectorMemoryParseException()
	{
		super();
	}

	public BitVectorMemoryParseException(String message)
	{
		super(message);
	}

	public BitVectorMemoryParseException(Throwable cause)
	{
		super(cause);
	}

}
