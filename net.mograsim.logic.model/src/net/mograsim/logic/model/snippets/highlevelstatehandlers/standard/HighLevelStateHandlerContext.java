package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;

public class HighLevelStateHandlerContext
{
	public final SubmodelComponent component;
	public final String stateID;

	public HighLevelStateHandlerContext(SubmodelComponent component, String stateID)
	{
		this.component = component;
		this.stateID = stateID;
	}
}