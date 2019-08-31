package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

//TODO support HighLevelStates
public class SimpleRectangularHardcodedGUIComponentAdapter implements ComponentAdapter<SimpleRectangularHardcodedGUIComponent>
{
	private final Function<SimpleRectangularHardcodedGUIComponent, RecalculateFunction> recalculateFunctionGenerator;

	public SimpleRectangularHardcodedGUIComponentAdapter(
			Function<SimpleRectangularHardcodedGUIComponent, RecalculateFunction> recalculateFunctionGenerator)
	{
		this.recalculateFunctionGenerator = recalculateFunctionGenerator;
	}

	@Override
	public Class<SimpleRectangularHardcodedGUIComponent> getSupportedClass()
	{
		return SimpleRectangularHardcodedGUIComponent.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, SimpleRectangularHardcodedGUIComponent guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		RecalculateFunction recalculate = recalculateFunctionGenerator.apply(guiComponent);
		Map<String, ReadEnd> readEnds = new HashMap<>();
		Map<String, ReadWriteEnd> readWriteEnds = new HashMap<>();

		AtomicReference<Object> state = new AtomicReference<>();

		LogicObserver logicObs = c -> timeline.addEvent(e -> state.set(recalculate.recalculate(state.get(), readEnds, readWriteEnds)),
				params.gateProcessTime);

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

	public static interface RecalculateFunction
	{
		public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds);
	}
}