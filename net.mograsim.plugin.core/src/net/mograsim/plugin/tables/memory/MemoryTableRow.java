package net.mograsim.plugin.tables.memory;

import net.mograsim.machine.MainMemory;

public class MemoryTableRow
{
	public final long address;
	private final MainMemory memory;

	public MemoryTableRow(long address, MainMemory memory)
	{
		this.address = address;
		this.memory = memory;
	}

	public MainMemory getMemory()
	{
		return memory;
	}
}
