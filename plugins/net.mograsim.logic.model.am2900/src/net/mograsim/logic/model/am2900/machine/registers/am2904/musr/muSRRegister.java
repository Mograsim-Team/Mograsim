package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muSRRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muSRRegister instance = new muSRRegister();

	private muSRRegister()
	{
		super("\u00b5SR", "am2904.musr.q", 4); // 00b5 = micro symbol
	}
}