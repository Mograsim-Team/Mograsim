package net.mograsim.logic.model.am2900.machine.registers.am2904;

import net.mograsim.logic.model.am2900.machine.registers.am2904.msr.MSRRegisterGroup;
import net.mograsim.logic.model.am2900.machine.registers.am2904.musr.muSRRegisterGroup;
import net.mograsim.machine.registers.SimpleRegisterGroup;

public class Am2904RegisterGroup extends SimpleRegisterGroup
{
	public static final Am2904RegisterGroup instance = new Am2904RegisterGroup();

	private Am2904RegisterGroup()
	{
		super("Am2904", MSRRegisterGroup.instance, muSRRegisterGroup.instance);
	}
}