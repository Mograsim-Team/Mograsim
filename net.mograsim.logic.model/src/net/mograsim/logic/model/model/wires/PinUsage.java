package net.mograsim.logic.model.model.wires;

public enum PinUsage
{
	/**
	 * The component never affects the value of the wire connected to the pin.
	 */
	INPUT,
	/**
	 * The component is never affected by the value of the wire connected to this pin. This includes the look of the component.
	 */
	OUTPUT,
	/**
	 * The component (sometimes) affects the value of the wire connected to the pin, but is also (sometimes) affected by the value of this
	 * wire.
	 */
	TRISTATE;
}