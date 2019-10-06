package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class MOVRRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final MOVRRegister instance = new MOVRRegister();

	private MOVRRegister()
	{
		super("OVR", "am2904.msr.q4", 1);
	}
}