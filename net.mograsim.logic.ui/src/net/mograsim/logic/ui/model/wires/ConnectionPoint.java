package net.mograsim.logic.ui.model.wires;

public interface ConnectionPoint
{
	/**
	 * Retrieves the {@link Pin}, that is used by the {@link GUIWire} to connect to.
	 * 
	 * @return the {@link Pin} for the wire to connect to.
	 * 
	 * @author Christian Femers
	 */
	Pin getPin();
}
