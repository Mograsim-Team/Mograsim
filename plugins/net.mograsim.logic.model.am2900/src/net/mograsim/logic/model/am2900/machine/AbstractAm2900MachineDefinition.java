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
public class AbstractAm2900MachineDefinition implements MachineDefinition
{
	public static final String SIMPLE_AM2900_MACHINE_ID = "Am2900Simple";
	public static final String STRICT_AM2900_MACHINE_ID = "Am2900Strict";

	public static final Set<Register> allRegisters;

	static
	{
		Set<Register> allRegistersModifiable = new HashSet<>();
		allRegistersModifiable.add(QRegister.instance);
		allRegistersModifiable.addAll(NumberedRegister.instancesCorrectOrder);
		allRegisters = Collections.unmodifiableSet(allRegistersModifiable);
	}

	public final boolean strict;

	protected AbstractAm2900MachineDefinition(boolean strict)
	{
		this.strict = strict;
	}

	@Override
	public String getId()
	{
		return strict ? STRICT_AM2900_MACHINE_ID : SIMPLE_AM2900_MACHINE_ID;
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
		return strict ? 12345 : 54321;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof AbstractAm2900MachineDefinition
				&& ((AbstractAm2900MachineDefinition) obj).strict == this.strict;
	}

	@Override
	public Am2900MicroInstructionMemoryDefinition getMicroInstructionMemoryDefinition()
	{
		return Am2900MicroInstructionMemoryDefinition.instance;
	}

}
