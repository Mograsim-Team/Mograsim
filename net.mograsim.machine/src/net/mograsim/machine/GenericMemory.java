package net.mograsim.machine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class GenericMemory<T> implements Memory<T>
{
	private final long minimalAddress, maximalAddress;
	private final MemoryDefinition definition;
	private final int pageSize = 64;
	private Set<MemoryObserver> observers = new HashSet<>();

	private HashMap<Long, Page> pages;

	public GenericMemory(MemoryDefinition definition)
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
		private Object[] memory;

		public Page()
		{
			memory = new Object[pageSize];
		}

		public T getCell(int index)
		{
			@SuppressWarnings("unchecked")
			T data = (T) memory[index];
			return data;
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
	public void registerObserver(MemoryObserver ob)
	{
		observers.add(ob);
	}

	@Override
	public void deregisterObserver(MemoryObserver ob)
	{
		observers.remove(ob);
	}

	@Override
	public void notifyObservers(long address)
	{
		observers.forEach(ob -> ob.update(address));
	}
	
	@Override
	public MemoryDefinition getDefinition()
	{
		return definition;
	}
}
