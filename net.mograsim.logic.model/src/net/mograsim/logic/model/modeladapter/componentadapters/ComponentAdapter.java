package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;

public interface ComponentAdapter<G extends ModelComponent>
{
	public Class<G> getSupportedClass();

	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, G modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin);
}