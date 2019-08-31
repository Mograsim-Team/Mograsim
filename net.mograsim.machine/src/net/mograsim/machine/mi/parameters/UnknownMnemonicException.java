package net.mograsim.machine.mi.parameters;

import net.mograsim.machine.MachineException;

public class UnknownMnemonicException extends MachineException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 701558899889830975L;

	public UnknownMnemonicException()
	{
		super();
	}
	
	public UnknownMnemonicException(String message)
	{
		super(message);
	}
	
	public UnknownMnemonicException(Throwable cause)
	{
		super(cause);
	}
}
