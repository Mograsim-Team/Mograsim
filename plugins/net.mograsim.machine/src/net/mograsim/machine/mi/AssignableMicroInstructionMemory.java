package net.mograsim.machine.mi;

import java.util.HashSet;
import java.util.Set;

import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.MicroInstructionMemory.ActiveMicroInstructionChangedListener;

public class AssignableMicroInstructionMemory
		implements MicroInstructionMemory, MemoryCellModifiedListener, ActiveMicroInstructionChangedListener
{

	private Set<MemoryCellModifiedListener> observers = new HashSet<>();
	private Set<ActiveMicroInstructionChangedListener> activeInstructionListeners = new HashSet<>();
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

	@Override
	public void registerActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener ob)
	{
		activeInstructionListeners.add(ob);
	}

	@Override
	public void deregisterActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener ob)
	{
		activeInstructionListeners.remove(ob);
	}

	private void notifyMemoryChanged(long address)
	{
		observers.forEach(o -> o.update(address));
	}

	private void notifyActiveInstructionChanged(long address)
	{
		activeInstructionListeners.forEach(o -> o.activeMicroInstructionChanged(address));
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

	@Override
	public void setActiveInstruction(long address)
	{
		real.setActiveInstruction(address);
	}

	@Override
	public void activeMicroInstructionChanged(long address)
	{
		notifyActiveInstructionChanged(address);
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
