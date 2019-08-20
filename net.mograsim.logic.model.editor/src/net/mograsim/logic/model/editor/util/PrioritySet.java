package net.mograsim.logic.model.editor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * Similar to a SortedSet, except it is allowed for multiple elements to have the same priority (<code>c.compare(e1, e2) == 0</code> is
 * allowed to be true for two different elements e1 and e2). However, to elements are not allowed to be equal according to
 * <code>Object.equals(Object o)</code>.
 * 
 * @author Fabian Stemmler
 *
 * @param <T> the type of elements in this list
 */
public class PrioritySet<T> implements Set<T>
{
	private ArrayList<T> list;
	private Comparator<T> c;

	public PrioritySet(Comparator<T> c)
	{
		setComparator(c);
		list = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public PrioritySet()
	{
		this((e1, e2) -> ((Comparable<T>) e1).compareTo(e2));
	}

	public void setComparator(Comparator<T> c)
	{
		this.c = c;
	}

	//@formatter:off
	@Override
	public int size() { return list.size(); }
	@Override
	public boolean isEmpty() { return list.isEmpty(); }
	@Override
	public boolean contains(Object o) { return list.isEmpty(); }
	@Override
	public Iterator<T> iterator() { return list.iterator(); }
	@Override
	public Object[] toArray() { return list.toArray(); }
	@Override
	public <E> E[] toArray(E[] a) { return list.toArray(a); }
	@Override
	public boolean remove(Object o) { return list.remove(o); }
	@Override
	public boolean containsAll(Collection<?> c) { return list.containsAll(c); }
	@Override
	public boolean removeAll(Collection<?> c) { return list.removeAll(c); }
	@Override
	public boolean retainAll(Collection<?> c) { return list.retainAll(c); }
	@Override
	public void clear() { list.clear(); }
	//@formatter:on

	@Override
	public boolean add(T e)
	{
		if (isEmpty())
		{
			list.add(e);
			return true;
		}
		int index = Collections.binarySearch(list, e, c);
		if (index < 0)
			index = -index - 1;
		if (index < size())
		{
			if (list.get(index).equals(e))
				return false;
			list.add(index, e);
		} else
			list.add(e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		return c.stream().map(e -> add(e)).reduce(false, (a, b) -> a || b);
	}

	@Override
	public String toString()
	{
		return list.toString();
	}
}
