package net.mograsim.logic.model.am2900.components;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;
import net.mograsim.machine.standard.memory.CoreWordAddressableMemory;
import net.mograsim.machine.standard.memory.WordAddressableMemory;

public class ModelAm2900MainMemoryAdapter implements ComponentAdapter<ModelAm2900MainMemory>
{

	@Override
	public Class<ModelAm2900MainMemory> getSupportedClass()
	{
		return ModelAm2900MainMemory.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelAm2900MainMemory modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		ReadEnd mode = logicWiresPerPin.get(modelComponent.getReadWritePin()).createReadOnlyEnd();
		ReadEnd clock = logicWiresPerPin.get(modelComponent.getClockPin()).createReadOnlyEnd();
		CoreWordAddressableMemory mem = new CoreWordAddressableMemory(timeline, 2,
				new WordAddressableMemory(modelComponent.getDefinition()), data, mode, address, clock);
		modelComponent.setCoreModelBinding(mem);
	}
}
