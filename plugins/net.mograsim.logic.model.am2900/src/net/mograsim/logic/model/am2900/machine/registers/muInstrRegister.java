package net.mograsim.logic.model.am2900.machine.registers;

import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muInstrRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muInstrRegister instance = new muInstrRegister();

	private muInstrRegister()
	{
		super("\u00b5IR", "muir_2.q", 80);// 00b5 = micro symbol
	}
}