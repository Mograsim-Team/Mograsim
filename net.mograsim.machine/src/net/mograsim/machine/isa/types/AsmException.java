package net.mograsim.machine.isa.types;

public class AsmException extends Exception
{
	private static final long serialVersionUID = 1L;

	public AsmException()
	{
		super();
	}

	public AsmException(Throwable cause, String message, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AsmException(Throwable cause, String formatString, Object... formatArgs)
	{
		super(String.format(formatString, formatArgs), cause);
	}

	public AsmException(String formatString, Object... formatArgs)
	{
		super(String.format(formatString, formatArgs));
	}

	public AsmException(Throwable cause)
	{
		super(cause);
	}

}
