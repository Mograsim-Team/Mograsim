package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent.Usage;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class SimpleRectangularHardcodedGUIComponentAdapter implements ComponentAdapter<SimpleRectangularHardcodedGUIComponent>
{
	private final Function<SimpleRectangularHardcodedGUIComponent, BiConsumer<Map<String, ReadEnd>, Map<String, ReadWriteEnd>>> recalculateFunctionGenerator;

	public SimpleRectangularHardcodedGUIComponentAdapter(
			Function<SimpleRectangularHardcodedGUIComponent, BiConsumer<Map<String, ReadEnd>, Map<String, ReadWriteEnd>>> recalculateFunctionGenerator)
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
		BiConsumer<Map<String, ReadEnd>, Map<String, ReadWriteEnd>> recalculate = recalculateFunctionGenerator.apply(guiComponent);
		Map<String, ReadEnd> readEnds = new HashMap<>();
		Map<String, ReadWriteEnd> readWriteEnds = new HashMap<>();

		LogicObserver logicObs = c -> recalculate.accept(readEnds, readWriteEnds);

		for (Pin pin : guiComponent.getPins().values())
		{
			Wire wire = logicWiresPerPin.get(pin);
			ReadEnd end;
			if (guiComponent.getPinUsage(pin) != Usage.INPUT)
			{
				ReadWriteEnd rwEnd = wire.createReadWriteEnd();
				readWriteEnds.put(pin.name, rwEnd);
				end = rwEnd;
			} else
				end = wire.createReadOnlyEnd();
			readEnds.put(pin.name, end);
			if (guiComponent.getPinUsage(pin) != Usage.OUTPUT)
				end.registerObserver(logicObs);
		}
	}
}