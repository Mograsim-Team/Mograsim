package net.mograsim.machine;

public class MachineException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6224307211078376836L;

	public MachineException()
	{
		super();
	}

	public MachineException(String message)
	{
		super(message);
	}

	public MachineException(Throwable cause)
	{
		super(cause);
	}
}
