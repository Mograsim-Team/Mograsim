package net.mograsim.logic.model.am2900.machine.registers.am2910;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class SPRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final SPRegister instance = new SPRegister();

	private SPRegister()
	{
		super("Stack pointer", "am2910.sp.q", 3);
	}
}