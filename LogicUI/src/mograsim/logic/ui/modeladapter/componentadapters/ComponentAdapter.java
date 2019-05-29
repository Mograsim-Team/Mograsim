package mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import mograsim.logic.core.components.Component;
import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.wires.Wire;
import mograsim.logic.ui.model.components.GUIComponent;
import mograsim.logic.ui.model.wires.Pin;
import mograsim.logic.ui.modeladapter.LogicModelParameters;

public interface ComponentAdapter<G extends GUIComponent>
{
	public Class<G> getSupportedClass();

	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, G guiComponent,
			Map<Pin, Wire> logicWiresPerPin);
}