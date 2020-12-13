package net.mograsim.logic.model.verilog.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UnionFind<E>
{
	private final Map<E, UnionFindElement<E>> elements;

	public UnionFind()
	{
		this.elements = new HashMap<>();
	}

	public UnionFindElement<E> getElement(E e)
	{
		return elements.computeIfAbsent(e, UnionFindElement::new);
	}

	public E find(E e)
	{
		return find(getElement(e)).getE();
	}

	public static <E> UnionFindElement<E> find(UnionFindElement<E> elem)
	{
		if (elem == elem.parent)
			return elem;
		return elem.parent = find(elem.parent);
	}

	public E union(E e1, E e2)
	{
		return union(getElement(e1), getElement(e2)).getE();
	}

	public static <E> UnionFindElement<E> union(UnionFindElement<E> e1, UnionFindElement<E> e2)
	{
		UnionFindElement<E> e1Root = find(e1);
		UnionFindElement<E> e2Root = find(e2);

		if (e1Root == e2Root)
			return e1Root;

		if (e1Root.rank < e2Root.rank)
			return e1Root.parent = e2Root;
		else if (e1Root.rank > e2Root.rank)
			return e2Root.parent = e1Root;
		else
		{
			e2Root.rank++;
			return e1Root.parent = e2Root;
		}
	}

	public E unionAll(Collection<E> es)
	{
		Iterator<E> it = es.iterator();
		if (!it.hasNext())
			return null;

		UnionFindElement<E> representant = getElement(it.next());

		while (it.hasNext())
			representant = union(representant, getElement(it.next()));

		return representant.getE();
	}

	public static <E> UnionFindElement<E> unionAll2(Collection<UnionFindElement<E>> es)
	{
		Iterator<UnionFindElement<E>> it = es.iterator();
		if (!it.hasNext())
			return null;

		UnionFindElement<E> representant = it.next();

		while (it.hasNext())
			representant = union(representant, it.next());

		return representant;
	}

	public static class UnionFindElement<E>
	{
		private final E e;
		private UnionFindElement<E> parent;
		private int rank;

		private UnionFindElement(E e)
		{
			this.e = e;
			this.parent = this;
		}

		public E getE()
		{
			return e;
		}
	}
}
