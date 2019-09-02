package net.mograsim.machine.mi;

import java.util.HashSet;

import net.mograsim.machine.MemoryDefinition;
import net.mograsim.machine.MemoryObserver;
import net.mograsim.machine.standard.memory.MemoryException;

class StandardMicroprogramMemory implements MicroprogramMemory
{
	private MicroInstruction[] data;
	private MemoryDefinition definition;
	private HashSet<MemoryObserver> observers = new HashSet<>();
	
	StandardMicroprogramMemory(MemoryDefinition definition)
	{
		if(definition.size() > Integer.MAX_VALUE)
			throw new MemoryException("Size of MicroprogramMemory must be an int, not a long");
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
		return data[translate(address)];
	}

	@Override
	public void setCell(long address, MicroInstruction data)
	{
		this.data[translate(address)] = data;
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
	public MemoryDefinition getDefinition()
	{
		return definition;
	}

}
