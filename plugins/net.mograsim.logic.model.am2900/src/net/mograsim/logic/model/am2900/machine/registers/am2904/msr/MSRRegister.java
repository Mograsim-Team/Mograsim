package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class MSRRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final MSRRegister instance = new MSRRegister();

	private MSRRegister()
	{
		super("MSR", "am2904.msr.q", 4);
	}
}