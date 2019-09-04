package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.model.model.components.atomic.GUIFixedOutput;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class FixedOutputAdapter implements ComponentAdapter<GUIFixedOutput>
{
	@Override
	public Class<GUIFixedOutput> getSupportedClass()
	{
		return GUIFixedOutput.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIFixedOutput guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		logicWiresPerPin.get(guiComponent.getPin("out")).createReadWriteEnd().feedSignals(guiComponent.bits);
	}
}