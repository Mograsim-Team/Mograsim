package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class WireForcingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private final SubmodelComponent component;
	private int logicWidth;
	private final List<ModelWire> wiresToForce;
	private final List<ModelWire> wiresToForceUnmodifiable;
	private final List<ModelWire> wiresToForceInverted;
	private final List<ModelWire> wiresToForceInvertedUnmodifiable;

	private final Map<Consumer<Object>, LogicObserver> wireObsPerListener;

	public WireForcingAtomicHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public WireForcingAtomicHighLevelStateHandler(SubmodelComponent component, WireForcingAtomicHighLevelStateHandlerParams params)
	{
		this.component = component;
		this.wiresToForce = new ArrayList<>();
		this.wiresToForceUnmodifiable = Collections.unmodifiableList(wiresToForce);
		this.wiresToForceInverted = new ArrayList<>();
		this.wiresToForceInvertedUnmodifiable = Collections.unmodifiableList(wiresToForceInverted);

		this.wireObsPerListener = new HashMap<>();

		if (params != null)
		{
			Map<String, ModelWire> wiresByName = component.submodel.getWiresByName();
			setWiresToForce(params.wiresToForce.stream().map((Function<String, ModelWire>) wiresByName::get).collect(Collectors.toList()),
					params.wiresToForceInverted.stream().map((Function<String, ModelWire>) wiresByName::get).collect(Collectors.toList()));
		}
		component.submodel.addWireRemovedListener(w ->
		{
			wiresToForce.removeIf(w::equals);
			wiresToForceInverted.removeIf(w::equals);
		});
	}

	public void set(List<ModelWire> wiresToForce, List<ModelWire> wiresToForceInverted)
	{
		setWiresToForce(wiresToForce, wiresToForceInverted);
	}

	public void setWiresToForce(List<ModelWire> wiresToForce, List<ModelWire> wiresToForceInverted)
	{
		clearWiresToForce();
		for (ModelWire wire : wiresToForce)
			addWireToForce(wire, false);
		for (ModelWire wire : wiresToForceInverted)
			addWireToForce(wire, true);
	}

	public void addWireToForce(ModelWire wire, boolean inverted)
	{
		if (component.submodel.getWiresByName().get(wire.name) != wire)
			throw new IllegalArgumentException("Can only force wires belonging to the parent component of this handler");
		if (logicWidth < 1)
			logicWidth = wire.logicWidth;
		else if (wire.logicWidth != logicWidth)
			throw new IllegalArgumentException("Can only force wires of the same logic width");
		// this can add the same wire multiple times, but maybe there is a weird configuration where it is neccessary, due to race
		// conditions, to force the same wire twice.
		if (inverted)
			wiresToForceInverted.add(wire);
		else
			wiresToForce.add(wire);
	}

	public void clearWiresToForce()
	{
		wiresToForce.clear();
		wiresToForceInverted.clear();
		logicWidth = 0;
	}

	public List<ModelWire> getWiresToForce()
	{
		return wiresToForceUnmodifiable;
	}

	public List<ModelWire> getWiresToForceInverted()
	{
		return wiresToForceInvertedUnmodifiable;
	}

	@Override
	public Object getHighLevelState()
	{
		BitVector result = BitVector.of(Bit.Z, logicWidth);

		if (!wiresToForceInverted.isEmpty())
		{
			for (ModelWire wire : wiresToForceInverted)
				if (wire.hasCoreModelBinding())
					result = result.join(wire.getWireValues());
			result = result.not();
		}

		for (ModelWire wire : wiresToForce)
			if (wire.hasCoreModelBinding())
				result = result.join(wire.getWireValues());

		return result;
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		BitVector vector;
		if (newState instanceof Bit)
			vector = BitVector.of((Bit) newState);
		else
			vector = (BitVector) newState;
		for (ModelWire wire : wiresToForce)
			if (wire.hasCoreModelBinding())
				wire.forceWireValues(vector);
		vector = vector.not();
		for (ModelWire wire : wiresToForceInverted)
			if (wire.hasCoreModelBinding())
				wire.forceWireValues(vector);
	}

	@Override
	public void addListener(Consumer<Object> stateChanged)
	{
		if (wireObsPerListener.containsKey(stateChanged))
			return;
		AtomicReference<Object> lastStateRef = new AtomicReference<>(getHighLevelState());
		LogicObserver obs = w ->
		{
			Object newState = getHighLevelState();
			if (!Objects.equals(lastStateRef.getAndSet(newState), newState))
				stateChanged.accept(newState);
		};
		wireObsPerListener.put(stateChanged, obs);
		for (ModelWire w : wiresToForce)
			w.addObserver(obs);
		for (ModelWire w : wiresToForceInverted)
			w.addObserver(obs);
	}

	@Override
	public void removeListener(Consumer<Object> stateChanged)
	{
		LogicObserver obs = wireObsPerListener.remove(stateChanged);
		if (obs == null)
			return;
		for (ModelWire w : wiresToForce)
			w.removeObserver(obs);
		for (ModelWire w : wiresToForceInverted)
			w.removeObserver(obs);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "wireForcing";
	}

	@Override
	public WireForcingAtomicHighLevelStateHandlerParams getParamsForSerializing(IdentifyParams idParams)
	{
		WireForcingAtomicHighLevelStateHandlerParams params = new WireForcingAtomicHighLevelStateHandlerParams();
		params.wiresToForce = wiresToForce.stream().map(w -> w.name).collect(Collectors.toList());
		params.wiresToForceInverted = wiresToForceInverted.stream().map(w -> w.name).collect(Collectors.toList());
		return params;
	}

	public static class WireForcingAtomicHighLevelStateHandlerParams
	{
		public List<String> wiresToForce;
		public List<String> wiresToForceInverted;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier.setSnippetSupplier(
				WireForcingAtomicHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(WireForcingAtomicHighLevelStateHandlerParams.class, WireForcingAtomicHighLevelStateHandler::new));
	}
}