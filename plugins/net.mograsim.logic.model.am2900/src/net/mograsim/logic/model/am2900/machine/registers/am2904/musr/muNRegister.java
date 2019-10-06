package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muNRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muNRegister instance = new muNRegister();

	private muNRegister()
	{
		super("N", "am2904.musr.q3", 1);
	}
}