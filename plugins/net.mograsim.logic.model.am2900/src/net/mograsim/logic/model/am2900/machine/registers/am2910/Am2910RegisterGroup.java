package net.mograsim.logic.model.am2900.machine.registers.am2910;

import net.mograsim.logic.model.am2900.machine.registers.am2910.stack.Am2910StackRegisterGroup;
import net.mograsim.machine.registers.RegisterGroup;
import net.mograsim.machine.registers.SimpleRegisterGroup;

public class Am2910RegisterGroup extends SimpleRegisterGroup
{
	public static final Am2910RegisterGroup instance = new Am2910RegisterGroup();

	private Am2910RegisterGroup()
	{
		super("Am2910", new RegisterGroup[] { Am2910StackRegisterGroup.instance }, muPCRegister.instance, RegCntrRegister.instance,
				SPRegister.instance);
	}
}