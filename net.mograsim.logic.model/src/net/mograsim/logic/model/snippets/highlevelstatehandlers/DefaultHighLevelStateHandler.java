package net.mograsim.logic.model.snippets.highlevelstatehandlers;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;

public class DefaultHighLevelStateHandler implements HighLevelStateHandler
{
	@SuppressWarnings("unused") // we don't need the component; and params are always null
	public DefaultHighLevelStateHandler(SubmodelComponent component, Void params)
	{
		// nothing to do here
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		throw new IllegalArgumentException("No high level state with ID " + stateID);
	}

	static
	{
		SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier.setSnippetSupplier(
				DefaultHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(Void.class, DefaultHighLevelStateHandler::new));
	}
}