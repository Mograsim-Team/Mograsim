package net.mograsim.plugin.tables.memory;

import net.mograsim.machine.MainMemory;
import net.mograsim.plugin.tables.TableRow;

public class MemoryTableRow extends TableRow<MainMemory>
{
	public MemoryTableRow(long address, MainMemory memory)
	{
		super(address, memory);
	}

	public MainMemory getMemory()
	{
		return getData();
	}
}