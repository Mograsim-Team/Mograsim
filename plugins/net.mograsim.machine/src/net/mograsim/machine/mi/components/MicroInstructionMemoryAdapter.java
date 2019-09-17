package net.mograsim.machine.mi.components;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class MicroInstructionMemoryAdapter implements ComponentAdapter<ModelMicroInstructionMemory>
{
	@Override
	public Class<ModelMicroInstructionMemory> getSupportedClass()
	{
		return ModelMicroInstructionMemory.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelMicroInstructionMemory modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		CoreMicroInstructionMemory mem = new CoreMicroInstructionMemory(timeline, 2, modelComponent.getDefinition(), data, address);
		modelComponent.setCoreModelBinding(mem);
	}
}