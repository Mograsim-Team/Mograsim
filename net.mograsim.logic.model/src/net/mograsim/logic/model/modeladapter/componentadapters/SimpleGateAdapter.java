package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularModelGate;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SimpleGateAdapter<G extends SimpleRectangularModelGate> implements ComponentAdapter<G>
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
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, G modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd out = logicWiresPerPin.get(modelComponent.getPin("Y")).createReadWriteEnd();

		// TODO can we do this prettier?
		int inputPinCount = modelComponent.getPins().size() - 1;
		ReadEnd[] ins = new ReadEnd[inputPinCount];
		for (int i = 0; i < inputPinCount; i++)
			ins[i] = logicWiresPerPin.get(modelComponent.getPin(String.valueOf((char) ('A' + i)))).createReadOnlyEnd();

		constructor.newComponent(timeline, params.gateProcessTime, out, ins);
	}

	public static interface ComponentConstructor
	{
		public CoreComponent newComponent(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd[] ins);
	}

}