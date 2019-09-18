package net.mograsim.logic.model.am2900.machine;

import java.util.Set;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.machine.ISASchema;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.Register;

//we can't use the Singleton pattern here because a MachineDefinition needs a public parameterless constructor
//(used for detecting installed machines in plugin.core)
public class Am2900MachineDefinition implements MachineDefinition
{
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
		// TODO Auto-generated method stub
		return null;
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