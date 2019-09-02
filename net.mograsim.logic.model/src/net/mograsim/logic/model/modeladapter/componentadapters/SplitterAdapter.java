package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.components.atomic.GUISplitter;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SplitterAdapter implements ComponentAdapter<GUISplitter>
{
	@Override
	public Class<GUISplitter> getSupportedClass()
	{
		return GUISplitter.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUISplitter guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		Wire input = logicWiresPerPin.get(guiComponent.getPin("I"));
		ReadEnd[] outputEnds = new ReadEnd[guiComponent.logicWidth];
		for (int i = 0; i < guiComponent.logicWidth; i++)
		{
			Wire output = logicWiresPerPin.get(guiComponent.getPin("O" + (guiComponent.logicWidth - 1 - i)));
			Wire.fuse(input, output, i, 0);
			outputEnds[i] = output.createReadOnlyEnd();
		}
		guiComponent.setLogicModelBinding(input.createReadOnlyEnd(), outputEnds);
	}
}