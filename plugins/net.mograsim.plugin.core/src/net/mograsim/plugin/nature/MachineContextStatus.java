package net.mograsim.plugin.nature;

public enum MachineContextStatus
{
	/**
	 * A machine is currently associated and maybe running.
	 */
	@Deprecated(forRemoval = true)
	ACTIVE,
	/**
	 * A machine is currently associated and maybe running, but its ID does not match the current definition.
	 */
	@Deprecated(forRemoval = true)
	ACTIVE_CHANGED,
	/**
	 * The project can be actively used. The project must exist, be currently valid (uses a machine id that is known to Mograsim at runtime)
	 * and opened.
	 */
	READY,
	/**
	 * The project exists, has Mograsim nature and some machine id at all.
	 */
	INTACT,
	/**
	 * The project is closed now but exists
	 */
	CLOSED,
	/**
	 * The project lost it's machine id / Mograsim nature or other important properties to make it work.
	 */
	BROKEN,
	/**
	 * The project got deleted or similar.
	 */
	DEAD,
	/**
	 * Initial state
	 */
	UNKOWN;
}