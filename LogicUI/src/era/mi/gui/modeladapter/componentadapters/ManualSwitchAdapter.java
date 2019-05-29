package era.mi.gui.modeladapter.componentadapters;

import java.util.Map;

import era.mi.gui.model.components.GUIManualSwitch;
import era.mi.gui.model.wires.Pin;
import era.mi.gui.modeladapter.LogicModelParameters;
import era.mi.logic.components.Component;
import era.mi.logic.components.ManualSwitch;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadWriteEnd;

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