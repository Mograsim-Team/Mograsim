package net.mograsim.machine.standard.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;
import net.mograsim.machine.ModelMemory;

public abstract class AbstractModelBitVectorMemory<M extends BitVectorMemory, D extends BitVectorMemoryDefinition> extends ModelMemory
{
	private final Pin addrPin, dataPin, rWPin;
	private final D definition;
	private final boolean readonly;

	private CoreBitVectorMemory<M> memory;

	private final List<Consumer<Object>> memoryBindingListeners;

	public AbstractModelBitVectorMemory(LogicModelModifiable model, int width, int height, int aPinX, int dPinX, int rwPinX,
			boolean readonly, String centerText, D definition, String name)
	{
		super(model, width, height, name, centerText, false);
		this.definition = definition;
		this.readonly = readonly;

		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, width, aPinX));
		addPin(dataPin = new Pin(model, this, "D", definition.getCellWidth(), PinUsage.TRISTATE, width, dPinX));
		if (readonly)
			rWPin = null;
		else
			addPin(rWPin = new Pin(model, this, "RW", 1, PinUsage.INPUT, width, rwPinX));

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
					@SuppressWarnings("unchecked")
					M mem = (M) newState;
					memory.setMemory(mem);
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

	public D getDefinition()
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
		if (isReadonly())
			throw new IllegalArgumentException("This AbstractModelBitVectorMemory has no RW pin; it is readonly");
		return rWPin;
	}

	public boolean isReadonly()
	{
		return readonly;
	}

	public void setCoreModelBinding(CoreBitVectorMemory<M> memory)
	{
		this.memory = memory;
	}

	public CoreBitVectorMemory<M> getCoreMemory()
	{
		return memory;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new BitVectorMemoryAdapter());
	}
}