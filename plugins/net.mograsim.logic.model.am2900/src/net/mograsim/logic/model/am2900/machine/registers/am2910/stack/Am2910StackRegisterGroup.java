package net.mograsim.logic.model.am2900.machine.registers.am2910.stack;

import net.mograsim.machine.registers.Register;
import net.mograsim.machine.registers.SimpleRegisterGroup;

public class Am2910StackRegisterGroup extends SimpleRegisterGroup
{
	public static final Am2910StackRegisterGroup instance = new Am2910StackRegisterGroup();

	private Am2910StackRegisterGroup()
	{
		super("Stack", StackCellRegister.instancesCorrectOrder.toArray(Register[]::new));
	}
}