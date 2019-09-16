package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreTriStateBuffer;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;

public class TriStateBufferAdapter implements ComponentAdapter<ModelTriStateBuffer>
{
	@Override
	public Class<ModelTriStateBuffer> getSupportedClass()
	{
		return ModelTriStateBuffer.class;
	}

	@SuppressWarnings("unused")
	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelTriStateBuffer modelTsb,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadEnd in = logicWiresPerPin.get(modelTsb.getPin("IN")).createReadOnlyEnd();
		ReadEnd enable = logicWiresPerPin.get(modelTsb.getPin("EN")).createReadOnlyEnd();
		ReadWriteEnd out = logicWiresPerPin.get(modelTsb.getPin("OUT")).createReadWriteEnd();
		new CoreTriStateBuffer(timeline, params.gateProcessTime, in, out, enable);
	}
}
