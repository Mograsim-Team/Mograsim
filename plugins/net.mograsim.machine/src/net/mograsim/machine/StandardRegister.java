package net.mograsim.machine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class StandardRegister implements Register
{
	private final String id;
	private final Set<String> names;
	private final int width;
	private final Map<String, Integer> widthForAliases;

	public StandardRegister(String id, Set<String> names, int width, Map<String, Integer> widthForAliases)
	{
		this.id = id;
		this.names = Collections.unmodifiableSet(new HashSet<>(names));
		this.width = width;
		this.widthForAliases = Collections.unmodifiableMap(new HashMap<>(widthForAliases));
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public Set<String> names()
	{
		return names;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getWidth(String name)
	{
		return widthForAliases.getOrDefault(name, width);
	}
}