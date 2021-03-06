package net.mograsim.logic.model.snippets.highlevelstatehandlers;

import java.util.function.Consumer;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;

public class DefaultHighLevelStateHandler implements HighLevelStateHandler
{
	public DefaultHighLevelStateHandler()
	{
		this(null);
	}

	public DefaultHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	@SuppressWarnings("unused") // we don't need the component; and params are always null
	public DefaultHighLevelStateHandler(SubmodelComponent component, Void params)
	{
		// nothing to do here
	}

	@Override
	public Object get(String stateID)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public void set(String stateID, Object newState)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public void addListener(String stateID, Consumer<Object> stateChanged)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public void removeListener(String stateID, Consumer<Object> stateChanged)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "default";
	}

	@Override
	public Void getParamsForSerializing(IdentifyParams idParams)
	{
		return null;
	}

	static
	{
		SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier.setSnippetSupplier(
				DefaultHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(Void.class, DefaultHighLevelStateHandler::new));
	}
}