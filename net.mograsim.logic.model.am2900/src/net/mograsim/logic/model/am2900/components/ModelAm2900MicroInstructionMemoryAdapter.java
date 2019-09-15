package net.mograsim.logic.model.am2900.components;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.mi.components.CoreMicroInstructionMemory;

public class ModelAm2900MicroInstructionMemoryAdapter implements ComponentAdapter<ModelAm2900MicroInstructionMemory>
{

	@Override
	public Class<ModelAm2900MicroInstructionMemory> getSupportedClass()
	{
		return ModelAm2900MicroInstructionMemory.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelAm2900MicroInstructionMemory modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		CoreMicroInstructionMemory mem = new CoreMicroInstructionMemory(timeline, 2,
				new StandardMicroInstructionMemory(modelComponent.getDefinition()), data, address);
		modelComponent.setCoreModelBinding(mem);
	}

}
