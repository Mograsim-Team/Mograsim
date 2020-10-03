package net.mograsim.machine.standard.memory;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;
import net.mograsim.machine.BitVectorMemory;
import net.mograsim.machine.BitVectorMemoryDefinition;

public class BitVectorMemoryAdapter implements ComponentAdapter<AbstractModelBitVectorMemory<?, ?>>
{
	@SuppressWarnings({ "cast", "unchecked", "rawtypes" })
	@Override
	public Class<AbstractModelBitVectorMemory<?, ?>> getSupportedClass()
	{
		return (Class<AbstractModelBitVectorMemory<?, ?>>) (Class) AbstractModelBitVectorMemory.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, AbstractModelBitVectorMemory<?, ?> modelComponent,
			Map<Pin, CoreWire> logicWiresPerPin)
	{
		createAndLinkComponentCasted(timeline, params, modelComponent, logicWiresPerPin);
	}

	private static <M extends BitVectorMemory, D extends BitVectorMemoryDefinition> void createAndLinkComponentCasted(Timeline timeline,
			CoreModelParameters params, AbstractModelBitVectorMemory<M, D> modelComponent, Map<Pin, CoreWire> logicWiresPerPin)
	{
		ReadWriteEnd data = logicWiresPerPin.get(modelComponent.getDataPin()).createReadWriteEnd();
		ReadEnd address = logicWiresPerPin.get(modelComponent.getAddressPin()).createReadOnlyEnd();
		ReadEnd rwBit = modelComponent.isReadonly() ? null : logicWiresPerPin.get(modelComponent.getReadWritePin()).createReadOnlyEnd();
		// TODO introduce memoryProcessTime
		CoreBitVectorMemory<M> mem = new CoreBitVectorMemory<>(timeline, 2, modelComponent.getDefinition(), data, rwBit, address,
				modelComponent.isReadonly());
		modelComponent.setCoreModelBinding(mem);
	}
}