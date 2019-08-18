package net.mograsim.machine.isa.types;

public class AsmNumberFormatException extends AsmException
{
	private static final long serialVersionUID = 1L;

	public AsmNumberFormatException(Exception cause, String message, Object... formatArgs)
	{
		super(cause, message, formatArgs);
	}

	public AsmNumberFormatException(String message, Object... formatArgs)
	{
		super(message, formatArgs);
	}
}
