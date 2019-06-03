package net.mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.Component;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public interface ComponentAdapter<G extends GUIComponent>
{
	public Class<G> getSupportedClass();

	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, G guiComponent,
			Map<Pin, Wire> logicWiresPerPin);
}