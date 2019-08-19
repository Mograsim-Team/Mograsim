package net.mograsim.logic.core.components.gates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.mograsim.logic.core.components.BasicComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class WordAddressableMemoryComponent extends BasicComponent
{
	private final WordAddressableMemory memory;
	private final static Bit read = Bit.ONE;

	private ReadWriteEnd data;
	private ReadEnd rWBit, address;

	/**
	 * 
	 * @param timeline
	 * @param processTime
	 * @param minimalAddress
	 * @param maximalAddress
	 * @param data
	 * @param rWBit          0: Write 1: Read
	 * @param address
	 */
	public WordAddressableMemoryComponent(Timeline timeline, int processTime, long minimalAddress, long maximalAddress, ReadWriteEnd data,
			ReadEnd rWBit, ReadEnd address)
	{
		super(timeline, processTime);
		this.data = data;
		this.rWBit = rWBit;
		this.address = address;
		data.registerObserver(this);
		rWBit.registerObserver(this);
		address.registerObserver(this);

		memory = new WordAddressableMemory(data.length(), minimalAddress, maximalAddress);
	}

	@Override
	protected void compute()
	{
		if (!address.hasNumericValue())
		{
			if (read.equals(rWBit.getValue()))
				data.feedSignals(BitVector.of(Bit.U, data.length()));
			else
				data.clearSignals();
			return;
		}
		long addressed = address.getUnsignedValue();
		if (read.equals(rWBit.getValue()))
			data.feedSignals(memory.getCell(addressed));
		else
		{
			data.clearSignals();
			System.out.println(memory);
			memory.setCell(addressed, data.getValues());
		}
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(data, rWBit, address);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(data);
	}

	private class WordAddressableMemory
	{
		private final int cellWidth;
		private final long minimalAddress, maximalAddress;
		private final int pageSize;

		private HashMap<Long, Page> pages;

		public WordAddressableMemory(int cellWidth, long minimalAddress, long maximalAddress)
		{
			super();
			this.cellWidth = cellWidth;
			this.minimalAddress = minimalAddress;
			this.maximalAddress = maximalAddress;
			this.pages = new HashMap<>();
			this.pageSize = 64;
		}

		public void setCell(long address, BitVector b)
		{
			if (address < minimalAddress || address > maximalAddress)
				throw new IndexOutOfBoundsException(String.format("Memory address out of bounds! Minimum: %d Maimum: %d Actual: %d",
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
					throw new IllegalArgumentException(
							String.format("BitVector to be saved in memory cell has unexpected length. Expected: %d Actual: %d", cellWidth,
									bits.length()));
				memory[index] = bits;
			}

			@Override
			public String toString()
			{
				return Arrays.deepToString(memory);
			}
		}
	}
}
//import java.math.BigInteger;
//
//import net.mograsim.logic.core.types.Bit;
//import net.mograsim.logic.core.types.BitVector;
//
//private byte[] encode(BitVector v)
//{
//	BigInteger d = BigInteger.ZERO;
//	Bit[] bits = v.getBits();
//	for (int i = v.length() - 1; i >= 0; i--)
//	{
//		d = d.add(BigInteger.valueOf(bits[i].ordinal()));
//		d = d.multiply(BigInteger.valueOf(Bit.values().length));
//	}
//	return d.toByteArray();
//}
//
///**
// * 
// * @param bytes
// * @param numBits length of the resulting BitVector
// * @return
// */
//private BitVector decode(byte[] bytes, int numBits)
//{
//	BigInteger d = new BigInteger(bytes);
//	Bit[] bits = new Bit[numBits];
//	return BitVector.of(bits);
//}
//
//private static class CellArray
//{
//	private byte[] bytes;
//	private final static byte mask = -1;
//	public CellArray(int numCells, int cellSize)
//	{
//		bytes = new byte[numCells * cellSize / 8];
//	}
//	
//	public byte[] getCell(int i)
//	{
//		
//	}
//}
