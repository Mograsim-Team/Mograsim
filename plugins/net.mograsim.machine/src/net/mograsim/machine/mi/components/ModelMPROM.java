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
import net.mograsim.machine.mi.MPROM;
import net.mograsim.machine.mi.MPROMDefinition;

public abstract class ModelMPROM extends ModelMemory
{
	private final Pin addrPin, dataPin;
	private CoreMPROM memory;
	private final MPROMDefinition definition;

	private final List<Consumer<Object>> memoryBindingListeners;

	public ModelMPROM(LogicModelModifiable model, MPROMDefinition definition, String name)
	{
		super(model, 30, 80, name, "MPROM", false);
		this.definition = definition;
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, getWidth(), 30));
		addPin(dataPin = new Pin(model, this, "D", definition.getMicroInstructionMemoryAddressBits(), PinUsage.OUTPUT, getWidth(), 50));

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
					memory.setMemory((MPROM) newState);
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

	public MPROMDefinition getDefinition()
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

	public CoreMPROM getCoreMemory()
	{
		return memory;
	}

	public void setCoreModelBinding(CoreMPROM memory)
	{
		this.memory = memory;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new MPROMAdapter());
	}
}