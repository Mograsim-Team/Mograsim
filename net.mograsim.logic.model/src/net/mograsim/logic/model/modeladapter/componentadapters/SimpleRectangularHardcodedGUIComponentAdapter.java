package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SimpleRectangularHardcodedGUIComponentAdapter implements ComponentAdapter<SimpleRectangularHardcodedGUIComponent>
{
	@Override
	public Class<SimpleRectangularHardcodedGUIComponent> getSupportedClass()
	{
		return SimpleRectangularHardcodedGUIComponent.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, SimpleRectangularHardcodedGUIComponent guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		Map<String, ReadEnd> readEnds = new HashMap<>();
		Map<String, ReadWriteEnd> readWriteEnds = new HashMap<>();

		AtomicReference<Object> state = new AtomicReference<>();

		Runnable recalculate = () -> state.updateAndGet(s -> guiComponent.recalculate(s, readEnds, readWriteEnds));
		LogicObserver logicObs = c -> timeline.addEvent(e -> recalculate.run(), params.gateProcessTime);

		guiComponent.setLogicModelBindingAndResetState(state, recalculate);

		for (Pin pin : guiComponent.getPins().values())
		{
			Wire wire = logicWiresPerPin.get(pin);
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