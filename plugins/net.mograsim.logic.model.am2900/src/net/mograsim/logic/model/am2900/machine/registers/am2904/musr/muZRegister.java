package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muZRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muZRegister instance = new muZRegister();

	private muZRegister()
	{
		super("Z", "am2904.musr.q1", 1);
	}
}