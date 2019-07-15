package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerComponentParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.SubmodelParameters.InnerWireParams.InnerPinParams;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;
import net.mograsim.logic.model.util.JsonHandler;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 * 
 * @author Fabian Stemmler
 * @author Daniel Kirschten
 */
public final class SubmodelComponentSerializer
{
	// convenience methods

	/**
	 * Like {@link #deserialize(ViewModelModifiable, SubmodelComponentParams)}, but first reading the {@link SubmodelComponentParams} from
	 * the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, String sourcePath) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class));
	}

	/**
	 * Like {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, JsonElement)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, String sourcePath, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * Like {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, String sourcePath, String name) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), name);
	}

	/**
	 * Like {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, String sourcePath, String name, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), name, idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, SubmodelComponentParams params)
	{
		return deserialize(model, params, null, null, null);
	}

	/**
	 * {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)} using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, SubmodelComponentParams params, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride)
	{
		return deserialize(model, params, null, idForSerializingOverride, paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(ViewModelModifiable model, SubmodelComponentParams params, String name)
	{
		return deserialize(model, params, name, null, null);
	}

	/**
	 * Like {@link #serialize(SubmodelComponent)}, but instead of returning the generated {@link SubmodelComponentParams} they are written
	 * to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(SubmodelComponent comp, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(comp), targetPath);
	}

	/**
	 * Like {@link #serialize(SubmodelComponent, Function)}, but instead of returning the generated {@link SubmodelComponentParams} they are
	 * written to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(SubmodelComponent comp, Function<GUIComponent, String> getIdentifier, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(comp, getIdentifier), targetPath);
	}

	/**
	 * {@link #serialize(SubmodelComponent, Function)} using <code>"class:"</code> concatenated with a component's complete (canonical)
	 * class name for the ID of a component.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponentParams serialize(SubmodelComponent comp)
	{
		return serialize(comp, c -> "class:" + c.getClass().getCanonicalName());
	}

	// "core" methods
	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams} with the given name.
	 * <p>
	 * When serializing a <code>SubmodelComponent</code>, it is undesired for every subcomponent to be serialized with its complete inner
	 * structure. Instead, these sub-<code>SubmodelComponent</code>s should be serialized with the ID and params which were used to
	 * determine the <code>SubmodelComponentParams</code> defining the sub-<code>SubmodelComponent</code>. Because of this, it is possible
	 * to override the ID and params used in {@link #serialize(SubmodelComponent, Function) serialize(...)} to describe this subcomponent.
	 * See there for details.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings("unused") // for GUIWire being created
	public static SubmodelComponent deserialize(ViewModelModifiable model, SubmodelComponentParams params, String name,
			String idForSerializingOverride, JsonElement paramsForSerializingOverride)
	{
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model, name, idForSerializingOverride,
				paramsForSerializingOverride);
		comp.setSubmodelScale(params.submodel.innerScale);
		comp.setOutlineRenderer(SubmodelComponentSnippetSuppliers.outlineRendererSupplier
				.getSnippetSupplier(params.outlineRendererSnippetID).create(comp, params.outlineRendererParams));
		comp.setSymbolRenderer(SubmodelComponentSnippetSuppliers.symbolRendererSupplier.getSnippetSupplier(params.symbolRendererSnippetID)
				.create(comp, params.symbolRendererParams));
		comp.setHighLevelStateHandler(SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier
				.getSnippetSupplier(params.highLevelStateHandlerSnippetID).create(comp, params.highLevelStateHandlerParams));
		comp.setSize(params.width, params.height);
		for (InterfacePinParams iPinParams : params.interfacePins)
			comp.addSubmodelInterface(
					new MovablePin(comp, iPinParams.name, iPinParams.logicWidth, iPinParams.location.x, iPinParams.location.y));
		SubmodelParameters submodelParams = params.submodel;
		ViewModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		Map<String, GUIComponent> componentsByName = submodelModifiable.getComponentsByName();
		GUIComponent[] components = new GUIComponent[submodelParams.subComps.length];
		for (int i = 0; i < components.length; i++)
		{
			InnerComponentParams cParams = submodelParams.subComps[i];
			components[i] = IndirectGUIComponentCreator.createComponent(submodelModifiable, cParams.id, cParams.params, cParams.name);
			components[i].moveTo(cParams.pos.x, cParams.pos.y);
		}

		for (int i = 0; i < submodelParams.innerWires.length; i++)
		{
			InnerWireParams innerWire = submodelParams.innerWires[i];
			new GUIWire(submodelModifiable, innerWire.name, componentsByName.get(innerWire.pin1.compName).getPin(innerWire.pin1.pinName),
					componentsByName.get(innerWire.pin2.compName).getPin(innerWire.pin2.pinName), innerWire.path);
		}
		return comp;
	}

	/**
	 * Returns {@link SubmodelComponentParams}, which describe this {@link SubmodelComponent}. <br>
	 * Subcomponents are serialized in the following way: <br>
	 * If a subcomponent is a <code>SubmodelComponent</code> which has been deserialized, and it has an
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set (e.g. non-null; see
	 * {@link #deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement) deserialize(...)}), this ID and the
	 * component's {@link DeserializedSubmodelComponent#paramsForSerializingOverride paramsForSerializingOverride} are written.<br>
	 * If this case doesn't apply (e.g. if the subcomponent is not a <code>SubmodelComponent</code>; or it is a
	 * <code>SubmodelComponent</code>, but hasn't been deserialized; or it has no
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set), the ID returned by
	 * <code>getIdentifier</code> and the params obtained by {@link GUIComponent#getParamsForSerializing() getParams()} are written.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponentParams serialize(SubmodelComponent comp, Function<GUIComponent, String> getIdentifier)
	{
		SubmodelParameters submodelParams = new SubmodelParameters();
		submodelParams.innerScale = comp.getSubmodelScale();

		Map<String, GUIComponent> components = new HashMap<>(comp.submodel.getComponentsByName());
		components.remove(SubmodelComponent.SUBMODEL_INTERFACE_NAME);
		InnerComponentParams[] componentParams = new InnerComponentParams[components.size()];
		int i1 = 0;
		for (GUIComponent innerComponent : components.values())
		{
			InnerComponentParams innerComponentParams = new InnerComponentParams();
			componentParams[i1] = innerComponentParams;
			innerComponentParams.pos = new Point(innerComponent.getPosX(), innerComponent.getPosY());
			DeserializedSubmodelComponent innerCompCasted;
			if (innerComponent instanceof DeserializedSubmodelComponent
					&& (innerCompCasted = (DeserializedSubmodelComponent) innerComponent).idForSerializingOverride != null)
			{
				innerComponentParams.id = innerCompCasted.idForSerializingOverride;
				innerComponentParams.params = innerCompCasted.paramsForSerializingOverride;
			} else
			{
				innerComponentParams.id = getIdentifier.apply(innerComponent);
				innerComponentParams.params = innerComponent.getParamsForSerializing();
			}
			innerComponentParams.name = innerComponent.name;
			i1++;
		}
		submodelParams.subComps = componentParams;

		Collection<GUIWire> wires = comp.submodel.getWiresByName().values();
		InnerWireParams wireParams[] = new InnerWireParams[wires.size()];
		i1 = 0;
		for (GUIWire innerWire : wires)
		{
			InnerWireParams innerWireParams = new InnerWireParams();
			wireParams[i1] = innerWireParams;
			InnerPinParams pin1Params = new InnerPinParams(), pin2Params = new InnerPinParams();

			pin1Params.pinName = innerWire.getPin1().name;
			pin1Params.compName = innerWire.getPin1().component.name;
			pin2Params.pinName = innerWire.getPin2().name;
			pin2Params.compName = innerWire.getPin2().component.name;
			innerWireParams.name = innerWire.name;
			innerWireParams.pin1 = pin1Params;
			innerWireParams.pin2 = pin2Params;
			innerWireParams.path = innerWire.getPath();
			i1++;
		}
		submodelParams.innerWires = wireParams;

		SubmodelComponentParams params = new SubmodelComponentParams();
		params.submodel = submodelParams;

		params.width = comp.getWidth();
		params.height = comp.getHeight();

		InterfacePinParams[] iPins = new InterfacePinParams[comp.getPins().size()];
		int i = 0;
		for (Pin p : comp.getPins().values())
		{
			InterfacePinParams iPinParams = new InterfacePinParams();
			iPins[i] = iPinParams;
			iPinParams.location = p.getRelPos();
			iPinParams.name = p.name;
			iPinParams.logicWidth = p.logicWidth;
			i++;
		}
		params.interfacePins = iPins;

		// TODO This code does not belong here
		if (comp instanceof SimpleRectangularSubmodelComponent)
		{
			SimpleRectangularSubmodelComponent compCasted = (SimpleRectangularSubmodelComponent) comp;

			SimpleRectangularLikeParams symbolRendererParams = new SimpleRectangularLikeParams();
			symbolRendererParams.centerText = compCasted.label;
			symbolRendererParams.centerTextHeight = SimpleRectangularSubmodelComponent.labelFontHeight;
			symbolRendererParams.horizontalComponentCenter = compCasted.getWidth() / 2;
			symbolRendererParams.pinLabelHeight = SimpleRectangularSubmodelComponent.pinNameFontHeight;
			symbolRendererParams.pinLabelMargin = SimpleRectangularSubmodelComponent.pinNameMargin;

			params.symbolRendererSnippetID = "simpleRectangularLike";
			params.symbolRendererParams = new Gson().toJsonTree(symbolRendererParams);
		}

		return params;
	}
}