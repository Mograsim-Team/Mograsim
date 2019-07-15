package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
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
	private final Map<String, AtomicHighLevelStateHandler> atomicHighLevelStateHandlers;

	public StandardHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public StandardHighLevelStateHandler(SubmodelComponent component, StandardHighLevelStateHandlerParams params)
	{
		this.component = component;
		this.subcomponentHighLevelStateHandlers = new HashMap<>();
		this.atomicHighLevelStateHandlers = new HashMap<>();
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
			BiFunction<HighLevelStateHandlerContext, P, H> handlerConstructor, P handlerParams)
	{
		return addSubcomponentHighLevelState(subcomponentStateID, c -> handlerConstructor.apply(c, handlerParams));
	}

	public <H extends SubcomponentHighLevelStateHandler> H addSubcomponentHighLevelState(String subcomponentStateID,
			Function<HighLevelStateHandlerContext, H> handlerConstructor)
	{
		HighLevelStateHandlerContext context = new HighLevelStateHandlerContext(component, subcomponentStateID);
		H handler = handlerConstructor.apply(context);
		addSubcomponentHighLevelState(subcomponentStateID, handler);
		return handler;
	}

	public void addSubcomponentHighLevelState(String subcomponentStateID, SubcomponentHighLevelStateHandler handler)
	{
		checkHighLevelStateIDPart(subcomponentStateID);
		subcomponentHighLevelStateHandlers.put(subcomponentStateID, handler);
	}

	public AtomicHighLevelStateHandler addAtomicHighLevelState(String atomicStateID, AtomicHighLevelStateHandlerParams handlerParams)
	{
		return addAtomicHighLevelState(atomicStateID,
				StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier.getSnippetSupplier(handlerParams.id)::create,
				handlerParams.params);
	}

	public <P, H extends AtomicHighLevelStateHandler> H addAtomicHighLevelState(String subcomponentStateID,
			BiFunction<HighLevelStateHandlerContext, P, H> handlerConstructor, P handlerParams)
	{
		return addAtomicHighLevelState(subcomponentStateID, c -> handlerConstructor.apply(c, handlerParams));
	}

	public <H extends AtomicHighLevelStateHandler> H addAtomicHighLevelState(String subcomponentStateID,
			Function<HighLevelStateHandlerContext, H> handlerConstructor)
	{
		HighLevelStateHandlerContext context = new HighLevelStateHandlerContext(component, subcomponentStateID);
		H handler = handlerConstructor.apply(context);
		addAtomicHighLevelState(subcomponentStateID, handler);
		return handler;
	}

	public void addAtomicHighLevelState(String atomicStateID, AtomicHighLevelStateHandler handler)
	{
		checkHighLevelStateIDPart(atomicStateID);
		atomicHighLevelStateHandlers.put(atomicStateID, handler);
	}

	private static void checkHighLevelStateIDPart(String stateIDPart)
	{
		if (stateIDPart.indexOf('.') != -1)
			throw new IllegalArgumentException("Illegal high level state ID part (contains dot): " + stateIDPart);
	}

	@Override
	public Object getHighLevelState(String stateID)
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
	public void setHighLevelState(String stateID, Object newState)
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