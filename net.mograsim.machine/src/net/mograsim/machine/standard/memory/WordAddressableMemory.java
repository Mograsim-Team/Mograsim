package net.mograsim.machine.standard.memory;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.MemoryObserver;

public class WordAddressableMemory implements MainMemory
{
	private final int cellWidth;
	private final long minimalAddress, maximalAddress;
	private final MainMemoryDefinition definition;
	private final int pageSize = 64;
	private Set<MemoryObserver> observers = new HashSet<>();

	private HashMap<Long, Page> pages;

	public WordAddressableMemory(MainMemoryDefinition definition)
	{
		super();
		this.cellWidth = definition.getCellWidth();
		this.minimalAddress = definition.getMinimalAddress();
		this.maximalAddress = definition.getMaximalAddress();
		this.definition = definition;
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
	public void setCell(long address, BitVector b)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			pages.put(page, p = new Page());
		p.setCell(offset, b);
		notifyObservers(address);
	}

	@Override
	public BitVector getCell(long address)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			return BitVector.of(Bit.ZERO, cellWidth);
		return p.getCell(offset);
	}
	
	@Override
	public BigInteger getCellAsBigInteger(long address)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			return BigInteger.valueOf(0L);
		return p.getCellAsBigInteger(offset);
	}

	@Override
	public void setCellAsBigInteger(long address, BigInteger word)
	{
		inBoundsCheck(address);
		long page = page(address);
		int offset = offset(address);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			pages.put(page, p = new Page());
		p.setCellAsBigInteger(offset, word);
		notifyObservers(address);
	}

	private class Page
	{
		private BitVector[] memory;

		public Page()
		{
			memory = new BitVector[pageSize];
		}

		public BitVector getCell(int index)
		{
			BitVector b = memory[index];
			if (b == null)
				return BitVector.of(Bit.ZERO, cellWidth);
			return memory[index];
		}

		public void setCell(int index, BitVector bits)
		{
			if (bits.length() != cellWidth)
				throw new IllegalArgumentException(String.format(
						"BitVector to be saved in memory cell has unexpected width. Expected: %d Actual: %d", cellWidth, bits.length()));
			memory[index] = bits;
		}

		public void setCellAsBigInteger(int index, BigInteger bits)
		{
			setCell(index, BitVector.from(bits, cellWidth));
		}
		
		public BigInteger getCellAsBigInteger(int index)
		{
			try {
				return getCell(index).getUnsignedValue();
			}
			catch(NumberFormatException e)
			{
				throw new MemoryException(e);
			}
		}

		@Override
		public String toString()
		{
			return Arrays.deepToString(memory);
		}
	}

	@Override
	public MainMemoryDefinition getDefinition()
	{
		return definition;
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
}
