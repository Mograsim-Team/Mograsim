package net.mograsim.machine.standard.memory;

import java.math.BigInteger;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.GenericMemory;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;

public class WordAddressableMemory extends GenericMemory<BitVector> implements MainMemory
{
	private final int cellWidth;
	private final MainMemoryDefinition definition;

	public WordAddressableMemory(MainMemoryDefinition definition)
	{
		super(definition);
		this.cellWidth = definition.getCellWidth();
		this.definition = definition;
	}

	@Override
	public void setCell(long address, BitVector data)
	{
		if (data.isBinary())
			super.setCell(address, data);
	}

	@Override
	public BitVector getCell(long address)
	{
		BitVector data = super.getCell(address);
		return data == null ? BitVector.of(Bit.ZERO, cellWidth) : data;
	}

	@Override
	public BigInteger getCellAsBigInteger(long address)
	{
		BitVector data = getCell(address);
		return data == null ? BigInteger.valueOf(0) : data.getUnsignedValue();
	}

	@Override
	public void setCellAsBigInteger(long address, BigInteger data)
	{
		setCell(address, BitVector.from(data, cellWidth));
	}

	@Override
	public MainMemoryDefinition getDefinition()
	{
		return definition;
	}
}
