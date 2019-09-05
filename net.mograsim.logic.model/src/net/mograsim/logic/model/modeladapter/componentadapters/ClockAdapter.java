package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.GUIClock;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class ClockAdapter implements ComponentAdapter<GUIClock>
{

	@Override
	public Class<GUIClock> getSupportedClass()
	{
		return GUIClock.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIClock guiClock, Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd out = logicWiresPerPin.get(guiClock.getOutputPin()).createReadWriteEnd();
		CoreClock c = new CoreClock(timeline, out, guiClock.getDelta());
		guiClock.setLogicModelBinding(c);
	}

}