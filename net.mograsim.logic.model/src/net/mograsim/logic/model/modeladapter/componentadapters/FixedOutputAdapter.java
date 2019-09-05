package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.model.model.components.atomic.ModelFixedOutput;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class FixedOutputAdapter implements ComponentAdapter<ModelFixedOutput>
{
	@Override
	public Class<ModelFixedOutput> getSupportedClass()
	{
		return ModelFixedOutput.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, ModelFixedOutput modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		logicWiresPerPin.get(modelComponent.getPin("out")).createReadWriteEnd().feedSignals(modelComponent.bits);
	}
}