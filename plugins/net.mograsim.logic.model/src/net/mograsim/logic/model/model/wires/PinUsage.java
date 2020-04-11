package net.mograsim.logic.model.model.wires;

public enum PinUsage
{
	/**
	 * The component never applies a value (other than Z) to the wire connected to the pin.
	 */
	INPUT,
	/**
	 * The component expects that the wire is never pulled to a value (other than Z) by another component.
	 */
	OUTPUT,
	/**
	 * The component is free to use the pin in any way.
	 */
	TRISTATE;

	private PinUsage opposite;

	static
	{
		INPUT.opposite = OUTPUT;
		OUTPUT.opposite = INPUT;
		TRISTATE.opposite = TRISTATE;
	}

	public PinUsage getOpposite()
	{
		return opposite;
	}
}