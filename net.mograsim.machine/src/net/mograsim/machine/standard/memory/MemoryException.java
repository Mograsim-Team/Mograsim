package net.mograsim.machine.standard.memory;

import net.mograsim.machine.MachineException;

public class MemoryException extends MachineException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6416796315595721079L;

	public MemoryException()
	{
		super();
	}
	
	public MemoryException(String message)
	{
		super(message);
	}
	
	public MemoryException(Throwable cause)
	{
		super(cause);
	}
}
