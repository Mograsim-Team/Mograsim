package net.mograsim.machine;

import java.util.Set;

/**
 * A register in a machine is defined by this interface. A hardware register may
 * have {@link Register#names() named sub-registers}.
 *
 *
 * @author Christian Femers
 *
 */
public interface Register {
	/**
	 * The unique identifier of the register. This does not have to be the display
	 * name or name in the assembly language.
	 * 
	 * @return the registers id as case sensitive String
	 * @author Christian Femers
	 */
	String id();

	/**
	 * The name(s) of this register. This is the displayed name and the name used in
	 * the assembly language. All names of all registers must be case-insensitive
	 * unique. A register can have multiple names if these names address different
	 * regions (but still have a common hardware structure), e.g. <code>EAX</code>,
	 * <code>AX</code>, <code>AL</code>, <code>AH</code>.
	 * 
	 * @return all the names of regions addressing this register, must be
	 *         case-insensitive.
	 * @author Christian Femers
	 */
	Set<String> names();

	/**
	 * Returns the complete width in bits of the underlying hardware structure the
	 * register and possible sub-registers are part of.
	 * 
	 * @param name the name of the register
	 * @return the width of the (sub-)register in bits.
	 * 
	 * @see #names()
	 * @author Christian Femers
	 */
	int getWidth();

	/**
	 * Returns the width in bits of the register or a named part of it.
	 * 
	 * @param name the name of the register
	 * @return the width of the (sub-)register in bits.
	 * 
	 * @see #names()
	 * @author Christian Femers
	 */
	int getWidth(String name);
}
