package net.mograsim.machine.mi.components;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.machine.ModelMemory;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public abstract class ModelMicroInstructionMemory extends ModelMemory
{
	private final Pin addrPin, dataPin;
	private CoreMicroInstructionMemory memory;
	private final MicroInstructionMemoryDefinition definition;

	public ModelMicroInstructionMemory(LogicModelModifiable model, MicroInstructionMemoryDefinition definition, String name)
	{
		super(model, 120, 150, name, "MPM", false);
		this.definition = definition;
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, getWidth(), 30));
		addPin(dataPin = new Pin(model, this, "D", definition.getMicroInstructionDefinition().sizeInBits(), PinUsage.OUTPUT, getWidth(),
				50));

		init();
	}

	public MicroInstructionMemoryDefinition getDefinition()
	{
		return definition;
	}

	public Pin getAddressPin()
	{
		return addrPin;
	}

	public Pin getDataPin()
	{
		return dataPin;
	}

	public CoreMicroInstructionMemory getCoreMemory()
	{
		return memory;
	}

	public void setCoreModelBinding(CoreMicroInstructionMemory memory)
	{
		this.memory = memory;
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		if (stateID.equals("memory_binding"))
			memory.setMemory((MicroInstructionMemory) newState);
		else
			super.setHighLevelState(stateID, newState);
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		if (stateID.equals("memory_binding"))
			return memory.getMemory();
		return super.getHighLevelState(stateID);
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new MicroInstructionMemoryAdapter());
	}
}