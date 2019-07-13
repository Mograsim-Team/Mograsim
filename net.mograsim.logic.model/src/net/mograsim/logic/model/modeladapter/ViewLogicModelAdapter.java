package net.mograsim.logic.model.modeladapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class ViewLogicModelAdapter
{
	private final static Map<Class<? extends GUIComponent>, ComponentAdapter<? extends GUIComponent>> componentAdapters = new HashMap<>();

	public static void addComponentAdapter(ComponentAdapter<? extends GUIComponent> componentAdapter)
	{
		componentAdapters.put(componentAdapter.getSupportedClass(), componentAdapter);
	}

	public static Timeline convert(ViewModel viewModel, LogicModelParameters params)
	{
		// TODO replace Timeline with LogicModel as soon as it exists
		Timeline timeline = new Timeline(10);

		convert(viewModel, params, timeline, Map.of());

		return timeline;
	}

	private static void convert(ViewModel viewModel, LogicModelParameters params, Timeline timeline, Map<Pin, Wire> externalWires)
	{
		Map<Pin, Wire> logicWiresPerPin = convertWires(getAllPins(viewModel), viewModel.getWires(), externalWires, params, timeline);
		Map<Pin, Wire> logicWiresPerPinUnmodifiable = Collections.unmodifiableMap(logicWiresPerPin);

		for (GUIComponent guiComp : viewModel.getComponentsByName().values())
		{
			if (guiComp instanceof SubmodelComponent)
			{
				SubmodelComponent guiCompCasted = (SubmodelComponent) guiComp;
				Map<String, Pin> supermodelPins = guiCompCasted.getSupermodelPins();
				Map<Pin, Wire> externalWiresForSubmodel = supermodelPins.entrySet().stream()
						.collect(Collectors.toMap(e -> guiCompCasted.getSubmodelPin(e.getKey()), e -> logicWiresPerPin.get(e.getValue())));
				convert(guiCompCasted.submodel, params, timeline, externalWiresForSubmodel);
			} else if (guiComp instanceof WireCrossPoint)
			{
				WireCrossPoint guiCompCasted = (WireCrossPoint) guiComp;
				guiCompCasted.setLogicModelBinding(logicWiresPerPin.get(guiCompCasted.getPin()).createReadOnlyEnd());
			} else if (!(guiComp instanceof SubmodelInterface))// nothing to do for SubmodelInterfaces
				createAndLinkComponent(timeline, params, guiComp, logicWiresPerPinUnmodifiable, componentAdapters.get(guiComp.getClass()));
		}
	}

	private static Set<Pin> getAllPins(ViewModel viewModel)
	{
		return viewModel.getComponentsByName().values().stream().flatMap(component -> component.getPins().values().stream())
				.collect(Collectors.toSet());
	}

	private static Map<Pin, Wire> convertWires(Set<Pin> allPins, List<GUIWire> wires, Map<Pin, Wire> externalWires,
			LogicModelParameters params, Timeline timeline)
	{
		Map<Pin, Set<Pin>> connectedPinGroups = getConnectedPinGroups(allPins, wires);
		Map<Pin, Wire> logicWiresPerPin = createLogicWires(params, timeline, connectedPinGroups, externalWires);
		setGUIWiresLogicModelBinding(wires, logicWiresPerPin);
		return logicWiresPerPin;
	}

	private static Map<Pin, Wire> createLogicWires(LogicModelParameters params, Timeline timeline, Map<Pin, Set<Pin>> connectedPinGroups,
			Map<Pin, Wire> externalWires)
	{
		Map<Pin, Wire> logicWiresPerPin = new HashMap<>();
		Map<Set<Pin>, Wire> logicWiresPerPinGroup = new HashMap<>();
		for (Entry<Pin, Set<Pin>> e : connectedPinGroups.entrySet())
			logicWiresPerPin.put(e.getKey(), logicWiresPerPinGroup.computeIfAbsent(e.getValue(), set ->
			{
				Wire externalWire = null;
				for (Pin p : set)
				{
					Wire externalWireCandidate = externalWires.get(p);
					if (externalWireCandidate != null)
						if (externalWire == null)
							externalWire = externalWireCandidate;
						else if (externalWire.length == externalWireCandidate.length)
							Wire.fuse(externalWire, externalWireCandidate);
						else
							throw new IllegalArgumentException(
									"Two pins to external wires with different logicWidths can't be connected directly");
				}
				return externalWire == null ? new Wire(timeline, e.getKey().logicWidth, params.wireTravelTime) : externalWire;
			}));
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
	private static <G extends GUIComponent> void createAndLinkComponent(Timeline timeline, LogicModelParameters params,
			GUIComponent guiComponent, Map<Pin, Wire> logicWiresPerPin, ComponentAdapter<G> adapter)
	{
		if (adapter == null)
			throw new IllegalArgumentException("Unknown component class: " + guiComponent.getClass());
		adapter.createAndLinkComponent(timeline, params, (G) guiComponent, logicWiresPerPin);
	}

	private ViewLogicModelAdapter()
	{
		throw new UnsupportedOperationException("No ViewLogicModelConverter instances");
	}
}