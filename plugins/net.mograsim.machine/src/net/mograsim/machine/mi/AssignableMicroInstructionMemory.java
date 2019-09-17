package net.mograsim.machine.mi;

import java.util.HashSet;
import java.util.Set;

import net.mograsim.machine.MemoryObserver;

public class AssignableMicroInstructionMemory implements MicroInstructionMemory, MemoryObserver
{
	private Set<MemoryObserver> observers = new HashSet<>();
	MicroInstructionMemory real = null;

	public AssignableMicroInstructionMemory(StandardMicroInstructionMemory standardMicroInstructionMemory)
	{
		real = standardMicroInstructionMemory;
		real.registerObserver(this);
	}

	public void bind(MicroInstructionMemory real)
	{
		this.real.deregisterObserver(this);
		this.real = real;
		real.registerObserver(this);
		notifyObservers(-1);
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
	public void registerObserver(MemoryObserver ob)
	{
		observers.add(ob);
	}

	@Override
	public void deregisterObserver(MemoryObserver ob)
	{
		observers.remove(ob);
	}

	@Override
	public void notifyObservers(long address)
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
		notifyObservers(address);
	}
}
