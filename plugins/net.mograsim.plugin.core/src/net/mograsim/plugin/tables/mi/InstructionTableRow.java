package net.mograsim.plugin.tables.mi;

import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.plugin.tables.TableRow;

public class InstructionTableRow extends TableRow<MicroInstructionMemory>
{
	public InstructionTableRow(long address, MicroInstructionMemory data)
	{
		super(address, data);
	}
}