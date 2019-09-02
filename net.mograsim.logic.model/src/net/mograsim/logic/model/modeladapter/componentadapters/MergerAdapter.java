package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.components.atomic.GUIMerger;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class MergerAdapter implements ComponentAdapter<GUIMerger>
{
	@Override
	public Class<GUIMerger> getSupportedClass()
	{
		return GUIMerger.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIMerger guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		Wire output = logicWiresPerPin.get(guiComponent.getPin("O"));
		ReadEnd[] inputEnds = new ReadEnd[guiComponent.logicWidth];
		for (int i = 0; i < guiComponent.logicWidth; i++)
		{
			Wire input = logicWiresPerPin.get(guiComponent.getPin("I" + (guiComponent.logicWidth - 1 - i)));
			Wire.fuse(input, output, 0, i);
			inputEnds[i] = input.createReadOnlyEnd();
		}
		guiComponent.setLogicModelBinding(inputEnds, output.createReadOnlyEnd());
	}
}