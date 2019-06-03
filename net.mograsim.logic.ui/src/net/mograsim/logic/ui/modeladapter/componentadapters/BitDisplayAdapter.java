package net.mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.components.Component;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public class BitDisplayAdapter implements ComponentAdapter<GUIBitDisplay>
{
	@Override
	public Class<GUIBitDisplay> getSupportedClass()
	{
		return GUIBitDisplay.class;
	}

	@Override
	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIBitDisplay guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadEnd end = logicWiresPerPin.get(guiComponent.getInputPin()).createReadOnlyEnd();
		BitDisplay bitDisplay = new BitDisplay(timeline, end);
		guiComponent.setLogicModelBinding(bitDisplay);
		return bitDisplay;
	}
}