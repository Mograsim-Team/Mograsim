package net.mograsim.logic.model.am2900.machine.registers.am2910;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muPCRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muPCRegister instance = new muPCRegister();

	private muPCRegister()
	{
		super("\u00b5PC", "am2910.mupc.q", 12);// 00b5 = micro symbol
	}
}