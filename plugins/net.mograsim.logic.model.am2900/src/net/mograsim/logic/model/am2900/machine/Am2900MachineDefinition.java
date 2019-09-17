package net.mograsim.logic.model.am2900.machine;

import java.util.Objects;
import java.util.Set;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.machine.ISASchema;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;
import net.mograsim.machine.Register;

public class Am2900MachineDefinition implements MachineDefinition
{
	private Am2900MainMemoryDefinition memoryDefinition = new Am2900MainMemoryDefinition();
	private Am2900MicroInstructionMemoryDefinition microInstMemoryDefinition = new Am2900MicroInstructionMemoryDefinition();
	private final static Am2900MachineDefinition instance = new Am2900MachineDefinition();

	public static Am2900MachineDefinition getInstance()
	{
		return Objects.requireNonNullElseGet((Am2900MachineDefinition) MachineRegistry.getinstalledMachines().get("Am2900"),
				() -> instance);
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
		return memoryDefinition;
	}

	@Override
	public Am2900MicroInstructionMemoryDefinition getMicroInstructionMemoryDefinition()
	{
		return microInstMemoryDefinition;
	}
}
