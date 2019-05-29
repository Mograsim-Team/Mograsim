package era.mi.gui.modeladapter.componentadapters;

import java.util.List;
import java.util.Map;

import era.mi.gui.model.components.SimpleRectangularGUIGate;
import era.mi.gui.model.wires.Pin;
import era.mi.gui.modeladapter.LogicModelParameters;
import era.mi.logic.components.Component;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class SimpleGateAdapter<G extends SimpleRectangularGUIGate> implements ComponentAdapter<G>
{
	private final Class<G> supportedClass;
	private final ComponentConstructor constructor;

	public SimpleGateAdapter(Class<G> supportedClass, ComponentConstructor constructor)
	{
		this.supportedClass = supportedClass;
		this.constructor = constructor;
	}

	@Override
	public Class<G> getSupportedClass()
	{
		return supportedClass;
	}

	@Override
	public Component createAndLinkComponent(Timeline timeline, LogicModelParameters params, G guiComponent, Map<Pin, Wire> logicWiresPerPin)
	{
		ReadWriteEnd out = logicWiresPerPin.get(guiComponent.getOutputPin()).createReadWriteEnd();
		List<Pin> inputPins = guiComponent.getInputPins();
		ReadEnd[] ins = new ReadEnd[inputPins.size()];
		for (int i = 0; i < inputPins.size(); i++)
			ins[i] = logicWiresPerPin.get(inputPins.get(i)).createReadOnlyEnd();
		return constructor.newComponent(timeline, params.gateProcessTime, out, ins);
	}

	public static interface ComponentConstructor
	{
		public Component newComponent(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd[] ins);
	}

}