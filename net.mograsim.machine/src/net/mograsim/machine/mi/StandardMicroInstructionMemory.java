package net.mograsim.machine.mi;

import java.util.HashSet;

import net.mograsim.machine.MemoryObserver;
import net.mograsim.machine.standard.memory.MemoryException;

public class StandardMicroInstructionMemory implements MicroInstructionMemory
{
	private MicroInstruction[] data;
	private MicroInstructionMemoryDefinition definition;
	private HashSet<MemoryObserver> observers = new HashSet<>();

	public StandardMicroInstructionMemory(MicroInstructionMemoryDefinition definition)
	{
		if (definition.size() > Integer.MAX_VALUE)
			throw new MemoryException("Size of MicroInstructionMemory must be an int, not a long");
		this.definition = definition;
		data = new MicroInstruction[(int) definition.size()];
	}

	private int translate(long address)
	{
		return (int) (address - definition.getMinimalAddress());
	}

	@Override
	public MicroInstruction getCell(long address)
	{
		int translatedAddress = translate(address);
		MicroInstruction actual = data[translatedAddress];
		if (actual == null)
			actual = data[translatedAddress] = definition.getMicroInstructionDefinition().createDefaultInstruction();
		return actual;
	}

	@Override
	public void setCell(long address, MicroInstruction data)
	{
		this.data[translate(address)] = data;
		notifyObservers(address);
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
		observers.forEach(ob -> ob.update(address));
	}

	@Override
	public MicroInstructionMemoryDefinition getDefinition()
	{
		return definition;
	}

}
