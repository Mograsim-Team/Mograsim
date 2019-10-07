package net.mograsim.logic.model.am2900.machine.registers.am2910;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class RegCntrRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final RegCntrRegister instance = new RegCntrRegister();

	private RegCntrRegister()
	{
		super("Register / Counter", "am2910.r.q", 12);
	}
}