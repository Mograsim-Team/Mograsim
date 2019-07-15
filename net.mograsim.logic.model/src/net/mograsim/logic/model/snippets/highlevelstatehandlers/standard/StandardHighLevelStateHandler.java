package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler.AtomicHighLevelStateHandlerParams;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler.SubcomponentHighLevelStateHandlerParams;

public class StandardHighLevelStateHandler implements HighLevelStateHandler
{
	private final GUIComponent component;
	private final Map<String, SubcomponentHighLevelStateHandler> subcomponentHighLevelStateHandlers;
	private final Map<String, AtomicHighLevelStateHandler> atomicHighLevelStateHandlers;

	public StandardHighLevelStateHandler(GUIComponent component, SimpleHighLevelStateHandlerParams params)
	{
		this.component = component;
		this.subcomponentHighLevelStateHandlers = new HashMap<>();
		this.atomicHighLevelStateHandlers = new HashMap<>();
		params.subcomponentHighLevelStates.forEach(this::addSubcomponentHighLevelState);
		params.atomicHighLevelStates.forEach(this::addAtomicHighLevelState);
	}

	public void addSubcomponentHighLevelState(String subcomponentStateID, SubcomponentHighLevelStateHandlerParams handlerParams)
	{
		HighLevelStateHandlerContext context = new HighLevelStateHandlerContext(component, subcomponentStateID);
		addSubcomponentHighLevelState(subcomponentStateID, StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier
				.getSnippetSupplier(handlerParams.id).create(context, handlerParams.params));
	}

	public void addSubcomponentHighLevelState(String subcomponentStateID, SubcomponentHighLevelStateHandler handler)
	{
		checkHighLevelStateIDPart(subcomponentStateID);
		subcomponentHighLevelStateHandlers.put(subcomponentStateID, handler);
	}

	public void addAtomicHighLevelState(String atomicStateID, AtomicHighLevelStateHandlerParams handlerParams)
	{
		HighLevelStateHandlerContext context = new HighLevelStateHandlerContext(component, atomicStateID);
		addSubcomponentHighLevelState(atomicStateID, StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier
				.getSnippetSupplier(handlerParams.id).create(context, handlerParams.params));
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

	public static class SimpleHighLevelStateHandlerParams
	{
		public Map<String, SubcomponentHighLevelStateHandlerParams> subcomponentHighLevelStates;
		public Map<String, AtomicHighLevelStateHandlerParams> atomicHighLevelStates;
	}

	static
	{
		SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier.setSnippetSupplier(
				StandardHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(SimpleHighLevelStateHandlerParams.class, StandardHighLevelStateHandler::new));
	}
}