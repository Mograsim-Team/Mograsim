package net.mograsim.machine;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnionRegisterGroup implements RegisterGroup
{
	private final String id;
	private final Set<RegisterGroup> subGroups;
	private final Set<Register> subGroupRegisters;

	public UnionRegisterGroup(String id, RegisterGroup... subGroups)
	{
		this.id = id;
		this.subGroups = Collections.unmodifiableSet(new HashSet<>(List.of(subGroups)));
		this.subGroupRegisters = this.subGroups.stream().flatMap(sg -> sg.getRegisters().stream()).distinct()
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public Set<Register> getRegisters()
	{
		return subGroupRegisters;
	}

	@Override
	public Set<RegisterGroup> getSubGroups()
	{
		return subGroups;
	}

}
