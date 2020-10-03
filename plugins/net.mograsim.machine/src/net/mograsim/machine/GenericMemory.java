package net.mograsim.machine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class GenericMemory<T, D extends MemoryDefinition> implements Memory<T>
{
	private final long minimalAddress, maximalAddress;
	private final D definition;
	private final int pageSize = 64;
	private Set<MemoryCellModifiedListener> observers = new HashSet<>();

	private HashMap<Long, Page> pages;

	public GenericMemory(D definition)
	{
		super();
		this.definition = definition;
		this.minimalAddress = definition.getMinimalAddress();
		this.maximalAddress = definition.getMaximalAddress();
		this.pages = new HashMap<>();
	}

	private void inBoundsCheck(long address)
	{
		if (address < minimalAddress || address > maximalAddress)
			throw new IndexOutOfBoundsException(String.format("Memory address out of bounds! Minimum: %d Maximum: %d Actual: %d",
					minimalAddress, maximalAddress, address));
	}

	private long page(long address)
	{
		return address / pageSize;
	}

	private int offset(long address)
	{
		return (int) (address % pageSize);
	}

	@Override
	public void setCell(long address, T data)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			pages.put(page, p = new Page());
		p.setCell(offset, data);
		notifyObservers(address);
	}

	@Override
	public T getCell(long address)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			return null;
		return p.getCell(offset);
	}

	private class Page
	{
		private T[] memory;

		@SuppressWarnings("unchecked")
		public Page()
		{
			memory = (T[]) new Object[pageSize];
		}

		public T getCell(int index)
		{
			return memory[index];
		}

		public void setCell(int index, T data)
		{
			memory[index] = data;
		}

		@Override
		public String toString()
		{
			return Arrays.deepToString(memory);
		}
	}

	@Override
	public void registerCellModifiedListener(MemoryCellModifiedListener ob)
	{
		observers.add(ob);
	}

	@Override
	public void deregisterCellModifiedListener(MemoryCellModifiedListener ob)
	{
		observers.remove(ob);
	}

	protected void notifyObservers(long address)
	{
		observers.forEach(ob -> ob.update(address));
	}

	@Override
	public D getDefinition()
	{
		return definition;
	}
}
