package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreTriStateBuffer;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.GUITriStateBuffer;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class TriStateBufferAdapter implements ComponentAdapter<GUITriStateBuffer>
{
	@Override
	public Class<GUITriStateBuffer> getSupportedClass()
	{
		return GUITriStateBuffer.class;
	}

	@SuppressWarnings("unused")
	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUITriStateBuffer guiTsb,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadEnd in = logicWiresPerPin.get(guiTsb.getPin("IN")).createReadOnlyEnd();
		ReadEnd enable = logicWiresPerPin.get(guiTsb.getPin("EN")).createReadOnlyEnd();
		ReadWriteEnd out = logicWiresPerPin.get(guiTsb.getPin("OUT")).createReadWriteEnd();
		new CoreTriStateBuffer(timeline, params.gateProcessTime, in, out, enable);
	}
}
