package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class MNRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final MNRegister instance = new MNRegister();

	private MNRegister()
	{
		super("N", "am2904.msr.q3", 1);
	}
}