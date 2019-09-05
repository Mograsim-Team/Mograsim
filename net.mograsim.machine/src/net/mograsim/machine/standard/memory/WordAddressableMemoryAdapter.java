package net.mograsim.machine.standard.memory;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class WordAddressableMemoryAdapter implements ComponentAdapter<ModelMemoryWA>
{

	@Override
	public Class<ModelMemoryWA> getSupportedClass()
	{
		return ModelMemoryWA.class;
	}

	@SuppressWarnings("unused")
	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, ModelMemoryWA modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		ReadEnd mode = logicWiresPerPin.get(modelComponent.getReadWritePin()).createReadOnlyEnd();
		new WordAddressableMemoryComponent(timeline, 2, modelComponent.getDefinition(), data, mode, address);
	}

}
