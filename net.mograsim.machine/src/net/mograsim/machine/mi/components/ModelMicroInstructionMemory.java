package net.mograsim.machine.mi.components;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.machine.ModelMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public abstract class ModelMicroInstructionMemory extends ModelMemory
{
	private final Pin addrPin, dataPin, clock;
	private CoreMicroInstructionMemory memory;
	private final MicroInstructionMemoryDefinition definition;
	
	public ModelMicroInstructionMemory(LogicModelModifiable model, MicroInstructionMemoryDefinition definition, String name)
	{
		super(model, 200, 100, name, "MPM", false);
		this.definition = definition;
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, width / 2, 0));
		addPin(dataPin = new Pin(model, this, "D", definition.getMicroInstructionDefinition().sizeInBits(), PinUsage.OUTPUT, 0, 30));
		addPin(clock = new Pin(model, this, "C", 1, PinUsage.INPUT, 0, 60));

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

	public Pin getClockPin()
	{
		return clock;
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
