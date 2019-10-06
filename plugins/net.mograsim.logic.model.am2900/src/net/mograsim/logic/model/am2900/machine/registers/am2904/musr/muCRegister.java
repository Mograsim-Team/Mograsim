package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muCRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muCRegister instance = new muCRegister();

	private muCRegister()
	{
		super("C", "am2904.musr.q2", 1);
	}
}