package net.mograsim.logic.model.modeladapter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.modeladapter.componentadapters.ComponentAdapter;

public class ViewLogicModelAdapter
{
	private final static Map<Class<? extends ModelComponent>, ComponentAdapter<? extends ModelComponent>> componentAdapters = new HashMap<>();

	public static void addComponentAdapter(ComponentAdapter<? extends ModelComponent> componentAdapter)
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

	private static void convert(ViewModel viewModel, LogicModelParameters params, Timeline timeline, Map<Pin, CoreWire> externalWires)
	{
		Map<Pin, CoreWire> logicWiresPerPin = convertWires(getAllPins(viewModel), viewModel.getWiresByName().values(), externalWires,
				params, timeline);
		Map<Pin, CoreWire> logicWiresPerPinUnmodifiable = Collections.unmodifiableMap(logicWiresPerPin);

		for (ModelComponent modelComp : viewModel.getComponentsByName().values())
		{
			if (modelComp instanceof SubmodelComponent)
			{
				SubmodelComponent modelCompCasted = (SubmodelComponent) modelComp;
				Map<String, Pin> supermodelPins = modelCompCasted.getSupermodelPins();
				Map<Pin, CoreWire> externalWiresForSubmodel = supermodelPins.entrySet().stream().collect(
						Collectors.toMap(e -> modelCompCasted.getSubmodelPin(e.getKey()), e -> logicWiresPerPin.get(e.getValue())));
				convert(modelCompCasted.submodel, params, timeline, externalWiresForSubmodel);
			} else if (modelComp instanceof ModelWireCrossPoint)
			{
				ModelWireCrossPoint modelCompCasted = (ModelWireCrossPoint) modelComp;
				modelCompCasted.setLogicModelBinding(logicWiresPerPin.get(modelCompCasted.getPin()).createReadOnlyEnd());
			} else if (!(modelComp instanceof SubmodelInterface))// nothing to do for SubmodelInterfaces
				createAndLinkComponent(timeline, params, modelComp, logicWiresPerPinUnmodifiable);
		}
	}

	private static Set<Pin> getAllPins(ViewModel viewModel)
	{
		return viewModel.getComponentsByName().values().stream().flatMap(component -> component.getPins().values().stream())
				.collect(Collectors.toSet());
	}

	private static Map<Pin, CoreWire> convertWires(Set<Pin> allPins, Collection<ModelWire> wires, Map<Pin, CoreWire> externalWires,
			LogicModelParameters params, Timeline timeline)
	{
		Map<Pin, Set<Pin>> connectedPinGroups = getConnectedPinGroups(allPins, wires);
		Map<Pin, CoreWire> logicWiresPerPin = createLogicWires(params, timeline, connectedPinGroups, externalWires);
		setModelWiresLogicModelBinding(wires, logicWiresPerPin);
		return logicWiresPerPin;
	}

	private static Map<Pin, CoreWire> createLogicWires(LogicModelParameters params, Timeline timeline,
			Map<Pin, Set<Pin>> connectedPinGroups, Map<Pin, CoreWire> externalWires)
	{
		Map<Pin, CoreWire> logicWiresPerPin = new HashMap<>();
		Map<Set<Pin>, CoreWire> logicWiresPerPinGroup = new HashMap<>();
		for (Entry<Pin, Set<Pin>> e : connectedPinGroups.entrySet())
			logicWiresPerPin.put(e.getKey(), logicWiresPerPinGroup.computeIfAbsent(e.getValue(), set ->
			{
				CoreWire externalWire = null;
				for (Pin p : set)
				{
					CoreWire externalWireCandidate = externalWires.get(p);
					if (externalWireCandidate != null)
						if (externalWire == null)
							externalWire = externalWireCandidate;
						else if (externalWire.width == externalWireCandidate.width)
							CoreWire.fuse(externalWire, externalWireCandidate);
						else
							throw new IllegalArgumentException(
									"Two pins to external wires with different logicWidths can't be connected directly");
				}
				return externalWire == null ? new CoreWire(timeline, e.getKey().logicWidth, params.wireTravelTime) : externalWire;
			}));
		return logicWiresPerPin;
	}

	private static void setModelWiresLogicModelBinding(Collection<ModelWire> wires, Map<Pin, CoreWire> logicWiresPerPin)
	{
		Map<CoreWire, ReadEnd> modelWireSharedReadEnd = logicWiresPerPin.values().stream().distinct()
				.collect(Collectors.toMap(Function.identity(), CoreWire::createReadOnlyEnd));
		for (ModelWire modelWire : wires)
			modelWire.setLogicModelBinding(modelWireSharedReadEnd.get(logicWiresPerPin.get(modelWire.getPin1())));
	}

	private static Map<Pin, Set<Pin>> getConnectedPinGroups(Set<Pin> allPins, Collection<ModelWire> wires)
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
	private static <G extends ModelComponent> void createAndLinkComponent(Timeline timeline, LogicModelParameters params,
			ModelComponent modelComponent, Map<Pin, CoreWire> logicWiresPerPin)
	{
		Class<?> cls = modelComponent.getClass();
		ComponentAdapter<? super G> adapter = null;
		while (cls != ModelComponent.class && adapter == null)
		{
			adapter = (ComponentAdapter<? super G>) componentAdapters.get(cls);
			cls = cls.getSuperclass();
		}
		if (adapter == null)
			throw new IllegalArgumentException("Unknown component class: " + modelComponent.getClass());
		adapter.createAndLinkComponent(timeline, params, (G) modelComponent, logicWiresPerPin);
	}

	private ViewLogicModelAdapter()
	{
		throw new UnsupportedOperationException("No ViewLogicModelConverter instances");
	}
}