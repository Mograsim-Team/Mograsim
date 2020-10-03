package net.mograsim.machine.mi;

import java.math.BigInteger;
import java.util.HashSet;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.standard.memory.MemoryException;

public class StandardMPROM implements MPROM
{
	private BitVector[] data;
	private MPROMDefinition definition;
	private HashSet<MemoryCellModifiedListener> observers = new HashSet<>();

	public StandardMPROM(MPROMDefinition definition)
	{
		if (definition.size() > Integer.MAX_VALUE)
			throw new MemoryException("Size of MPROM must be an int, not a long");
		this.definition = definition;
		data = new BitVector[(int) definition.size()];
	}

	private int translate(long address)
	{
		return (int) (address - definition.getMinimalAddress());
	}

	@Override
	public BitVector getCell(long address)
	{
		int translatedAddress = translate(address);
		BitVector cell = data[translatedAddress];
		if (cell == null)
			cell = data[translatedAddress] = BitVector.from(address * 16, definition.getMicroInstructionMemoryAddressBits());
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
		this.data[translate(address)] = data;
		notifyMemoryChanged(address);
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

	private void notifyMemoryChanged(long address)
	{
		observers.forEach(ob -> ob.update(address));
	}

	@Override
	public MPROMDefinition getDefinition()
	{
		return definition;
	}
}
