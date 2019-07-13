package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class ManualSwitchAdapter implements ComponentAdapter<GUIManualSwitch>
{
	@Override
	public Class<GUIManualSwitch> getSupportedClass()
	{
		return GUIManualSwitch.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIManualSwitch guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadWriteEnd end = logicWiresPerPin.get(guiComponent.getOutputPin()).createReadWriteEnd();
		ManualSwitch manualSwitch = new ManualSwitch(timeline, end);
		guiComponent.setLogicModelBinding(manualSwitch, end);
	}
}