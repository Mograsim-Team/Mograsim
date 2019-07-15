package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import net.mograsim.logic.model.model.components.GUIComponent;

public class HighLevelStateHandlerContext
{
	public final GUIComponent component;
	public final String stateID;

	public HighLevelStateHandlerContext(GUIComponent component, String stateID)
	{
		this.component = component;
		this.stateID = stateID;
	}
}