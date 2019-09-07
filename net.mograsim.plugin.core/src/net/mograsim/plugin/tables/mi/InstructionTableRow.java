package net.mograsim.plugin.tables.mi;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.plugin.tables.TableRow;

public class InstructionTableRow extends TableRow<MicroInstruction>
{
	public InstructionTableRow(long address, MicroInstruction data)
	{
		super(address, data);
	}
}
