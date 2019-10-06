package net.mograsim.machine.registers;

import net.mograsim.machine.Identifiable;

/**
 * A register in a machine is defined by this interface. A hardware register may have {@link Register#names() named sub-registers}.
 *
 *
 * @author Christian Femers
 *
 */
public interface Register extends Identifiable
{
	/**
	 * Returns the complete width in bits of the underlying hardware structure the register and possible sub-registers are part of.
	 * 
	 * @param name the name of the register
	 * @return the width of the (sub-)register in bits.
	 * 
	 * @see #names()
	 * @author Christian Femers
	 */
	int getWidth();
}