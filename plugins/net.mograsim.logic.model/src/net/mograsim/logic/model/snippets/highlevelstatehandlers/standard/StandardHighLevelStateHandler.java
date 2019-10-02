package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler.AtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler.SubcomponentHighLevelStateHandlerParams;

public class StandardHighLevelStateHandler implements HighLevelStateHandler
{
	private final SubmodelComponent component;
	private final Map<String, SubcomponentHighLevelStateHandler> subcomponentHighLevelStateHandlers;
	private final Map<String, SubcomponentHighLevelStateHandler> subcomponentHighLevelStateHandlersUnmodifiable;
	private final Map<String, AtomicHighLevelStateHandler> atomicHighLevelStateHandlers;
	private final Map<String, AtomicHighLevelStateHandler> atomicHighLevelStateHandlersUnmodifiable;

	public StandardHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public StandardHighLevelStateHandler(SubmodelComponent component, StandardHighLevelStateHandlerParams params)
	{
		this.component = component;
		this.subcomponentHighLevelStateHandlers = new HashMap<>();
		this.subcomponentHighLevelStateHandlersUnmodifiable = Collections.unmodifiableMap(subcomponentHighLevelStateHandlers);
		this.atomicHighLevelStateHandlers = new HashMap<>();
		this.atomicHighLevelStateHandlersUnmodifiable = Collections.unmodifiableMap(atomicHighLevelStateHandlers);
		if (params != null)
		{
			params.subcomponentHighLevelStates.forEach(this::addSubcomponentHighLevelState);
			params.atomicHighLevelStates.forEach(this::addAtomicHighLevelState);
		}
	}

	public SubcomponentHighLevelStateHandler addSubcomponentHighLevelState(String subcomponentStateID,
			SubcomponentHighLevelStateHandlerParams handlerParams)
	{
		return addSubcomponentHighLevelState(subcomponentStateID,
				StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier.getSnippetSupplier(handlerParams.id)::create,
				handlerParams.params);
	}

	public <P, H extends SubcomponentHighLevelStateHandler> H addSubcomponentHighLevelState(String subcomponentStateID,
			BiFunction<SubmodelComponent, P, H> handlerConstructor, P handlerParams)
	{
		return addSubcomponentHighLevelState(subcomponentStateID, c -> handlerConstructor.apply(c, handlerParams));
	}

	public <H extends SubcomponentHighLevelStateHandler> H addSubcomponentHighLevelState(String subcomponentStateID,
			Function<SubmodelComponent, H> handlerConstructor)
	{
		H handler = handlerConstructor.apply(component);
		addSubcomponentHighLevelState(subcomponentStateID, handler);
		return handler;
	}

	public void addSubcomponentHighLevelState(String subcomponentStateID, SubcomponentHighLevelStateHandler handler)
	{
		checkHighLevelStateIDPart(subcomponentStateID);
		subcomponentHighLevelStateHandlers.put(subcomponentStateID, handler);
	}

	public void removeSubcomponentHighLevelState(String subcomponentStateID)
	{
		checkHighLevelStateIDPart(subcomponentStateID);
		subcomponentHighLevelStateHandlers.remove(subcomponentStateID);
	}

	public Map<String, SubcomponentHighLevelStateHandler> getSubcomponentHighLevelStates()
	{
		return subcomponentHighLevelStateHandlersUnmodifiable;
	}

	public AtomicHighLevelStateHandler addAtomicHighLevelState(String atomicStateID, AtomicHighLevelStateHandlerParams handlerParams)
	{
		return addAtomicHighLevelState(atomicStateID,
				StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier.getSnippetSupplier(handlerParams.id)::create,
				handlerParams.params);
	}

	public <P, H extends AtomicHighLevelStateHandler> H addAtomicHighLevelState(String subcomponentStateID,
			BiFunction<SubmodelComponent, P, H> handlerConstructor, P handlerParams)
	{
		return addAtomicHighLevelState(subcomponentStateID, c -> handlerConstructor.apply(c, handlerParams));
	}

	public <H extends AtomicHighLevelStateHandler> H addAtomicHighLevelState(String subcomponentStateID,
			Function<SubmodelComponent, H> handlerConstructor)
	{
		H handler = handlerConstructor.apply(component);
		addAtomicHighLevelState(subcomponentStateID, handler);
		return handler;
	}

	public void addAtomicHighLevelState(String atomicStateID, AtomicHighLevelStateHandler handler)
	{
		checkHighLevelStateIDPart(atomicStateID);
		atomicHighLevelStateHandlers.put(atomicStateID, handler);
	}

