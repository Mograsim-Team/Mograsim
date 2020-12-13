package net.mograsim.logic.model.verilog.utils;

import java.util.HashSet;
import java.util.Set;

public class CollectionsUtils
{
	private CollectionsUtils()
	{
	}

	public static <E> Set<E> union(Set<E> a, Set<E> b)
	{
		Set<E> union = new HashSet<>();
		union.addAll(a);
		union.addAll(b);
		return Set.copyOf(union);
	}
}
