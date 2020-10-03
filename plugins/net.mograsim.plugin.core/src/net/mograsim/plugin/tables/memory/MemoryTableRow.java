package net.mograsim.plugin.tables.memory;

import net.mograsim.machine.BitVectorMemory;
import net.mograsim.plugin.tables.TableRow;

public class MemoryTableRow extends TableRow<BitVectorMemory>
{
	public MemoryTableRow(long address, BitVectorMemory memory)
	{
		super(address, memory);
	}

	public BitVectorMemory getMemory()
	{
		return getData();
	}
}