package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SimpleRectangularHardcodedModelComponentAdapter implements ComponentAdapter<SimpleRectangularHardcodedModelComponent>
{
	@Override
	public Class<SimpleRectangularHardcodedModelComponent> getSupportedClass()
	{
		return SimpleRectangularHardcodedModelComponent.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params,
			SimpleRectangularHardcodedModelComponent modelComponent, Map<Pin, CoreWire> logicWiresPerPin)
	{
		Map<String, ReadEnd> readEnds = new HashMap<>();
		Map<String, ReadWriteEnd> readWriteEnds = new HashMap<>();

		AtomicReference<Object> state = new AtomicReference<>();

		Runnable recalculate = () -> state.updateAndGet(s -> modelComponent.recalculate(s, readEnds, readWriteEnds));
		LogicObserver logicObs = c -> timeline.addEvent(e -> recalculate.run(), params.gateProcessTime);

		modelComponent.setLogicModelBindingAndResetState(state, recalculate);

		for (Pin pin : modelComponent.getPins().values())
		{
			CoreWire wire = logicWiresPerPin.get(pin);
			ReadEnd end;
			if (pin.usage != PinUsage.INPUT)
			{
				ReadWriteEnd rwEnd = wire.createReadWriteEnd();
				readWriteEnds.put(pin.name, rwEnd);
				end = rwEnd;
			} else
				end = wire.createReadOnlyEnd();
			readEnds.put(pin.name, end);
			if (pin.usage != PinUsage.OUTPUT)
				end.registerObserver(logicObs);
		}
	}
}