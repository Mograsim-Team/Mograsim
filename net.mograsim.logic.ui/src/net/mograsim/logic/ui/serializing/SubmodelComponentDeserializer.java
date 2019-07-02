package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.MovablePin;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.ComponentCompositionParams;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.InnerWireParams;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.serializing.SubmodelComponentParams.ComponentCompositionParams.InnerComponentParams;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 */
public final class SubmodelComponentDeserializer
{
	private static final String rectC = SimpleRectangularSubmodelComponent.class.getSimpleName();

	/**
	 * Creates a {@link SubmodelComponent} from the {@link SubmodelComponentParams}, specified at the given path. The returned
	 * SubmodelComponent can also be e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the
	 * {@link SubmodelComponentParams} describe.
	 * 
	 * @param path The path of the file describing the {@link SubmodelComponentParams}, which define the new {@link SubmodelComponent}
	 * @return A new SubmodelComponent, as described in the file located at the given path
	 */
	public static SubmodelComponent create(ViewModelModifiable model, String path)
	{
		try
		{
			SubmodelComponentParams params = SubmodelComponentParams.readJson(path);
			SubmodelComponent ret = create(model, params);
			return ret;
		}
		catch (IOException e)
		{
			System.err.println("Failed to construct GUICustomComponent. Parameters were not found.");
			e.printStackTrace();
		}
		return new SimpleRectangularSubmodelComponent(model, 0, "ERROR");
	}

	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams}. The returned SubmodelComponent can also be
	 * e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the {@link SubmodelComponentParams} describe.
	 * 
	 * @param params The parameters describing the {@link SubmodelComponent}
	 * 
	 * @return A new SubmodelComponent, as described by the {@link SubmodelComponentParams}
	 */
	public static SubmodelComponent create(ViewModelModifiable model, SubmodelComponentParams params)
	{
		DeserializedSubmodelComponentI comp = null;
		if (rectC.equals(params.type))
		{
			comp = createRectComponent(model, params);
		}

		if (comp == null)
		{
			comp = createSubmodelComponent(model, params);
		}
		comp.setIdentifierDelegate(() -> params.name);
		initInnerComponents(comp, params.composition);
		return (SubmodelComponent) comp;
	}

	// May return null
	@SuppressWarnings("unchecked")
	private static DeserializedSimpleRectangularSubmodelComponent createRectComponent(ViewModelModifiable model,
			SubmodelComponentParams params)
	{
		try
		{
			Map<String, Object> m = params.specialized;
			DeserializedSimpleRectangularSubmodelComponent rect = new DeserializedSimpleRectangularSubmodelComponent(model,
					((Number) m.get(SimpleRectangularSubmodelComponent.kLogicWidth)).intValue(),
					(String) m.get(SimpleRectangularSubmodelComponent.kLabel));
			rect.setSubmodelScale(params.composition.innerScale);

			Object[] names = ((ArrayList<Object>) m.get(SimpleRectangularSubmodelComponent.kInCount)).toArray();
			rect.setInputPins(Arrays.copyOf(names, names.length, String[].class));

			names = ((ArrayList<Object>) m.get(SimpleRectangularSubmodelComponent.kOutCount)).toArray();
			rect.setOutputPins(Arrays.copyOf(names, names.length, String[].class));

			return rect;
		}
		catch (ClassCastException | NullPointerException e)
		{
			System.err.println("Failed to specialize component!");
			e.printStackTrace();
			return null;
		}
	}

	private static DeserializedSubmodelComponent createSubmodelComponent(ViewModelModifiable model, SubmodelComponentParams params)
	{
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model);
		comp.setSubmodelScale(params.composition.innerScale);
		comp.setSize(params.width, params.height);
		for (InterfacePinParams iPinParams : params.interfacePins)
		{
			comp.addSubmodelInterface(
					new MovablePin(comp, iPinParams.name, iPinParams.logicWidth, iPinParams.location.x, iPinParams.location.y));
		}
		return comp;
	}

	@SuppressWarnings("unused")
	private static void initInnerComponents(DeserializedSubmodelComponentI comp, ComponentCompositionParams params)
	{
		GUIComponent[] components = new GUIComponent[params.subComps.length];
		for (int i = 0; i < components.length; i++)
		{
			InnerComponentParams cParams = params.subComps[i];
			String path = cParams.name;
			components[i] = IndirectGUIComponentCreator.create(comp.getSubmodelModifiable(), cParams.name, cParams.params);
			components[i].moveTo(cParams.pos.x, cParams.pos.y);
		}

		for (int i = 0; i < params.innerWires.length; i++)
		{
			InnerWireParams innerWire = params.innerWires[i];
			new GUIWire(comp.getSubmodelModifiable(),
					comp.getSubmodelModifiable().getComponents().get(innerWire.pin1.compId).getPin(innerWire.pin1.pinName),
					comp.getSubmodelModifiable().getComponents().get(innerWire.pin2.compId).getPin(innerWire.pin2.pinName), innerWire.path);
		}
	}
}
