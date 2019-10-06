package net.mograsim.machine.registers;

import java.util.List;

import net.mograsim.machine.Identifiable;

public interface RegisterGroup extends Identifiable
{
	/**
	 * Returns all Registers contained in this group that are not part of a subgroup.
	 */
	List<Register> getRegisters();

	/**
	 * Returns the sub groups of this group. May very well be an empty set.
	 */
	List<RegisterGroup> getSubGroups();
}
