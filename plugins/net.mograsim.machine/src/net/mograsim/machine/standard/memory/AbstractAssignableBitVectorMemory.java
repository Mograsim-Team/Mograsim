package net.mograsim.machine.standard.memory;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;

public class AbstractAssignableBitVectorMemory<M extends BitVectorMemory> implements BitVectorMemory, MemoryCellModifiedListener
{
	private Set<MemoryCellModifiedListener> observers = new HashSet<>();
	private Set<BitVectorMemoryReassignedListener<M>> reassignmentListeners = new HashSet<>();
	private M real = null;

	public AbstractAssignableBitVectorMemory(M memory)
	{
		real = memory;
		real.registerCellModifiedListener(this);
	}

	public void bind(M real)
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

	protected M getReal()
	{
		return real;
	}

	@Override
	public BitVectorMemoryDefinition getDefinition()
	{
		return real.getDefinition();
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
	public void update(long address)
	{
		notifyMemoryChanged(address);
	}

	public void registerMemoryReassignedListener(BitVectorMemoryReassignedListener<M> listener)
	{
		reassignmentListeners.add(listener);
	}

	public void deregisterMemoryReassignedListener(BitVectorMemoryReassignedListener<M> listener)
	{
		reassignmentListeners.remove(listener);
	}

	private void notifyMemoryReassigned(M newAssignee)
	{
		reassignmentListeners.forEach(l -> l.reassigned(newAssignee));
	}

	public interface BitVectorMemoryReassignedListener<M extends BitVectorMemory>
	{
		public void reassigned(M newAssignee);
	}
}