	public void removeAtomicHighLevelState(String atomicStateID)
	{
		checkHighLevelStateIDPart(atomicStateID);
		atomicHighLevelStateHandlers.remove(atomicStateID);
	}

	public Map<String, AtomicHighLevelStateHandler> getAtomicHighLevelStates()
	{
		return atomicHighLevelStateHandlersUnmodifiable;
	}

	private static void checkHighLevelStateIDPart(String stateIDPart)
	{
		if (stateIDPart.indexOf('.') != -1)
			throw new IllegalArgumentException("Illegal high level state ID part (contains dot): " + stateIDPart);
	}

	@Override
	public Object get(String stateID)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
		{
			AtomicHighLevelStateHandler handler = atomicHighLevelStateHandlers.get(stateID);
			if (handler != null)
				return handler.getHighLevelState();
		} else
		{
			SubcomponentHighLevelStateHandler handler = subcomponentHighLevelStateHandlers.get(stateID.substring(0, indexOfDot));
			if (handler != null)
				return handler.getHighLevelState(stateID.substring(indexOfDot + 1));
		}
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public void set(String stateID, Object newState)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
		{
			AtomicHighLevelStateHandler handler = atomicHighLevelStateHandlers.get(stateID);
			if (handler != null)
				handler.setHighLevelState(newState);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		} else
		{
			SubcomponentHighLevelStateHandler handler = subcomponentHighLevelStateHandlers.get(stateID.substring(0, indexOfDot));
			if (handler != null)
				handler.setHighLevelState(stateID.substring(indexOfDot + 1), newState);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		}
	}

	@Override
	public void addListener(String stateID, Consumer<Object> stateChanged)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
		{
			AtomicHighLevelStateHandler handler = atomicHighLevelStateHandlers.get(stateID);
			if (handler != null)
				handler.addListener(stateChanged);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		} else
		{
			SubcomponentHighLevelStateHandler handler = subcomponentHighLevelStateHandlers.get(stateID.substring(0, indexOfDot));
			if (handler != null)
				handler.addListener(stateID.substring(indexOfDot + 1), stateChanged);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		}
	}

	@Override
	public void removeListener(String stateID, Consumer<Object> stateChanged)
	{
		int indexOfDot = stateID.indexOf('.');
		if (indexOfDot == -1)
		{
			AtomicHighLevelStateHandler handler = atomicHighLevelStateHandlers.get(stateID);
			if (handler != null)
				handler.removeListener(stateChanged);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		} else
		{
			SubcomponentHighLevelStateHandler handler = subcomponentHighLevelStateHandlers.get(stateID.substring(0, indexOfDot));
			if (handler != null)
				handler.removeListener(stateID.substring(indexOfDot + 1), stateChanged);
			else
				throw new IllegalArgumentException("No high level state with ID " + stateID);
		}
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "standard";
	}

	@Override
	public StandardHighLevelStateHandlerParams getParamsForSerializing(IdentifyParams idParams)
	{
		StandardHighLevelStateHandlerParams params = new StandardHighLevelStateHandlerParams();
		params.subcomponentHighLevelStates = new TreeMap<>();
		params.atomicHighLevelStates = new TreeMap<>();
		for (Entry<String, SubcomponentHighLevelStateHandler> e : subcomponentHighLevelStateHandlers.entrySet())
		{
			String stateID = e.getKey();
			SubcomponentHighLevelStateHandler handler = e.getValue();
			SubcomponentHighLevelStateHandlerParams handlerParams = new SubcomponentHighLevelStateHandlerParams();
			handlerParams.id = handler.getIDForSerializing(idParams);
			handlerParams.params = handler.getParamsForSerializingJSON(idParams);
			params.subcomponentHighLevelStates.put(stateID, handlerParams);
		}
		for (Entry<String, AtomicHighLevelStateHandler> e : atomicHighLevelStateHandlers.entrySet())
		{
			String stateID = e.getKey();
			AtomicHighLevelStateHandler handler = e.getValue();
			AtomicHighLevelStateHandlerParams handlerParams = new AtomicHighLevelStateHandlerParams();
			handlerParams.id = handler.getIDForSerializing(idParams);
			handlerParams.params = handler.getParamsForSerializingJSON(idParams);
			params.atomicHighLevelStates.put(stateID, handlerParams);
		}
		return params;
	}

	public static class StandardHighLevelStateHandlerParams
	{
		public Map<String, SubcomponentHighLevelStateHandlerParams> subcomponentHighLevelStates;
		public Map<String, AtomicHighLevelStateHandlerParams> atomicHighLevelStates;
	}

	static
	{
		SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier.setSnippetSupplier(
				StandardHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(StandardHighLevelStateHandlerParams.class, StandardHighLevelStateHandler::new));
	}
}