package net.mograsim.machine.standard.memory;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class WordAddressableMemoryAdapter implements ComponentAdapter<GUIMemoryWA>
{

	@Override
	public Class<GUIMemoryWA> getSupportedClass()
	{
		return GUIMemoryWA.class;
	}

	@SuppressWarnings("unused")
	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIMemoryWA guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(guiComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(guiComponent.getAddressPin()).createReadOnlyEnd();
		ReadEnd mode = logicWiresPerPin.get(guiComponent.getReadWritePin()).createReadOnlyEnd();
		new WordAddressableMemoryComponent(timeline, 2, guiComponent.maximalAddress, guiComponent.minimalAddress, data, mode, address);
	}

}
