package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.util.ObservableAtomicReference;

public class SimpleRectangularHardcodedModelComponentAdapter implements ComponentAdapter<SimpleRectangularHardcodedModelComponent>
{
	@Override
	public Class<SimpleRectangularHardcodedModelComponent> getSupportedClass()
	{
		return SimpleRectangularHardcodedModelComponent.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params,
			SimpleRectangularHardcodedModelComponent modelComponent, Map<Pin, CoreWire> logicWiresPerPin)
	{
		Map<String, ReadEnd> readEnds = new HashMap<>();
		Map<String, ReadWriteEnd> readWriteEnds = new HashMap<>();

		ObservableAtomicReference<Object> state = new ObservableAtomicReference<>();

		Runnable recalculate = () -> state.updateAndGet(s -> modelComponent.recalculate(s, readEnds, readWriteEnds));
		LogicObserver logicObs = c -> recalculate.run();

		modelComponent.setCoreModelBindingAndResetState(state, recalculate);

		for (Pin pin : modelComponent.getPins().values())
		{
			CoreWire wire = logicWiresPerPin.get(pin);
			ReadEnd end;
			if (pin.usage != PinUsage.INPUT)
			{
				// TODO do this prettier
				CoreWire pseudoWire = new CoreWire(timeline, wire.width, params.hardcodedComponentProcessTime);
				CoreWire.fuse(wire, pseudoWire);
				readWriteEnds.put(pin.name, pseudoWire.createReadWriteEnd());
			}
			end = wire.createReadOnlyEnd();
			readEnds.put(pin.name, end);
			if (pin.usage != PinUsage.OUTPUT)
				end.registerObserver(logicObs);
		}
	}
}