package net.mograsim.machine.standard.memory;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.ModelMemory;

public abstract class ModelWordAddressableMemory extends ModelMemory
{
	private final Pin addrPin, dataPin, rWPin;
	private CoreWordAddressableMemory memory;
	private MainMemoryDefinition definition;

	public ModelWordAddressableMemory(LogicModelModifiable model, MainMemoryDefinition definition, String name)
	{
		super(model, 120, 150, name, "RAM", false);
		this.definition = definition;

		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, getWidth(), 30));
		addPin(dataPin = new Pin(model, this, "D", definition.getCellWidth(), PinUsage.TRISTATE, getWidth(), 50));
		addPin(rWPin = new Pin(model, this, "RW", 1, PinUsage.INPUT, getWidth(), 70));

		setHighLevelStateHandler(new HighLevelStateHandler()
		{
			@Override
			public Object getHighLevelState(String stateID)
			{
				if (stateID.equals("memory_binding"))
					return memory.getMemory();
				throw new IllegalArgumentException("No high level state with ID " + stateID);
			}

			@Override
			public void setHighLevelState(String stateID, Object newState)
			{
				if (stateID.equals("memory_binding"))
					memory.setMemory((MainMemory) newState);
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

	public MainMemoryDefinition getDefinition()
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

	public Pin getReadWritePin()
	{
		return rWPin;
	}

	public void setCoreModelBinding(CoreWordAddressableMemory memory)
	{
		this.memory = memory;
	}

	public CoreWordAddressableMemory getCoreMemory()
	{
		return memory;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new WordAddressableMemoryAdapter());
	}
}