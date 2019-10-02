package net.mograsim.machine.mi.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.machine.ModelMemory;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryDefinition;

public abstract class ModelMicroInstructionMemory extends ModelMemory
{
	private final Pin addrPin, dataPin;
	private CoreMicroInstructionMemory memory;
	private final MicroInstructionMemoryDefinition definition;

	private final List<Consumer<Object>> memoryBindingListeners;

	public ModelMicroInstructionMemory(LogicModelModifiable model, MicroInstructionMemoryDefinition definition, String name)
	{
		super(model, 120, 150, name, "MPM", false);
		this.definition = definition;
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, getWidth(), 30));
		addPin(dataPin = new Pin(model, this, "D", definition.getMicroInstructionDefinition().sizeInBits(), PinUsage.OUTPUT, getWidth(),
				50));

		memoryBindingListeners = new ArrayList<>();

		setHighLevelStateHandler(new HighLevelStateHandler()
		{
			@Override
			public Object get(String stateID)
			{
				if (stateID.equals("memory_binding"))
					return memory.getMemory();
				throw new IllegalArgumentException("No high level state with ID " + stateID);
			}

			@Override
			public void set(String stateID, Object newState)
			{
				if (stateID.equals("memory_binding"))
				{
					memory.setMemory((MicroInstructionMemory) newState);
					memoryBindingListeners.forEach(l -> l.accept(newState));
				} else
					throw new IllegalArgumentException("No high level state with ID " + stateID);
			}

			@Override
			public void addListener(String stateID, Consumer<Object> stateChanged)
			{
				if (stateID.equals("memory_binding"))
					memoryBindingListeners.add(stateChanged);
				else
					throw new IllegalArgumentException("No high level state with ID " + stateID);
			}

			@Override
			public void removeListener(String stateID, Consumer<Object> stateChanged)
			{
				if (stateID.equals("memory_binding"))
					memoryBindingListeners.remove(stateChanged);
				else
					throw new IllegalArgumentException("No high level state with ID " + stateID);
			}

			@Override
			public String getIDForSerializing(IdentifyParams idParams)
			{
				return null;
			}

			@Override
			public Object getParamsForSerializing(IdentifyParams idParams)
			{
				return null;
			}
		});

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

	static
	{
		LogicCoreAdapter.addComponentAdapter(new MicroInstructionMemoryAdapter());
	}
}