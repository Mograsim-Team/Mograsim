package era.mi.gui.modeladapter.componentadapters;

import java.util.List;
import java.util.Map;

import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.wires.Pin;
import era.mi.gui.modeladapter.LogicModelParameters;
import era.mi.logic.components.Component;
import era.mi.logic.components.gates.AndGate;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class AndGateAdapter implements ComponentAdapter<GUIAndGate>
{
	@Override
	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIAndGate guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadWriteEnd out = logicWiresPerPin.get(guiComponent.getOutputPin()).createReadWriteEnd();
		List<Pin> inputPins = guiComponent.getInputPins();
		ReadEnd[] ins = new ReadEnd[inputPins.size()];
		for (int i = 0; i < inputPins.size(); i++)
			ins[i] = logicWiresPerPin.get(inputPins.get(i)).createReadOnlyEnd();
		return new AndGate(timeline, params.gateProcessTime, out, ins);
	}
}