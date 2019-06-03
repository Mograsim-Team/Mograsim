package net.mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

public class NandGateAdapter implements ComponentAdapter<GUINandGate>
{
	@Override
	public Class<GUINandGate> getSupportedClass()
	{
		return GUINandGate.class;
	}

	@Override
	@SuppressWarnings("unused") // AndGate and NotGate
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUINandGate guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		Wire i0 = logicWiresPerPin.get(guiComponent.getInputPins().get(0));
		Wire i1 = logicWiresPerPin.get(guiComponent.getInputPins().get(1));
		Wire o = logicWiresPerPin.get(guiComponent.getOutputPin());
		Wire w = new Wire(timeline, guiComponent.getOutputPin().logicWidth, 1);

		new AndGate(timeline, 1, w.createReadWriteEnd(), i0.createReadOnlyEnd(), i1.createReadOnlyEnd());
		new NotGate(timeline, Math.max(1, params.gateProcessTime - 2), w.createReadOnlyEnd(), o.createReadWriteEnd());
	}
}