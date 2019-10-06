package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class MZRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final MZRegister instance = new MZRegister();

	private MZRegister()
	{
		super("Z", "am2904.msr.q1", 1);
	}
}