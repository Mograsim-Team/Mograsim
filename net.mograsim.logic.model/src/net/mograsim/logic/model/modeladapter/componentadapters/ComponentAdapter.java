package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public interface ComponentAdapter<G extends GUIComponent>
{
	public Class<G> getSupportedClass();

	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, G guiComponent, Map<Pin, CoreWire> logicWiresPerPin);
}