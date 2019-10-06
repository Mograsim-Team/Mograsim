package net.mograsim.machine;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleRegisterGroup implements RegisterGroup
{

	private final String id;
	private final Set<Register> registers;

	protected SimpleRegisterGroup(String id, Register... registers)
	{
		this.id = id;
		this.registers = Collections.unmodifiableSet(new HashSet<>(List.of(registers)));
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public Set<Register> getRegisters()
	{
		return registers;
	}

	@Override
	public Set<RegisterGroup> getSubGroups()
	{
		return Set.of();
	}

}
