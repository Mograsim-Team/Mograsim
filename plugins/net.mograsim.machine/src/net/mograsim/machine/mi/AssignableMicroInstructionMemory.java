package net.mograsim.machine.mi;

import java.util.HashSet;
import java.util.Set;

import net.mograsim.machine.Memory.MemoryCellModifiedListener;

public class AssignableMicroInstructionMemory implements MicroInstructionMemory, MemoryCellModifiedListener
{

	private Set<MemoryCellModifiedListener> observers = new HashSet<>();

	private Set<MIMemoryReassignedListener> reassignmentListeners = new HashSet<>();
	private MicroInstructionMemory real = null;

	public AssignableMicroInstructionMemory(StandardMicroInstructionMemory standardMicroInstructionMemory)
	{
		real = standardMicroInstructionMemory;
		real.registerCellModifiedListener(this);
	}

	public void bind(MicroInstructionMemory real)
	{
		this.real.deregisterCellModifiedListener(this);
		this.real = real;
		real.registerCellModifiedListener(this);
		notifyMemoryChanged(-1);
		notifyMemoryReassigned(real);
	}

	@Override
	public MicroInstruction getCell(long address)
	{
		return real.getCell(address);
	}

	@Override
	public void setCell(long address, MicroInstruction data)
	{
		real.setCell(address, data);
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
	public MicroInstructionMemoryDefinition getDefinition()
	{
		return real.getDefinition();
	}

	@Override
	public void update(long address)
	{
		notifyMemoryChanged(address);
	}

	public void registerMemoryReassignedListener(MIMemoryReassignedListener listener)
	{
		reassignmentListeners.add(listener);
	}

	public void deregisterMemoryReassignedListener(MIMemoryReassignedListener listener)
	{
		reassignmentListeners.remove(listener);
	}

	private void notifyMemoryReassigned(MicroInstructionMemory newAssignee)
	{
		reassignmentListeners.forEach(l -> l.reassigned(newAssignee));
	}

	public static interface MIMemoryReassignedListener
	{
		public void reassigned(MicroInstructionMemory newAssignee);
	}
}
