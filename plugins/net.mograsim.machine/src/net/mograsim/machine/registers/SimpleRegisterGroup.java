package net.mograsim.machine.registers;

import java.util.List;

public class SimpleRegisterGroup implements RegisterGroup
{
	private final String id;
	private final List<Register> registers;
	private final List<RegisterGroup> subgroups;

	protected SimpleRegisterGroup(String id, Register... registers)
	{
		this(id, new RegisterGroup[0], registers);
	}

	protected SimpleRegisterGroup(String id, RegisterGroup... subgroups)
	{
		this(id, subgroups, new Register[0]);
	}

	protected SimpleRegisterGroup(String id, RegisterGroup[] subgroups, Register... registers)
	{
		this.id = id;
		this.registers = List.of(registers);
		this.subgroups = List.of(subgroups);
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public List<Register> getRegisters()
	{
		return registers;
	}

	@Override
	public List<RegisterGroup> getSubGroups()
	{
		return subgroups;
	}
}