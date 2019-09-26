package net.mograsim.machine.standard.memory;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;

public class AssignableMainMemory implements MainMemory, MemoryCellModifiedListener
{
	private Set<MemoryCellModifiedListener> observers = new HashSet<>();
	private Set<MainMemoryReassignedListener> reassignmentListeners = new HashSet<>();
	private MainMemory real = null;

	public AssignableMainMemory(MainMemory mainMemory)
	{
		real = mainMemory;
		real.registerCellModifiedListener(this);
	}

	public void bind(MainMemory real)
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
	public BigInteger getCellAsBigInteger(long address)
	{
		return real.getCellAsBigInteger(address);
	}

	@Override
	public void setCell(long address, BitVector data)
	{
		real.setCell(address, data);
	}

	@Override
	public void setCellAsBigInteger(long address, BigInteger word)
	{
		real.setCellAsBigInteger(address, word);
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
	public MainMemoryDefinition getDefinition()
	{
		return real.getDefinition();
	}

	@Override
	public void update(long address)
	{
		notifyMemoryChanged(address);
	}

	public void registerMemoryReassignedListener(MainMemoryReassignedListener listener)
	{
		reassignmentListeners.add(listener);
	}

	public void deregisterMemoryReassignedListener(MainMemoryReassignedListener listener)
	{
		reassignmentListeners.remove(listener);
	}

	private void notifyMemoryReassigned(MainMemory newAssignee)
	{
		reassignmentListeners.forEach(l -> l.reassigned(newAssignee));
	}

	public static interface MainMemoryReassignedListener
	{
		public void reassigned(MainMemory newAssignee);
	}
}
