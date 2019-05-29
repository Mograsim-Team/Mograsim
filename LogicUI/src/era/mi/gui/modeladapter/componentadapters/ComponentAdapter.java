package era.mi.gui.modeladapter.componentadapters;

import java.util.Map;

import era.mi.gui.model.components.GUIComponent;
import era.mi.gui.model.wires.Pin;
import era.mi.gui.modeladapter.LogicModelParameters;
import era.mi.logic.components.Component;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;

public interface ComponentAdapter<G extends GUIComponent>
{
	public Class<G> getSupportedClass();

	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, G guiComponent,
			Map<Pin, Wire> logicWiresPerPin);
}