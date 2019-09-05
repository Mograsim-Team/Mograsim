package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.serializing.LogicModelParams.ComponentParams;
import net.mograsim.logic.model.serializing.LogicModelParams.WireParams;
import net.mograsim.logic.model.serializing.LogicModelParams.WireParams.PinParams;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.logic.model.util.Version;

public class LogicModelSerializer
{
	public static final Version CURRENT_JSON_VERSION = Version.parseSemver("0.1.1");

	// convenience methods
	/**
	 * Like {@link #deserialize(LogicModelParams)}, but first reading the {@link LogicModelParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static LogicModelModifiable deserialize(String sourcePath) throws IOException
	{
		return deserialize(JsonHandler.readJson(sourcePath, LogicModelParams.class));
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LogicModelParams)}, but first reading the {@link LogicModelParams} from the given file
	 * path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void deserialize(LogicModelModifiable model, String sourcePath) throws IOException
	{
		deserialize(model, JsonHandler.readJson(sourcePath, LogicModelParams.class));
	}

	/**
	 * Like {@link #deserialize(LogicModelModifiable, LogicModelParams)}, but using a newly created {@link LogicModelModifiable}.
	 * 
	 * @author Daniel Kirschten
	 */
	public static LogicModelModifiable deserialize(LogicModelParams params)
	{
		LogicModelModifiable model = new LogicModelModifiable();
		deserialize(model, params);
		return model;
	}

	/**
	 * Like {@link #serialize(LogicModel)}, but instead of returning the generated {@link LogicModelParams} they are written to a file at the
	 * given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(LogicModel model, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(model), targetPath);
	}

	/**
	 * Like {@link #serialize(LogicModel, IdentifierGetter)}, but instead of returning the generated {@link LogicModelParams} they are written
	 * to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(LogicModel model, IdentifyParams idParams, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(model, idParams), targetPath);
	}

	/**
	 * {@link #serialize(LogicModel, IdentifierGetter)} using a default {@link IdentifierGetter} (see <code>IdentifierGetter</code>'s
	 * {@link IdentifierGetter#IdentifierGetter() default constructor})
	 * 
	 * @author Daniel Kirschten
	 */
	public static LogicModelParams serialize(LogicModel model)
	{
		return serialize(model, new IdentifyParams());
	}

	// "core" methods
	/**
	 * Deserializes components and wires from the specified {@link LogicModelParams} and adds them to the given {@link LogicModelModifiable}.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings("unused") // for ModelWire being created
	public static void deserialize(LogicModelModifiable model, LogicModelParams params)
	{
		Map<String, ModelComponent> componentsByName = model.getComponentsByName();
		ModelComponent[] components = new ModelComponent[params.components.length];
		for (int i = 0; i < components.length; i++)
		{
			ComponentParams compParams = params.components[i];
			components[i] = IndirectModelComponentCreator.createComponent(model, compParams.id, compParams.params, compParams.name);
			components[i].moveTo(compParams.pos.x, compParams.pos.y);
		}

		for (int i = 0; i < params.wires.length; i++)
		{
			WireParams wire = params.wires[i];
			new ModelWire(model, wire.name, componentsByName.get(wire.pin1.compName).getPin(wire.pin1.pinName),
					componentsByName.get(wire.pin2.compName).getPin(wire.pin2.pinName), wire.path);
		}
	}

	/**
	 * Returns {@link LogicModelModifiable}, which describe the components and wires in the given {@link LogicModel}. <br>
	 * Components are serialized in the following way: <br>
	 * If a component is a <code>SubmodelComponent</code> which has been deserialized, and it has an
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set (e.g. non-null; see
	 * {@link SubmodelComponentSerializer#deserialize(LogicModelModifiable, SubmodelComponentParams, String, String, JsonElement)
	 * SubmodelComponentSerializer.deserialize(...)}), this ID and the component's
	 * {@link DeserializedSubmodelComponent#paramsForSerializingOverride paramsForSerializingOverride} are written.<br>
	 * If this case doesn't apply (e.g. if the component is not a <code>SubmodelComponent</code>; or it is a <code>SubmodelComponent</code>,
	 * but hasn't been deserialized; or it has no {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride}
	 * set), the ID defined by <code>idGetter</code> and the params obtained by {@link ModelComponent#getParamsForSerializing() getParams()}
	 * are written.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	public static LogicModelParams serialize(LogicModel model, IdentifyParams idParams)
	{
		LogicModelParams modelParams = new LogicModelParams(CURRENT_JSON_VERSION);

		Map<String, ModelComponent> components = new HashMap<>(model.getComponentsByName());
		components.remove(SubmodelComponent.SUBMODEL_INTERFACE_NAME);
		Set<ComponentParams> componentsParams = new HashSet<>();
		for (ModelComponent component : components.values())
		{
			ComponentParams compParams = new ComponentParams();
			componentsParams.add(compParams);
			compParams.pos = new Point(component.getPosX(), component.getPosY());
			DeserializedSubmodelComponent innerCompCasted;
			if (component instanceof DeserializedSubmodelComponent
					&& (innerCompCasted = (DeserializedSubmodelComponent) component).idForSerializingOverride != null)
			{
				compParams.id = innerCompCasted.idForSerializingOverride;
				compParams.params = innerCompCasted.paramsForSerializingOverride;
			} else
			{
				compParams.id = component.getIDForSerializing(idParams);
				compParams.params = component.getParamsForSerializingJSON(idParams);
			}
			compParams.name = component.name;
		}
		modelParams.components = componentsParams.toArray(ComponentParams[]::new);
		Arrays.sort(modelParams.components, Comparator.comparing(c -> c.name));

		Collection<ModelWire> wires = model.getWiresByName().values();
		Set<WireParams> wiresParams = new HashSet<>();
		for (ModelWire innerWire : wires)
		{
			WireParams innerWireParams = new WireParams();
			wiresParams.add(innerWireParams);
			PinParams pin1Params = new PinParams(), pin2Params = new PinParams();

			pin1Params.pinName = innerWire.getPin1().name;
			pin1Params.compName = innerWire.getPin1().component.name;
			pin2Params.pinName = innerWire.getPin2().name;
			pin2Params.compName = innerWire.getPin2().component.name;
			innerWireParams.name = innerWire.name;
			innerWireParams.pin1 = pin1Params;
			innerWireParams.pin2 = pin2Params;
			innerWireParams.path = innerWire.getPath();
		}
		modelParams.wires = wiresParams.toArray(WireParams[]::new);
		Arrays.sort(modelParams.wires, Comparator.comparing(c -> c.name));

		return modelParams;
	}
}