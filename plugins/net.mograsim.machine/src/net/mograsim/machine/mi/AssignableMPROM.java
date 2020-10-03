package net.mograsim.machine.mi;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;

public class AssignableMPROM implements MPROM, MemoryCellModifiedListener
{

	private Set<MemoryCellModifiedListener> observers = new HashSet<>();

	private Set<MPROMReassignedListener> reassignmentListeners = new HashSet<>();
	private MPROM real = null;

	public AssignableMPROM(MPROM standardMPROM)
	{
		real = standardMPROM;
		real.registerCellModifiedListener(this);
	}

	public void bind(MPROM real)
	{
		this.real.deregisterCellModifiedListener(this);
		this.real = real;
		real.registerCellModifiedListener(this);
		notifyMemoryChanged(-1);
		notifyMemoryReassigned(real);
	}

	@Override
	public BitVector getCell(long address)
	{
		return real.getCell(address);
	}

	@Override
	public void setCell(long address, BitVector data)
	{
		real.setCell(address, data);
	}

	@Override
	public BigInteger getCellAsBigInteger(long address)
	{
		return real.getCellAsBigInteger(address);
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
		observers.forEach(o -> o.update(address));
	}

	@Override
	public MPROMDefinition getDefinition()
	{
		return real.getDefinition();
	}

	@Override
	public void update(long address)
	{
		notifyMemoryChanged(address);
	}

	public void registerMemoryReassignedListener(MPROMReassignedListener listener)
	{
		reassignmentListeners.add(listener);
	}

	public void deregisterMemoryReassignedListener(MPROMReassignedListener listener)
	{
		reassignmentListeners.remove(listener);
	}

	private void notifyMemoryReassigned(MPROM newAssignee)
	{
		reassignmentListeners.forEach(l -> l.reassigned(newAssignee));
	}

	public static interface MPROMReassignedListener
	{
		public void reassigned(MPROM newAssignee);
	}
}
