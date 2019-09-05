package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class ManualSwitchAdapter implements ComponentAdapter<ModelManualSwitch>
{
	@Override
	public Class<ModelManualSwitch> getSupportedClass()
	{
		return ModelManualSwitch.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, ModelManualSwitch modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd end = logicWiresPerPin.get(modelComponent.getOutputPin()).createReadWriteEnd();
		CoreManualSwitch manualSwitch = new CoreManualSwitch(timeline, end);
		modelComponent.setLogicModelBinding(manualSwitch);
	}
}