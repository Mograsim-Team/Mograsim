package net.mograsim.logic.model.am2900.machine.registers.am2904.musr;

import net.mograsim.machine.registers.SimpleRegisterGroup;

public class muSRRegisterGroup extends SimpleRegisterGroup
{
	public static final muSRRegisterGroup instance = new muSRRegisterGroup();

	private muSRRegisterGroup()
	{
		super("\u00b5SR", muSRRegister.instance, muZRegister.instance, muCRegister.instance, muNRegister.instance, muOVRRegister.instance);
		// 00b5 = micro symbol
	}
}