package net.mograsim.machine.mi;

import java.util.HashSet;

import net.mograsim.machine.standard.memory.MemoryException;

public class StandardMicroInstructionMemory implements MicroInstructionMemory
{
	private MicroInstruction[] data;
	private MicroInstructionMemoryDefinition definition;
	private HashSet<MemoryCellModifiedListener> observers = new HashSet<>();

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
	public MicroInstructionMemoryDefinition getDefinition()
	{
		return definition;
	}
}
