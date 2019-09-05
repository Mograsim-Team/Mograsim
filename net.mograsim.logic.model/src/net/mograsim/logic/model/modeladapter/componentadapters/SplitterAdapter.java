package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.components.atomic.ModelSplitter;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SplitterAdapter implements ComponentAdapter<ModelSplitter>
{
	@Override
	public Class<ModelSplitter> getSupportedClass()
	{
		return ModelSplitter.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, ModelSplitter modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		CoreWire input = logicWiresPerPin.get(modelComponent.getPin("I"));
		ReadEnd[] outputEnds = new ReadEnd[modelComponent.logicWidth];
		for (int i = 0; i < modelComponent.logicWidth; i++)
		{
			CoreWire output = logicWiresPerPin.get(modelComponent.getPin("O" + (modelComponent.logicWidth - 1 - i)));
			CoreWire.fuse(input, output, i, 0);
			outputEnds[i] = output.createReadOnlyEnd();
		}
		modelComponent.setLogicModelBinding(input.createReadOnlyEnd(), outputEnds);
	}
}