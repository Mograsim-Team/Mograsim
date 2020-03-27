package net.mograsim.logic.model.am2900.machine.registers;

import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class InstrRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final InstrRegister instance = new InstrRegister();

	private InstrRegister()
	{
		super("IR", "ir.q", 16);
	}
}