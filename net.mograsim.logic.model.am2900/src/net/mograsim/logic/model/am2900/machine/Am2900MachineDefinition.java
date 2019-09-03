package net.mograsim.logic.model.am2900.machine;

import java.util.Set;

import net.mograsim.machine.ISASchema;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.Register;

public class Am2900MachineDefinition implements MachineDefinition
{
	private MainMemoryDefinition memoryDefinition = new Am2900MainMemoryDefinition();

	@Override
	public Machine createNew()
	{
		return new Am2900Machine(this);
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
	public MainMemoryDefinition getMainMemoryDefinition()
	{
		return memoryDefinition;
	}

}
