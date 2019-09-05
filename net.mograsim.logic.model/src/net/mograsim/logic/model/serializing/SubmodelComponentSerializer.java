package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.MovablePin;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.logic.model.util.Version;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 * 
 * @author Fabian Stemmler
 * @author Daniel Kirschten
 */
public final class SubmodelComponentSerializer
{
	public static final Version JSON_VERSION_CURRENT_SERIALIZING = Version.parseSemver("0.1.5");
	public static final Version JSON_VERSION_LATEST_SUPPORTED_DESERIALIZING = Version.parseSemver("0.1.5");
	public static final Version JSON_VERSION_EARLIEST_WITH_USAGE_SERIALIZED = Version.parseSemver("0.1.5");
	// convenience methods

	/**
	 * Like {@link #deserialize(LogicModelModifiable, SubmodelComponentParams)}, but first reading the {@link SubmodelComponentParams} from
	 * the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class));
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String, JsonElement)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String name) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), name);
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)}, but first reading the
	 * {@link SubmodelComponentParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, String sourcePath, String name, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride) throws IOException
	{
		return deserialize(model, JsonHandler.readJson(sourcePath, SubmodelComponentParams.class), name, idForSerializingOverride,
				paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set and using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, SubmodelComponentParams params)
	{
		return deserialize(model, params, null, null, null);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)} using the default name.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, SubmodelComponentParams params, String idForSerializingOverride,
			JsonElement paramsForSerializingOverride)
	{
		return deserialize(model, params, null, idForSerializingOverride, paramsForSerializingOverride);
	}

	/**
	 * {@link #deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)} with no
	 * <code>idForSerializingOverride</code> set.
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponent deserialize(LogicModelModifiable model, SubmodelComponentParams params, String name)
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
	 * Like {@link #serialize(SubmodelComponent, IdentifierGetter)}, but instead of returning the generated {@link SubmodelComponentParams}
	 * they are written to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(SubmodelComponent comp, IdentifyParams idParams, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(comp, idParams), targetPath);
	}

	/**
	 * {@link #serialize(SubmodelComponent, IdentifierGetter)} using a default {@link IdentifierGetter} (see <code>IdentifierGetter</code>'s
	 * {@link IdentifierGetter#IdentifierGetter() default constructor})
	 * 
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponentParams serialize(SubmodelComponent comp)
	{
		return serialize(comp, new IdentifyParams());
	}

	// "core" methods
	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams} with the given name.
	 * <p>
	 * When serializing a <code>SubmodelComponent</code>, it is undesired for every subcomponent to be serialized with its complete inner
	 * structure. Instead, these sub-<code>SubmodelComponent</code>s should be serialized with the ID and params which were used to
	 * determine the <code>SubmodelComponentParams</code> defining the sub-<code>SubmodelComponent</code>. Because of this, it is possible
	 * to override the ID and params used in {@link #serialize(SubmodelComponent, IdentifierGetter) serialize(...)} to describe this
	 * subcomponent. See there for details.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings("unused") // for ModelWire being created
	public static SubmodelComponent deserialize(LogicModelModifiable model, SubmodelComponentParams params, String name,
			String idForSerializingOverride, JsonElement paramsForSerializingOverride)
	{
		Version version = params.version;
		if (version.compareTo(JSON_VERSION_LATEST_SUPPORTED_DESERIALIZING) > 0)
			throw new IllegalArgumentException("JSON version " + version + " not supported yet");
		boolean hasUsageSerialized = version.compareTo(JSON_VERSION_EARLIEST_WITH_USAGE_SERIALIZED) >= 0;
		DeserializedSubmodelComponent comp = new DeserializedSubmodelComponent(model, name, idForSerializingOverride,
				paramsForSerializingOverride);
		comp.setSubmodelScale(params.innerScale);
		comp.setSize(params.width, params.height);
		for (InterfacePinParams iPinParams : params.interfacePins)
			// TRISTATE because we don't have a better choice
			comp.addSubmodelInterface(new MovablePin(comp, iPinParams.name, iPinParams.logicWidth,
					hasUsageSerialized ? iPinParams.usage : PinUsage.TRISTATE, iPinParams.location.x, iPinParams.location.y));
		LogicModelModifiable submodelModifiable = comp.getSubmodelModifiable();
		LogicModelSerializer.deserialize(comp.getSubmodelModifiable(), params.submodel);
		comp.setSymbolRenderer(SubmodelComponentSnippetSuppliers.symbolRendererSupplier.getSnippetSupplier(params.symbolRendererSnippetID)
				.create(comp, params.symbolRendererParams));
		comp.setOutlineRenderer(SubmodelComponentSnippetSuppliers.outlineRendererSupplier
				.getSnippetSupplier(params.outlineRendererSnippetID).create(comp, params.outlineRendererParams));
		comp.setHighLevelStateHandler(SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier
				.getSnippetSupplier(params.highLevelStateHandlerSnippetID).create(comp, params.highLevelStateHandlerParams));
		return comp;
	}

	/**
	 * Returns {@link SubmodelComponentParams}, which describe this {@link SubmodelComponent}. <br>
	 * See {@link LogicModelSerializer#serialize(net.mograsim.logic.model.model.LogicModel, IdentifierGetter)
	 * LogicModelSerializer.serialize(...)} for how subcomponents are serialized.<br>
	 * CodeSnippets are serialized using the ID defined by <code>idGetter</code> and the params obtained by the respective
	 * <coce>getParamsForSerializing</code> methods ({@link Renderer#getParamsForSerializing()}).
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	public static SubmodelComponentParams serialize(SubmodelComponent comp, IdentifyParams idParams)
	{
		SubmodelComponentParams params = new SubmodelComponentParams(JSON_VERSION_CURRENT_SERIALIZING);
		params.innerScale = comp.getSubmodelScale();
		params.submodel = LogicModelSerializer.serialize(comp.submodel, idParams);

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
			iPinParams.usage = p.usage;
			i++;
		}
		params.interfacePins = iPins;
		Arrays.sort(params.interfacePins, Comparator.comparing(p -> p.name));

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