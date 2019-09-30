package net.mograsim.logic.model.am2900.machine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.model.am2900.machine.registers.NumberedRegister;
import net.mograsim.logic.model.am2900.machine.registers.QRegister;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.machine.ISASchema;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.Register;

//we can't use the Singleton pattern here because a MachineDefinition needs a public parameterless constructor
//(used for detecting installed machines in plugin.core)
public class Am2900MachineDefinition implements MachineDefinition
{
	public static final String AM2900_MACHINE_ID = "Am2900";

	public static final Set<Register> allRegisters;

	static
	{
		Set<Register> allRegistersModifiable = new HashSet<>();
		allRegistersModifiable.add(QRegister.instance);
		allRegistersModifiable.addAll(NumberedRegister.instancesCorrectOrder);
		allRegisters = Collections.unmodifiableSet(allRegistersModifiable);
	}

	@Override
	public String getId()
	{
		return AM2900_MACHINE_ID;
	}

	@Override
	public Am2900Machine createNew()
	{
		return createNew(new LogicModelModifiable());
	}

	public Am2900Machine createNew(LogicModelModifiable model)
	{
		return new Am2900Machine(model, this);
	}

	@Override
	public ISASchema getISASchema()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Register> getRegisters()
	{
		return allRegisters;
	}

	@Override
	public int getAddressBits()
	{
		return 16;
	}

	@Override
	public Am2900MainMemoryDefinition getMainMemoryDefinition()
	{
		return Am2900MainMemoryDefinition.instance;
	}

	@Override
	public int hashCode()
	{
		return 12345;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof Am2900MachineDefinition;
	}

	@Override
	public Am2900MicroInstructionMemoryDefinition getMicroInstructionMemoryDefinition()
	{
		return Am2900MicroInstructionMemoryDefinition.instance;
	}

}
