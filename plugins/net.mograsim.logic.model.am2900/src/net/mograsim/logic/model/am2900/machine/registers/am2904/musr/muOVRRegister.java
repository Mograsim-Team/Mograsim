package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class muOVRRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final muOVRRegister instance = new muOVRRegister();

	private muOVRRegister()
	{
		super("OVR", "am2904.musr.q4", 1);
	}
}