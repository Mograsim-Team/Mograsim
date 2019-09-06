package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.LegacySubmodelComponentParams.LegacyInterfacePinParams;
import net.mograsim.logic.model.serializing.LegacySubmodelComponentParams.LegacySubmodelParameters;
import net.mograsim.logic.model.serializing.LegacySubmodelComponentParams.LegacySubmodelParameters.LegacyInnerComponentParams;
import net.mograsim.logic.model.serializing.LegacySubmodelComponentParams.LegacySubmodelParameters.LegacyInnerWireParams;
import net.mograsim.logic.model.serializing.LegacySubmodelComponentParams.LegacySubmodelParameters.LegacyInnerWireParams.LegacyInnerPinParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.util.JsonHandler;

/**
 * Creates {@link SubmodelComponent}s from {@link LegacySubmodelComponentParams}
 * 
 * @author Fabian Stemmler
 * @author Daniel Kirschten
 */
public final class LegacySubmodelComponentSerializer
{
	// convenience methods

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams)}, but first reading the
	 * {@link LegacySubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, LegacySubmodelComponentParams.class));
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, JsonElement)}, but first reading the
	 * {@link LegacySubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, LegacySubmodelComponentParams.class), idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String)}, but first reading the
	 * {@link LegacySubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String name) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, LegacySubmodelComponentParams.class), name);
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, String, JsonElement)}, but first reading the
	 * {@link LegacySubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String name, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, LegacySubmodelComponentParams.class), name, idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, LegacySubmodelComponentParams params)
	{
		return deserialize(model, params, null, null, null);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, String, JsonElement)} using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, LegacySubmodelComponentParams params,
			String idForSerializingOverride, JsonElement paramsForSerializingOverride)
	{
		return deserialize(model, params, null, idForSerializingOverride, paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, LegacySubmodelComponentParams params, String name)
	{
		return deserialize(model, params, name, null, null);
	}

	/**
	 * Like {@link #serialize(SubmodelComponent)}, but instead of returning the generated {@link LegacySubmodelComponentParams} they are
	 * written to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(SubmodelComponent comp, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(comp), targetPath);
	}

	/**
	 * Like {@link #serialize(SubmodelComponent, Function)}, but instead of returning the generated {@link LegacySubmodelComponentParams}
	 * they are written to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(SubmodelComponent comp, IdentifyParams idParams, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(comp, idParams), targetPath);
	}

	/**
	 * {@link #serialize(SubmodelComponent, Function)} using the default {@link IdentifyParams} (see <code>IdentifyParams</code>'s
	 * {@link IdentifyParams#IdentifyParams() default constructor})
	 * 
	 * @author Daniel Kirschten
	 */
	public static LegacySubmodelComponentParams serialize(SubmodelComponent comp)
	{
		return serialize(comp, new IdentifyParams());
	}

	// "core" methods
	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link LegacySubmodelComponentParams} with the given name.
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
	@SuppressWarnings("unused") // for ModelWire being created
	public static SubmodelComponent deserialize(LogicModelModifiable model, LegacySubmodelComponentParams params, String name,
			String idForSerializingOverride, JsonElement paramsForSerializingOverride)
	{
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model, name, idForSerializingOverride,
				paramsForSerializingOverride);
		comp.setSubmodelScale(params.submodel.innerScale);
		comp.setSize(params.width, params.height);
		for (LegacyInterfacePinParams iPinParams : params.interfacePins)
			// TRISTATE because we don't have a better choice
			comp.addSubmodelInterface(new MovablePin(model, comp, iPinParams.name, iPinParams.logicWidth, PinUsage.TRISTATE,
					iPinParams.location.x, iPinParams.location.y));
		LegacySubmodelParameters submodelParams = params.submodel;
		LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		Map<String, ModelComponent> componentsByName = submodelModifiable.getComponentsByName();
		ModelComponent[] components = new ModelComponent[submodelParams.subComps.length];
		for (int i = 0; i < components.length; i++)
		{
			LegacyInnerComponentParams cParams = submodelParams.subComps[i];
			components[i] = IndirectModelComponentCreator.createComponent(submodelModifiable, cParams.id, cParams.params, cParams.name);
			components[i].moveTo(cParams.pos.x, cParams.pos.y);
		}

		for (int i = 0; i < submodelParams.innerWires.length; i++)
		{
			LegacyInnerWireParams innerWire = submodelParams.innerWires[i];
			new ModelWire(submodelModifiable, innerWire.name, componentsByName.get(innerWire.pin1.compName).getPin(innerWire.pin1.pinName),
					componentsByName.get(innerWire.pin2.compName).getPin(innerWire.pin2.pinName), innerWire.path);
		}
		comp.setSymbolRenderer(SubmodelComponentSnippetSuppliers.symbolRendererSupplier.getSnippetSupplier(params.symbolRendererSnippetID)
				.create(comp, params.symbolRendererParams));
		comp.setOutlineRenderer(SubmodelComponentSnippetSuppliers.outlineRendererSupplier
				.getSnippetSupplier(params.outlineRendererSnippetID).create(comp, params.outlineRendererParams));
		comp.setHighLevelStateHandler(SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier
				.getSnippetSupplier(params.highLevelStateHandlerSnippetID).create(comp, params.highLevelStateHandlerParams));
		return comp;
	}

	/**
	 * Returns {@link LegacySubmodelComponentParams}, which describe this {@link SubmodelComponent}. <br>
	 * Subcomponents are serialized in the following way: <br>
	 * If a subcomponent is a <code>SubmodelComponent</code> which has been deserialized, and it has an
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set (e.g. non-null; see
	 * {@link #deserialize(LogicModelModifiable, LegacySubmodelComponentParams, String, String, JsonElement) deserialize(...)}), this ID and
	 * the component's {@link DeserializedSubmodelComponent#paramsForSerializingOverride paramsForSerializingOverride} are written.<br>
	 * If this case doesn't apply (e.g. if the subcomponent is not a <code>SubmodelComponent</code>; or it is a
	 * <code>SubmodelComponent</code>, but hasn't been deserialized; or it has no
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set), the ID defined by <code>idGetter</code>
	 * and the params obtained by {@link ModelComponent#getParamsForSerializing() getParams()} are written.<br>
	 * CodeSnippets are serialized using the ID defined by <code>idGetter</code> and the params obtained by the respective
	 * <coce>getParamsForSerializing</code> methods ({@link Renderer#getParamsForSerializing()}).
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	public static LegacySubmodelComponentParams serialize(SubmodelComponent comp, IdentifyParams idParams)
	{
		LegacySubmodelParameters submodelParams = new LegacySubmodelParameters();
		submodelParams.innerScale = comp.getSubmodelScale();

		Map<String, ModelComponent> components = new HashMap<>(comp.submodel.getComponentsByName());
		components.remove(SubmodelComponent.SUBMODEL_INTERFACE_NAME);
		LegacyInnerComponentParams[] componentParams = new LegacyInnerComponentParams[components.size()];
		int i1 = 0;
		for (ModelComponent innerComponent : components.values())
		{
			LegacyInnerComponentParams innerComponentParams = new LegacyInnerComponentParams();
			componentParams[i1] = innerComponentParams;
			innerComponentParams.pos = new Point(innerComponent.getPosX(), innerComponent.getPosY());
			innerComponentParams.id = innerComponent.getIDForSerializing(idParams);
			innerComponentParams.params = innerComponent.getParamsForSerializingJSON(idParams);
			innerComponentParams.name = innerComponent.name;
			i1++;
		}
		submodelParams.subComps = componentParams;

		Collection<ModelWire> wires = comp.submodel.getWiresByName().values();
		LegacyInnerWireParams wireParams[] = new LegacyInnerWireParams[wires.size()];
		i1 = 0;
		for (ModelWire innerWire : wires)
		{
			LegacyInnerWireParams innerWireParams = new LegacyInnerWireParams();
			wireParams[i1] = innerWireParams;
			LegacyInnerPinParams pin1Params = new LegacyInnerPinParams(), pin2Params = new LegacyInnerPinParams();

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

		LegacySubmodelComponentParams params = new LegacySubmodelComponentParams();
		params.submodel = submodelParams;

		params.width = comp.getWidth();
		params.height = comp.getHeight();

		LegacyInterfacePinParams[] iPins = new LegacyInterfacePinParams[comp.getPins().size()];
		int i = 0;
		for (Pin p : comp.getPins().values())
		{
			LegacyInterfacePinParams iPinParams = new LegacyInterfacePinParams();
			iPins[i] = iPinParams;
			iPinParams.location = p.getRelPos();
			iPinParams.name = p.name;
			iPinParams.logicWidth = p.logicWidth;
			i++;
		}
		params.interfacePins = iPins;

		Renderer symbolRenderer = comp.getSymbolRenderer();
		if (symbolRenderer != null)
		{
			params.symbolRendererSnippetID = symbolRenderer.getIDForSerializing(idParams);
			params.symbolRendererParams = symbolRenderer.getParamsForSerializingJSON(idParams);
		}

		Renderer outlineRenderer = comp.getOutlineRenderer();
		if (outlineRenderer != null)
		{
			params.outlineRendererSnippetID = outlineRenderer.getIDForSerializing(idParams);
			params.outlineRendererParams = outlineRenderer.getParamsForSerializingJSON(idParams);
		}

		HighLevelStateHandler highLevelStateHandler = comp.getHighLevelStateHandler();
		if (highLevelStateHandler != null)
		{
			params.highLevelStateHandlerSnippetID = highLevelStateHandler.getIDForSerializing(idParams);
			params.highLevelStateHandlerParams = highLevelStateHandler.getParamsForSerializingJSON(idParams);
		}

		return params;
	}
}