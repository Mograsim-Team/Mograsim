package net.mograsim.logic.model.verilog.utils;

import java.util.HashSet;
import java.util.Set;

public class CollectionsUtils
{
	private CollectionsUtils()
	{
	}

	@SafeVarargs
	public static <E> Set<E> union(Set<E>... sets)
	{
		Set<E> union = new HashSet<>();
		for (Set<E> set : sets)
			union.addAll(set);
		return Set.copyOf(union);
	}
}
