package net.mograsim.machine.mi.components;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.machine.ModelMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public abstract class ModelMicroInstructionMemory extends ModelMemory
{
	private final Pin addrPin, dataPin;
	private CoreMicroInstructionMemory memory;
	private final MicroInstructionMemoryDefinition definition;
	
	public ModelMicroInstructionMemory(LogicModelModifiable model, MicroInstructionMemoryDefinition definition, String name)
	{
		super(model, 200, 100, name, "MPM", false);
		this.definition = definition;
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, width / 2, 0));
		addPin(dataPin = new Pin(model, this, "D", definition.getMicroInstructionDefinition().sizeInBits(), PinUsage.OUTPUT, 0, 30));

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
}
