package mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import mograsim.logic.core.components.Component;
import mograsim.logic.core.components.ManualSwitch;
import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.wires.Wire;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;
import mograsim.logic.ui.model.components.GUIManualSwitch;
import mograsim.logic.ui.model.wires.Pin;
import mograsim.logic.ui.modeladapter.LogicModelParameters;

public class ManualSwitchAdapter implements ComponentAdapter<GUIManualSwitch>
{
	@Override
	public Class<GUIManualSwitch> getSupportedClass()
	{
		return GUIManualSwitch.class;
	}

	@Override
	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIManualSwitch guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadWriteEnd end = logicWiresPerPin.get(guiComponent.getOutputPin()).createReadWriteEnd();
		ManualSwitch manualSwitch = new ManualSwitch(timeline, end);
		guiComponent.setLogicModelBinding(manualSwitch, end);
		return manualSwitch;
	}
}