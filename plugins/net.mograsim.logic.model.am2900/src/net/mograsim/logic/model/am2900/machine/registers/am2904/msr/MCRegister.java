package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class MCRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final MCRegister instance = new MCRegister();

	private MCRegister()
	{
		super("C", "am2904.msr.q2", 1);
	}
}