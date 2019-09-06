package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;

public class ClockAdapter implements ComponentAdapter<ModelClock>
{

	@Override
	public Class<ModelClock> getSupportedClass()
	{
		return ModelClock.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelClock modelClock,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd out = logicWiresPerPin.get(modelClock.getOutputPin()).createReadWriteEnd();
		CoreClock c = new CoreClock(timeline, out, modelClock.getDelta());
		modelClock.setCoreModelBinding(c);
	}

}