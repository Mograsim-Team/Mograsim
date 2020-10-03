package net.mograsim.machine.mi.components;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class MPROMAdapter implements ComponentAdapter<ModelMPROM>
{
	@Override
	public Class<ModelMPROM> getSupportedClass()
	{
		return ModelMPROM.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, ModelMPROM modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		CoreMPROM mem = new CoreMPROM(timeline, 2, modelComponent.getDefinition(), data, address);
		modelComponent.setCoreModelBinding(mem);
	}
}