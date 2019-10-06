package net.mograsim.logic.model.am2900.machine.registers.am2901;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.machine.registers.Register;
import net.mograsim.machine.registers.SimpleRegisterGroup;

public class Am2901RegisterGroup extends SimpleRegisterGroup
{
	public static final Am2901RegisterGroup instance = new Am2901RegisterGroup();

	private Am2901RegisterGroup()
	{
		super("Am2901", getAllRegisters());
	}

	private static Register[] getAllRegisters()
	{
		List<Register> allRegistersModifiable = new ArrayList<>();
		allRegistersModifiable.addAll(NumberedRegister.instancesCorrectOrder);
		allRegistersModifiable.add(QRegister.instance);
		return allRegistersModifiable.toArray(Register[]::new);
	}
}