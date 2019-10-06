package net.mograsim.logic.model.am2900.machine.registers.am2904.msr;

import net.mograsim.machine.registers.SimpleRegisterGroup;

public class MSRRegisterGroup extends SimpleRegisterGroup
{
	public static final MSRRegisterGroup instance = new MSRRegisterGroup();

	private MSRRegisterGroup()
	{
		super("MSR", MSRRegister.instance, MZRegister.instance, MCRegister.instance, MNRegister.instance, MOVRRegister.instance);
	}
}