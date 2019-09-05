package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class BitDisplayAdapter implements ComponentAdapter<GUIBitDisplay>
{
	@Override
	public Class<GUIBitDisplay> getSupportedClass()
	{
		return GUIBitDisplay.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIBitDisplay guiComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadEnd end = logicWiresPerPin.get(guiComponent.getInputPin()).createReadOnlyEnd();
		CoreBitDisplay bitDisplay = new CoreBitDisplay(timeline, end);
		guiComponent.setLogicModelBinding(bitDisplay);
	}
}