package net.mograsim.logic.ui.modeladapter;

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

import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.components.GUIAndGate;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.GUINotGate;
import net.mograsim.logic.ui.model.components.GUIOrGate;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.modeladapter.componentadapters.ComponentAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.ManualSwitchAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.core.components.Component;
import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.core.components.gates.OrGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public class ViewLogicModelAdapter
{
	private final static Map<Class<? extends GUIComponent>, ComponentAdapter<? extends GUIComponent>> componentAdapters;
	static
	{
		Set<ComponentAdapter<? extends GUIComponent>> componentAdaptersModifiable = new HashSet<>();
		componentAdaptersModifiable.add(new SimpleGateAdapter<>(GUIOrGate.class, OrGate::new));
		componentAdaptersModifiable.add(new SimpleGateAdapter<>(GUIAndGate.class, AndGate::new));
		componentAdaptersModifiable.add(new SimpleGateAdapter<>(GUINotGate.class, (t, p, o, i) -> new NotGate(t, p, i[0], o)));
		componentAdaptersModifiable.add(new ManualSwitchAdapter());
		// TODO list all "primitive" adapters here
		componentAdapters = Collections.unmodifiableMap(
				componentAdaptersModifiable.stream().collect(Collectors.toMap(ComponentAdapter::getSupportedClass, Function.identity())));
	}

	public static Timeline convert(ViewModel viewModel, LogicModelParameters params)
	{
		// TODO replace Timeline with LogicModel as soon as it exists
		Timeline timeline = new Timeline(10);

		Map<Pin, Wire> logicWiresPerPin = convertWires(
				viewModel.getComponents().stream().flatMap(component -> component.getPins().stream()).collect(Collectors.toSet()),
				viewModel.getWires(), params, timeline);
		Map<Pin, Wire> logicWiresPerPinUnmodifiable = Collections.unmodifiableMap(logicWiresPerPin);

		Map<GUIComponent, Component> oneToOneComponents = new HashMap<>();
		for (GUIComponent guiComp : viewModel.getComponents())
		{
			if (!(guiComp instanceof WireCrossPoint))
				oneToOneComponents.put(guiComp, createAndLinkComponent(timeline, params, guiComp, logicWiresPerPinUnmodifiable,
						componentAdapters.get(guiComp.getClass())));
			else
			{
				WireCrossPoint guiCompCasted = (WireCrossPoint) guiComp;
				guiCompCasted.setLogicModelBinding(logicWiresPerPin.get(guiCompCasted.getPin()).createReadOnlyEnd());
			}
		}

		// TODO handle complex components

		List<Component> logicComponents = new ArrayList<>();
		logicComponents.addAll(oneToOneComponents.values());

		return timeline;
	}

	private static Map<Pin, Wire> convertWires(Set<Pin> allPins, List<GUIWire> wires, LogicModelParameters params, Timeline timeline)
	{
		Map<Pin, Set<Pin>> connectedPinGroups = getConnectedPinGroups(allPins, wires);
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
		Map<Wire, ReadEnd> guiWireSharedReadEnd = logicWiresPerPin.values().stream().distinct()
				.collect(Collectors.toMap(Function.identity(), Wire::createReadOnlyEnd));
		for (GUIWire guiWire : wires)
			guiWire.setLogicModelBinding(guiWireSharedReadEnd.get(logicWiresPerPin.get(guiWire.getPin1())));
	}

	private static Map<Pin, Set<Pin>> getConnectedPinGroups(Set<Pin> allPins, List<GUIWire> wires)
	{
		Map<Pin, Set<Pin>> connectedPinsPerPin = new HashMap<>();

		for (Pin p : allPins)
		{
			HashSet<Pin> connectedPins = new HashSet<>();
			connectedPins.add(p);
			connectedPinsPerPin.put(p, connectedPins);
		}

		wires.forEach(wire ->
		{
			Pin pin1 = wire.getPin1();
			Pin pin2 = wire.getPin2();

			Set<Pin> pin1ConnectedPins = connectedPinsPerPin.get(pin1);
			Set<Pin> pin2ConnectedPins = connectedPinsPerPin.get(pin2);

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
		if (adapter == null)
			throw new IllegalArgumentException("Unknown component class: " + guiComponent.getClass());
		return adapter.createAndLinkComponent(timeline, params, (G) guiComponent, logicWiresPerPin);
	}

	private ViewLogicModelAdapter()
	{
		throw new UnsupportedOperationException("No ViewLogicModelConverter instances");
	}
}