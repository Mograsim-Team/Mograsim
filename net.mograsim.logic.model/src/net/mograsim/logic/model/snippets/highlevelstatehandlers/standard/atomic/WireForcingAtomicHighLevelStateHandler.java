package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.HighLevelStateHandlerContext;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class WireForcingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private SubmodelComponent component;
	private int logicWidth;
	private final List<GUIWire> wiresToForce;
	private final List<GUIWire> wiresToForceInverted;

	public WireForcingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context)
	{
		this(context, null);
	}

	public WireForcingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context, WireForcingAtomicHighLevelStateHandlerParams params)
	{
		this.component = context.component;
		this.wiresToForce = new ArrayList<>();
		this.wiresToForceInverted = new ArrayList<>();
		if (params != null)
		{
			Map<String, GUIWire> wiresByName = component.submodel.getWiresByName();
			setWiresToForce(params.wiresToForce.stream().map((Function<String, GUIWire>) wiresByName::get).collect(Collectors.toList()),
					params.wiresToForceInverted.stream().map((Function<String, GUIWire>) wiresByName::get).collect(Collectors.toList()));
		}
	}

	public void set(List<GUIWire> wiresToForce, List<GUIWire> wiresToForceInverted)
	{
		setWiresToForce(wiresToForce, wiresToForceInverted);
	}

	public void setWiresToForce(List<GUIWire> wiresToForce, List<GUIWire> wiresToForceInverted)
	{
		clearWiresToForce();
		for (GUIWire wire : wiresToForce)
			addWireToForce(wire, false);
		for (GUIWire wire : wiresToForceInverted)
			addWireToForce(wire, true);
	}

	public void addWireToForce(GUIWire wire, boolean inverted)
	{
		if (component.submodel.getWiresByName().get(wire.name) != wire)
			throw new IllegalArgumentException("Can only force wires belonging to the parent component of this handler");
		if (logicWidth < 1)
			logicWidth = wire.logicWidth;
		else if (wire.logicWidth != logicWidth)
			throw new IllegalArgumentException("Can only force wires of the same logic width");
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

	@Override
	public Object getHighLevelState()
	{
		BitVector result = BitVector.of(Bit.ZERO, logicWidth);
		for (GUIWire wire : wiresToForceInverted)
			if (wire.hasLogicModelBinding())
				result = result.or(wire.getWireValues());
		result = result.not();
		for (GUIWire wire : wiresToForce)
			if (wire.hasLogicModelBinding())
				result = result.and(wire.getWireValues());
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
		for (GUIWire wire : wiresToForce)
			if (wire.hasLogicModelBinding())
				wire.forceWireValues(vector);
		vector = vector.not();
		for (GUIWire wire : wiresToForceInverted)
			if (wire.hasLogicModelBinding())
				wire.forceWireValues(vector);
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