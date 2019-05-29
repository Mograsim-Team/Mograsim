package era.mi.gui.modeladapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import era.mi.gui.model.ViewModel;
import era.mi.gui.model.components.GUIAndGate;
import era.mi.gui.model.components.GUIComponent;
import era.mi.gui.model.wires.GUIWire;
import era.mi.gui.model.wires.Pin;
import era.mi.gui.model.wires.WireCrossPoint;
import era.mi.gui.modeladapter.componentadapters.AndGateAdapter;
import era.mi.gui.modeladapter.componentadapters.ComponentAdapter;
import era.mi.logic.components.Component;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;

public class ViewLogicModelAdapter
{
	private final static Map<Class<? extends GUIComponent>, ComponentAdapter<? extends GUIComponent>> componentAdapters;
	static
	{
		Map<Class<? extends GUIComponent>, ComponentAdapter<? extends GUIComponent>> componentAdaptersModifiable = new HashMap<>();
		componentAdaptersModifiable.put(GUIAndGate.class, new AndGateAdapter());
		// TODO list all "primitive" adapters here
		componentAdapters = Collections.unmodifiableMap(componentAdaptersModifiable);
	}

	public static Timeline convert(ViewModel viewModel, LogicModelParameters params)
	{
		// TODO replace Timeline with LogicModel as soon as it exists
		Timeline timeline = new Timeline(10);

		Map<Pin, Wire> logicWiresPerPin = convertWires(viewModel.getWires(), params, timeline);
		Map<Pin, Wire> logicWiresPerPinUnmodifiable = Collections.unmodifiableMap(logicWiresPerPin);

		Map<GUIComponent, Component> oneToOneComponents = new HashMap<>();
		for (GUIComponent guiComp : viewModel.getComponents())
		{
			// WireCrossPoints just vanish
			if (!(guiComp instanceof WireCrossPoint))
				oneToOneComponents.put(guiComp, createAndLinkComponent(timeline, params, guiComp, logicWiresPerPinUnmodifiable,
						componentAdapters.get(guiComp.getClass())));
		}

		// TODO handle complex components

		List<Component> logicComponents = new ArrayList<>();
		logicComponents.addAll(oneToOneComponents.values());

		return timeline;
	}

	private static Map<Pin, Wire> convertWires(List<GUIWire> wires, LogicModelParameters params, Timeline timeline)
	{
		Map<Pin, Set<Pin>> connectedPinGroups = getConnectedPinGroups(wires);
		Map<Pin, Wire> logicWiresPerPin = createLogicWires(params, timeline, connectedPinGroups);
		setGUIWiresLogicModelBinding(wires, logicWiresPerPin);
		return logicWiresPerPin;
	}

	private static Map<Pin, Wire> createLogicWires(LogicModelParameters params, Timeline timeline, Map<Pin, Set<Pin>> connectedPinGroups)
	{
		Map<Pin, Wire> logicWiresPerPin = new HashMap<>();
		Map<Set<Pin>, Wire> logicWiresPerPinGroup = new HashMap<>();
		for (Entry<Pin, Set<Pin>> e : connectedPinGroups.entrySet())
			logicWiresPerPin.put(e.getKey(), logicWiresPerPinGroup.computeIfAbsent(e.getValue(),
					set -> new Wire(timeline, e.getKey().logicWidth, params.wireTravelTime)));
		return logicWiresPerPin;
	}

	private static void setGUIWiresLogicModelBinding(List<GUIWire> wires, Map<Pin, Wire> logicWiresPerPin)
	{
		Map<Wire, ReadEnd> guiWireSharedReadEnd = logicWiresPerPin.values().stream()
				.collect(Collectors.toMap(Function.identity(), Wire::createReadOnlyEnd));
		for (GUIWire guiWire : wires)
			guiWire.setLogicModelBinding(guiWireSharedReadEnd.get(logicWiresPerPin.get(guiWire.getPin1())));
	}

	private static Map<Pin, Set<Pin>> getConnectedPinGroups(List<GUIWire> wires)
	{
		Map<Pin, Set<Pin>> connectedPinsPerPin = new HashMap<>();
		wires.forEach(wire ->
		{
			Pin pin1 = wire.getPin1();
			Pin pin2 = wire.getPin2();

			Set<Pin> pin1ConnectedPins = connectedPinsPerPin.putIfAbsent(pin1, new HashSet<>());
			Set<Pin> pin2ConnectedPins = connectedPinsPerPin.putIfAbsent(pin2, new HashSet<>());

			pin1ConnectedPins.addAll(pin2ConnectedPins);
			pin1ConnectedPins.add(pin1);
			pin1ConnectedPins.add(pin2);

			pin2ConnectedPins.forEach(pin -> connectedPinsPerPin.put(pin, pin1ConnectedPins));
		});
		return connectedPinsPerPin;
	}

	@SuppressWarnings("unchecked")
	private static <G extends GUIComponent> Component createAndLinkComponent(Timeline timeline, LogicModelParameters params,
			GUIComponent guiComponent, Map<Pin, Wire> logicWiresPerPin, ComponentAdapter<G> adapter)
	{
		return adapter.createAndLinkComponent(timeline, params, (G) guiComponent, logicWiresPerPin);
	}

	private ViewLogicModelAdapter()
	{
		throw new UnsupportedOperationException("No ViewLogicModelConverter instances");
	}
}