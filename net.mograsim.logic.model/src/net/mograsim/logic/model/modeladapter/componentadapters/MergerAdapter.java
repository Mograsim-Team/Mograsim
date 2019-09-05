package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.components.atomic.ModelMerger;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class MergerAdapter implements ComponentAdapter<ModelMerger>
{
	@Override
	public Class<ModelMerger> getSupportedClass()
	{
		return ModelMerger.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, ModelMerger modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		CoreWire output = logicWiresPerPin.get(modelComponent.getPin("O"));
		ReadEnd[] inputEnds = new ReadEnd[modelComponent.logicWidth];
		for (int i = 0; i < modelComponent.logicWidth; i++)
		{
			CoreWire input = logicWiresPerPin.get(modelComponent.getPin("I" + (modelComponent.logicWidth - 1 - i)));
			CoreWire.fuse(input, output, 0, i);
			inputEnds[i] = input.createReadOnlyEnd();
		}
		modelComponent.setLogicModelBinding(inputEnds, output.createReadOnlyEnd());
	}
}