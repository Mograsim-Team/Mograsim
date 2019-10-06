package net.mograsim.machine;

import java.util.Set;

public interface RegisterGroup extends Identifiable
{
	/**
	 * Returns all Registers contained in this group and subgroups
	 */
	Set<Register> getRegisters();

	/**
	 * Returns the sub groups of this group. May very well be an empty set.
	 */
	Set<RegisterGroup> getSubGroups();
}
