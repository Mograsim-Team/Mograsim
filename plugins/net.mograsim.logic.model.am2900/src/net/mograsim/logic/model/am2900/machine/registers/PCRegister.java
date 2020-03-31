package net.mograsim.logic.model.am2900.machine.registers;

import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class PCRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final PCRegister instance = new PCRegister();

	private PCRegister()
	{
		super("PC", "pc.q", 16);
	}
}