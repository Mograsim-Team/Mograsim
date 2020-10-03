package net.mograsim.machine.standard.memory;

import java.math.BigInteger;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.GenericMemory;

public class StandardBitVectorMemory<D extends BitVectorMemoryDefinition> extends GenericMemory<BitVector, D> implements BitVectorMemory
{
	public StandardBitVectorMemory(D definition)
	{
		super(definition);
	}

	@Override
	public BitVector getCell(long address)
	{
		BitVector cell = super.getCell(address);
		if (cell == null)
			setCell(address, cell = getDefaultValue(address));
		return cell;
	}

	@Override
	public BigInteger getCellAsBigInteger(long address)
	{
		return getCell(address).getUnsignedValue();
	}

	@Override
	public void setCell(long address, BitVector data)
	{
		if (data.isBinary())
			super.setCell(address, data);
	}

	@Override
	public void setCellAsBigInteger(long address, BigInteger data)
	{
		setCell(address, BitVector.from(data, getDefinition().getCellWidth()));
	}

	protected BitVector getDefaultValue(@SuppressWarnings("unused") /* this method is inteded to be overridden */ long address)
	{
		return BitVector.of(Bit.U, getDefinition().getCellWidth());
	}
}
