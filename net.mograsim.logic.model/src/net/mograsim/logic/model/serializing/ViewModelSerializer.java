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
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.serializing.ViewModelParams.ComponentParams;
import net.mograsim.logic.model.serializing.ViewModelParams.WireParams;
import net.mograsim.logic.model.serializing.ViewModelParams.WireParams.PinParams;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.logic.model.util.Version;

public class ViewModelSerializer
{
	public static final Version CURRENT_JSON_VERSION = Version.parseSemver("0.1.1");

	// convenience methods
	/**
	 * Like {@link #deserialize(ViewModelParams)}, but first reading the {@link ViewModelParams} from the given file path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static ViewModelModifiable deserialize(String sourcePath) throws IOException
	{
		return deserialize(JsonHandler.readJson(sourcePath, ViewModelParams.class));
	}

	/**
	 * Like {@link #deserialize(ViewModelModifiable, ViewModelParams)}, but first reading the {@link ViewModelParams} from the given file
	 * path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void deserialize(ViewModelModifiable model, String sourcePath) throws IOException
	{
		deserialize(model, JsonHandler.readJson(sourcePath, ViewModelParams.class));
	}

	/**
	 * Like {@link #deserialize(ViewModelModifiable, ViewModelParams)}, but using a newly created {@link ViewModelModifiable}.
	 * 
	 * @author Daniel Kirschten
	 */
	public static ViewModelModifiable deserialize(ViewModelParams params)
	{
		ViewModelModifiable model = new ViewModelModifiable();
		deserialize(model, params);
		return model;
	}

	/**
	 * Like {@link #serialize(ViewModel)}, but instead of returning the generated {@link ViewModelParams} they are written to a file at the
	 * given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(ViewModel model, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(model), targetPath);
	}

	/**
	 * Like {@link #serialize(ViewModel, IdentifierGetter)}, but instead of returning the generated {@link ViewModelParams} they are written
	 * to a file at the given path.
	 * 
	 * @author Daniel Kirschten
	 */
	public static void serialize(ViewModel model, IdentifierGetter idGetter, String targetPath) throws IOException
	{
		JsonHandler.writeJson(serialize(model, idGetter), targetPath);
	}

	/**
	 * {@link #serialize(ViewModel, IdentifierGetter)} using a default {@link IdentifierGetter} (see <code>IdentifierGetter</code>'s
	 * {@link IdentifierGetter#IdentifierGetter() default constructor})
	 * 
	 * @author Daniel Kirschten
	 */
	public static ViewModelParams serialize(ViewModel model)
	{
		return serialize(model, new IdentifierGetter());
	}

	// "core" methods
	/**
	 * Deserializes components and wires from the specified {@link ViewModelParams} and adds them to the given {@link ViewModelModifiable}.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	@SuppressWarnings("unused") // for GUIWire being created
	public static void deserialize(ViewModelModifiable model, ViewModelParams params)
	{
		Map<String, GUIComponent> componentsByName = model.getComponentsByName();
		GUIComponent[] components = new GUIComponent[params.components.length];
		for (int i = 0; i < components.length; i++)
		{
			ComponentParams compParams = params.components[i];
			components[i] = IndirectGUIComponentCreator.createComponent(model, compParams.id, compParams.params, compParams.name);
			components[i].moveTo(compParams.pos.x, compParams.pos.y);
		}

		for (int i = 0; i < params.wires.length; i++)
		{
			WireParams wire = params.wires[i];
			new GUIWire(model, wire.name, componentsByName.get(wire.pin1.compName).getPin(wire.pin1.pinName),
					componentsByName.get(wire.pin2.compName).getPin(wire.pin2.pinName), wire.path);
		}
	}

	/**
	 * Returns {@link ViewModelModifiable}, which describe the components and wires in the given {@link ViewModel}. <br>
	 * Components are serialized in the following way: <br>
	 * If a component is a <code>SubmodelComponent</code> which has been deserialized, and it has an
	 * {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride} set (e.g. non-null; see
	 * {@link SubmodelComponentSerializer#deserialize(ViewModelModifiable, SubmodelComponentParams, String, String, JsonElement)
	 * SubmodelComponentSerializer.deserialize(...)}), this ID and the component's
	 * {@link DeserializedSubmodelComponent#paramsForSerializingOverride paramsForSerializingOverride} are written.<br>
	 * If this case doesn't apply (e.g. if the component is not a <code>SubmodelComponent</code>; or it is a <code>SubmodelComponent</code>,
	 * but hasn't been deserialized; or it has no {@link DeserializedSubmodelComponent#idForSerializingOverride idForSerializingOverride}
	 * set), the ID defined by <code>idGetter</code> and the params obtained by {@link GUIComponent#getParamsForSerializing() getParams()}
	 * are written.
	 * 
	 * @author Fabian Stemmler
	 * @author Daniel Kirschten
	 */
	public static ViewModelParams serialize(ViewModel model, IdentifierGetter idGetter)
	{
		ViewModelParams modelParams = new ViewModelParams(CURRENT_JSON_VERSION);

		Map<String, GUIComponent> components = new HashMap<>(model.getComponentsByName());
		components.remove(SubmodelComponent.SUBMODEL_INTERFACE_NAME);
		Set<ComponentParams> componentsParams = new HashSet<>();
		for (GUIComponent component : components.values())
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
				compParams.id = idGetter.componentIDs.apply(component);
				compParams.params = component.getParamsForSerializing(idGetter);
			}
			compParams.name = component.name;
		}
		modelParams.components = componentsParams.toArray(ComponentParams[]::new);
		Arrays.sort(modelParams.components, Comparator.comparing(c -> c.name));

		Collection<GUIWire> wires = model.getWiresByName().values();
		Set<WireParams> wiresParams = new HashSet<>();
		for (GUIWire innerWire : wires)
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