package net.mograsim.machine.standard.memory;

import java.util.Arrays;
import java.util.HashMap;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;

public class WordAddressableMemory
{
	private final int cellWidth;
	private final long minimalAddress, maximalAddress;
	private final int pageSize = 64;

	private HashMap<Long, Page> pages;

	public WordAddressableMemory(int cellWidth, long minimalAddress, long maximalAddress)
	{
		super();
		this.cellWidth = cellWidth;
		this.minimalAddress = minimalAddress;
		this.maximalAddress = maximalAddress;
		this.pages = new HashMap<>();
	}

	public void setCell(long address, BitVector b)
	{
		if (address < minimalAddress || address > maximalAddress)
			throw new IndexOutOfBoundsException(String.format("Memory address out of bounds! Minimum: %d Maximum: %d Actual: %d",
					minimalAddress, maximalAddress, address));
		long page = address / pageSize;
		int offset = (int) (address % pageSize);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			pages.put(page, p = new Page());
		p.setCell(offset, b);
	}

	public BitVector getCell(long address)
	{
		long page = address / pageSize;
		int offset = (int) (address % pageSize);
		Page p = pages.get(Long.valueOf(page));
		if (p == null)
			return BitVector.of(Bit.U, cellWidth);
		return p.getCell(offset);
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
				return BitVector.of(Bit.U, cellWidth);
			return memory[index];
		}

		public void setCell(int index, BitVector bits)
		{
			if (bits.length() != cellWidth)
				throw new IllegalArgumentException(String.format(
						"BitVector to be saved in memory cell has unexpected length. Expected: %d Actual: %d", cellWidth, bits.length()));
			memory[index] = bits;
		}

		@Override
		public String toString()
		{
			return Arrays.deepToString(memory);
		}
	}
}
